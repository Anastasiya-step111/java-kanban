package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrioritizedHandlerIntegrationTest {

    private com.sun.net.httpserver.HttpServer server;
    private HttpClient client;
    private int port;
    private ru.practicum.manager.TaskManager taskManager;
    private static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(java.time.LocalDateTime.class, new ru.practicum.server.LocalDateTimeAdapter())
            .registerTypeAdapter(java.time.Duration.class, new ru.practicum.server.DurationAdapter())
            .create();

    @BeforeEach
    void startServer() throws IOException {
        server = com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress(0), 0);
        port = server.getAddress().getPort();

        taskManager = new ru.practicum.manager.InMemoryTaskManager();
        server.createContext("/tasks", new ru.practicum.server.handler.TaskHandler(taskManager));
        server.createContext("/epics", new ru.practicum.server.handler.EpicHandler(taskManager));
        server.createContext("/subtasks", new ru.practicum.server.handler.SubtaskHandler(taskManager));
        server.createContext("/prioritized", new ru.practicum.server.handler.PrioritizedHandler(taskManager));

        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(1);
        }
    }

    @Test
    void shouldReturnEmptyListWhenNoTasks() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body().trim());
    }

    @Test
    void shouldReturnTasksInPrioritizedOrder() throws Exception {
        createTask("Задача 3", "Последняя", "2027-01-03T10:00", 60);
        createTask("Задача 1", "Первая", "2027-01-01T08:00", 30);
        createTask("Задача 2", "Вторая", "2027-01-02T09:00", 45);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task[] tasks = GSON.fromJson(response.body(), Task[].class);
        assertEquals(3, tasks.length);

        assertEquals("2027-01-01T08:00:00",
                tasks[0].getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertEquals("2027-01-02T09:00:00",
                tasks[1].getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertEquals("2027-01-03T10:00:00",
                tasks[2].getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void shouldIncludeTasksAndSubtasksButNotEpics() throws Exception {
        createTask("Обычная задача", "Описание", "2027-01-01T10:00", 60);

        String epicJson = """
                {"title": "Эпик", "description": "Эпики не попадают в prioritized"}
                """;
        HttpResponse<String> epicResp = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/epics"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        int epicId = GSON.fromJson(epicResp.body(), JsonObject.class).get("id").getAsInt();

        String subtaskJson = String.format("""
                {
                  "title": "Подзадача",
                  "description": "Подзадачи попадают",
                  "epicId": %d,
                  "status": "NEW",
                  "startTime": "2027-01-02T10:00",
                  "duration": 30
                }
                """, epicId);
        client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/subtasks"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String body = response.body();
        assertTrue(body.contains("Обычная задача"));
        assertTrue(body.contains("Подзадача"));
        assertFalse(body.contains("Эпик"));

        Task[] tasks = GSON.fromJson(response.body(), Task[].class);
        assertEquals(2, tasks.length);

        List<String> titles = Arrays.stream(tasks)
                .map(Task::getTitle)
                .collect(Collectors.toList());
        assertTrue(titles.contains("Обычная задача"));
        assertTrue(titles.contains("Подзадача"));
        assertFalse(titles.contains("Эпик"));
    }

    @Test
    void shouldReturn405ForPostRequest() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/prioritized"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
        assertTrue(response.body().contains("Method not allowed"));
    }

    private void createTask(String title, String description, String startTime, int durationMinutes) throws Exception {
        String json;
        if (startTime != null) {
            json = String.format("""
                    {
                      "title": "%s",
                      "description": "%s",
                      "startTime": "%s",
                      "duration": %d
                    }
                    """, title, description, startTime, durationMinutes);
        } else {
            json = String.format("""
                    {
                      "title": "%s",
                      "description": "%s",
                      "duration": %d
                    }
                    """, title, description, durationMinutes);
        }

        client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/tasks"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
    }
}