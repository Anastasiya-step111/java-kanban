package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.manager.InMemoryTaskManager;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.server.DurationAdapter;
import ru.practicum.server.LocalDateTimeAdapter;
import ru.practicum.server.handler.EpicHandler;
import com.google.gson.JsonObject;
import ru.practicum.server.handler.SubtaskHandler;

import java.time.format.DateTimeFormatter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubtaskHandlerIntegrationTest {

    private HttpServer server;
    private HttpClient client;
    private int port;
    TaskManager taskManager;
    private static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        port = server.getAddress().getPort();

        taskManager = new InMemoryTaskManager();
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/subtasks", new SubtaskHandler(taskManager));

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
    void shouldAddEpicAndSubtaskSuccessfully() throws IOException, InterruptedException {
        String epicJson = """
            {
              "title": "Учить английский",
              "description": "Очень страшная задача"
            }
            """;

        HttpRequest createEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> epicResponse = client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, epicResponse.statusCode());

        JsonObject responseJson = GSON.fromJson(epicResponse.body(), JsonObject.class);
        int epicId = responseJson.get("id").getAsInt();

        LocalDateTime startTime1 = LocalDateTime.of(2027, 1, 1, 1, 0);
        Duration duration1 = Duration.ofHours(1);

        String subtaskJson = String.format("""
            {
              "title": "Найти репетитора",
              "description": "Почитать отзывы",
              "epicId": %d,
              "status": "NEW",
              "startTime": "%s",
              "duration": %d
            }
            """,
                epicId,
                startTime1.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                duration1.toSeconds()
        );

        HttpRequest createSubtaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> subtaskResponse = client.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, subtaskResponse.statusCode());

        List<Epic> epics = taskManager.getAllEpics();
        assertEquals(1, epics.size());
        Epic createdEpic = epics.get(0);
        assertEquals("Учить английский", createdEpic.getTitle());

        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertEquals(1, subtasks.size());
        Subtask createdSubtask = subtasks.get(0);
        assertEquals("Найти репетитора", createdSubtask.getTitle());
        assertEquals(epicId, createdSubtask.getEpicId());
        assertEquals(startTime1, createdSubtask.getStartTime());

        List<Subtask> epicSubtasks = createdEpic.getSubtasks();
        assertEquals(1, epicSubtasks.size());
        assertEquals("Найти репетитора", epicSubtasks.get(0).getTitle());
    }

    @Test
    void shouldReturnEmptySubtasksListInitially() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body().trim());
    }

    @Test
    void shouldGetSubtaskById() throws Exception {
        // 1. Создаём эпик через HTTP
        String epicJson = """
        {"title": "Учить английский", "description": "Очень страшная задача"}
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

        // 2. Создаём подзадачу через HTTP
        LocalDateTime startTime = LocalDateTime.of(2027, 10, 1, 8, 0);
        String subtaskJson = String.format("""
        {
          "title": "Найти репетитора",
          "description": "Почитать отзывы",
          "epicId": %d,
          "status": "NEW",
          "startTime": "%s",
          "duration": %d
        }
        """, epicId, startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), Duration.ofHours(1).toSeconds());

        HttpResponse<String> subtaskResp = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/subtasks"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        int subtaskId = GSON.fromJson(subtaskResp.body(), JsonObject.class).get("id").getAsInt();

        // 3. Запрашиваем подзадачу
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/subtasks/" + subtaskId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String body = response.body();
        assertTrue(body.contains("\"id\":" + subtaskId));
        assertTrue(body.contains("\"title\":\"Найти репетитора\"")); // ← исправлено название!
        assertTrue(body.contains("\"epicId\":" + epicId));
        assertTrue(body.contains("2027-10-01T08:00"));
        assertTrue(body.contains("\"duration\":3600"));
    }
}
