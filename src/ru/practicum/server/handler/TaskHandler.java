package ru.practicum.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.manager.ManagerSaveException;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET" -> {
                    if ("/tasks".equals(path)) {
                        List<Task> tasks = taskManager.getAllTasks();
                        String json = GSON.toJson(tasks);
                        sendText(exchange, json);
                        return;
                    }

                    if (path.matches("/tasks/\\d+")) {
                        int id = Integer.parseInt(path.split("/")[2]);
                        Task task = taskManager.getTaskById(id);
                        if (task == null) {
                            sendNotFound(exchange);
                            return;
                        }
                        String json = GSON.toJson(task);
                        sendText(exchange, json);
                        return;
                    }

                    sendResponse(exchange, "{\"error\":\"Not found\"}", 404);
                }

                case "POST" -> {
                    try {
                        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Task newTask = GSON.fromJson(body, Task.class);
                        Task task = taskManager.createTask(newTask);
                        int id = task.getId();

                        sendResponse(exchange, GSON.toJson(Map.of("id", id)), 201);
                        return;
                    } catch (ManagerSaveException e) {
                        sendHasOverlaps(exchange);
                        return;
                    }
                }

                case "DELETE" -> {
                    if ("/tasks".equals(path)) {
                        taskManager.deleteAllTasks();
                        exchange.sendResponseHeaders(204, -1);
                        exchange.close();
                        return;
                    }

                    if (Pattern.matches("/tasks/\\d+", path)) {
                        int id = Integer.parseInt(path.split("/")[2]);
                        if (!taskManager.deleteTask(id)) {
                            sendNotFound(exchange);
                            return;
                        }
                        exchange.sendResponseHeaders(204, -1);
                        exchange.close();
                        return;
                    }
                    sendResponse(exchange, "{\"error\":\"Not found\"}", 404);
                }

                default -> sendResponse(exchange, "{\"error\":\"Method not allowed\"}", 405);
            }
        } catch (Exception e) {
            sendServerError(exchange);
        }
    }
}