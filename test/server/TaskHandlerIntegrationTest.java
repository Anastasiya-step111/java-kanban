package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sun.net.httpserver.HttpServer;
import ru.practicum.manager.InMemoryTaskManager;
import ru.practicum.manager.TaskManager;
import ru.practicum.server.DurationAdapter;
import ru.practicum.server.LocalDateTimeAdapter;
import ru.practicum.server.handler.TaskHandler;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import com.google.gson.JsonObject;

public class TaskHandlerIntegrationTest {

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
        server.createContext("/tasks", new TaskHandler(taskManager));

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
                .uri(URI.create("http://localhost:" + port + "/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body().trim());
    }

    @Test
    void shouldCreateAndRetrieveTask() throws Exception {
        String taskJson = """
                {
                  "title": "Test Task",
                  "description": "Test Description",
                  "startTime": "2025-11-08T14:30:00",
                  "duration": 60
                }
                """;

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode());
    }

    @Test
    void shouldDeleteTask() throws Exception {
        String taskJson = """
            { "title": "To delete", "description": "", "startTime": "2025-11-08T15:00:00", "duration": 30 }
            """;

        HttpResponse<String> postResponse = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/tasks"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(201, postResponse.statusCode());

        JsonObject responseJson = GSON.fromJson(postResponse.body(), JsonObject.class);
        int taskId = responseJson.get("id").getAsInt();

        HttpResponse<String> deleteResponse = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/tasks/" + taskId))
                        .DELETE()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(204, deleteResponse.statusCode());

        HttpResponse<String> getResponse = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/tasks/" + taskId))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, getResponse.statusCode());
    }

    @Test
    void shouldDeleteAllTasks() throws Exception {
        String task1 = "{ \"title\": \"T1\", \"description\": \"\", \"startTime\": \"2025-11-08T16:00:00\", \"duration\": 10 }";
        String task2 = "{ \"title\": \"T2\", \"description\": \"\", \"startTime\": \"2025-11-08T16:30:00\", \"duration\": 10 }";

        client.send(
                HttpRequest.newBuilder().uri(URI.create("http://localhost:" + port + "/tasks"))
                        .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(task1)).build(),
                HttpResponse.BodyHandlers.ofString()
        );
        client.send(
                HttpRequest.newBuilder().uri(URI.create("http://localhost:" + port + "/tasks"))
                        .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(task2)).build(),
                HttpResponse.BodyHandlers.ofString()
        );

        HttpResponse<String> deleteResponse = client.send(
                HttpRequest.newBuilder().uri(URI.create("http://localhost:" + port + "/tasks")).DELETE().build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(204, deleteResponse.statusCode());

        HttpResponse<String> getResponse = client.send(
                HttpRequest.newBuilder().uri(URI.create("http://localhost:" + port + "/tasks")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode());
        assertEquals("[]", getResponse.body().trim());
    }
}