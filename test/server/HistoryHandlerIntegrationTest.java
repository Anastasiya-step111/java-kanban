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

import static org.junit.jupiter.api.Assertions.*;

public class HistoryHandlerIntegrationTest {

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
        server.createContext("/history", new ru.practicum.server.handler.HistoryHandler(taskManager));

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
    void shouldReturnEmptyHistoryInitially() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body().trim());
    }

    @Test
    void shouldReturnHistoryInCorrectOrder() throws Exception {
        String task1Json = """
                {
                  "title": "Задача 1",
                  "description": "Первая",
                  "duration": 60
                }
                """;
        HttpResponse<String> resp1 = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/tasks"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        int id1 = GSON.fromJson(resp1.body(), JsonObject.class).get("id").getAsInt();

        String task2Json = """
                {
                  "title": "Задача 2",
                  "description": "Вторая",
                  "duration": 60
                }
                """;
        HttpResponse<String> resp2 = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/tasks"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        int id2 = GSON.fromJson(resp2.body(), JsonObject.class).get("id").getAsInt();

        String task3Json = """
                {
                  "title": "Задача 3",
                  "description": "Третья",
                  "duration": 60
                }
                """;
        HttpResponse<String> resp3 = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/tasks"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(task3Json))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        int id3 = GSON.fromJson(resp3.body(), JsonObject.class).get("id").getAsInt();

        client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/tasks/" + id2))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/tasks/" + id1))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/tasks/" + id3))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task[] history = GSON.fromJson(response.body(), Task[].class);
        assertEquals(3, history.length);

        assertEquals("Задача 2", history[0].getTitle());
        assertEquals("Задача 1", history[1].getTitle());
        assertEquals("Задача 3", history[2].getTitle());
    }

    @Test
    void shouldMoveTaskToTheEndOnRepeatView() throws Exception {
        int id1 = createSimpleTask("Задача A", "A");
        int id2 = createSimpleTask("Задача B", "B");

        client.send(HttpRequest.newBuilder().uri(URI.create("http://localhost:" + port + "/tasks/" + id1)).GET().build(), HttpResponse.BodyHandlers.ofString());
        client.send(HttpRequest.newBuilder().uri(URI.create("http://localhost:" + port + "/tasks/" + id2)).GET().build(), HttpResponse.BodyHandlers.ofString());
        client.send(HttpRequest.newBuilder().uri(URI.create("http://localhost:" + port + "/tasks/" + id1)).GET().build(), HttpResponse.BodyHandlers.ofString());

        Task[] history = getHistory();
        assertEquals(2, history.length);
        assertEquals("Задача B", history[0].getTitle());
        assertEquals("Задача A", history[1].getTitle());
    }

    @Test
    void shouldNotIncludeDeletedTasksInHistory() throws Exception {
        int id1 = createSimpleTask("Удаляемая задача", "Будет удалена");
        int id2 = createSimpleTask("Сохраняемая задача", "Останется");

        client.send(HttpRequest.newBuilder().uri(URI.create("http://localhost:" + port + "/tasks/" + id1)).GET().build(), HttpResponse.BodyHandlers.ofString());
        client.send(HttpRequest.newBuilder().uri(URI.create("http://localhost:" + port + "/tasks/" + id2)).GET().build(), HttpResponse.BodyHandlers.ofString());

        client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/tasks/" + id1))
                        .DELETE()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        Task[] history = getHistory();
        assertEquals(2, history.length, "История должна содержать обе задачи");
        assertEquals("Удаляемая задача", history[0].getTitle(), "Удаляемая задача должна оставаться в истории");
    }

    @Test
    void shouldReturn405ForPostRequest() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/history"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
        assertTrue(response.body().contains("Method not allowed"));
    }

    private int createSimpleTask(String title, String description) throws Exception {
        String json = String.format("""
                {
                  "title": "%s",
                  "description": "%s",
                  "duration": 60
                }
                """, title, description);

        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/tasks"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        return GSON.fromJson(response.body(), JsonObject.class).get("id").getAsInt();
    }

    private Task[] getHistory() throws Exception {
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/history"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, response.statusCode());
        return GSON.fromJson(response.body(), Task[].class);
    }
}