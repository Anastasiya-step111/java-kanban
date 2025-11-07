package ru.practicum.server;

import com.sun.net.httpserver.HttpServer;
import ru.practicum.server.handler.EpicHandler;
import ru.practicum.server.handler.HistoryHandler;
import ru.practicum.server.handler.PrioritizedHandler;
import ru.practicum.server.handler.SubtaskHandler;
import ru.practicum.server.handler.TaskHandler;
import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/subtasks", new SubtaskHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на http://localhost:" + PORT);
    }

    public void stop() {

        server.stop(1);
    }

    public static void main(String[] args) {
        try {
            new HttpTaskServer().start();
        } catch (IOException e) {
            System.err.println("Не удалось запустить сервер: " + e.getMessage());
        }
    }
}

