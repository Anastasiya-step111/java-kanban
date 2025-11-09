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
import ru.practicum.server.DurationAdapter;
import ru.practicum.server.LocalDateTimeAdapter;
import ru.practicum.server.handler.EpicHandler;

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

public class EpicHandlerIntegrationTest {

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
    void shouldAddEpicSuccessfully() throws IOException, InterruptedException {
        String json = """
            {
              "title": "Учить английский",
              "description": "Очень страшная задача"
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        // 3. Проверяем через менеджер (допустимо, потому что taskManager — тот же, что в сервере)
        List<Epic> epics = taskManager.getAllEpics();
        assertEquals(1, epics.size());
        assertEquals("Учить английский", epics.get(0).getTitle());
    }

    @Test
    void shouldReturnEmptyEpicsListInitially() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body().trim());
    }

    @Test
    void shouldDeleteEpic() throws Exception {
        client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/epics"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("{ \"title\": \"To delete\" }"))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        HttpResponse<String> deleteResp = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/epics/1"))
                        .DELETE()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(204, deleteResp.statusCode());

        HttpResponse<String> getResp = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/epics/1"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, getResp.statusCode());
        assertTrue(getResp.body().contains("Not found"));
    }

    @Test
    void shouldReturn404ForInvalidPath() throws Exception {
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/epics/abc"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("Not found"));
    }

    @Test
    void shouldReturn405ForUnsupportedMethod() throws Exception {
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/epics"))
                        .method("PUT", HttpRequest.BodyPublishers.noBody()) // не поддерживается
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(405, response.statusCode());
        assertTrue(response.body().contains("Method not allowed"));
    }
}