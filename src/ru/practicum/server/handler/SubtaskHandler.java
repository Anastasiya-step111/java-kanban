package ru.practicum.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.manager.ManagerSaveException;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class SubtaskHandler extends HttpHandler {
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET" -> {
                    if ("/subtasks".equals(path)) {
                        List<Subtask> subtasks = taskManager.getAllSubtasks();
                        String json = GSON.toJson(subtasks);
                        sendText(exchange, json);
                        return;
                    }

                    if (path.matches("/subtasks/\\d+")) {
                        int id = Integer.parseInt(path.split("/")[2]);
                        Subtask subtask = taskManager.getSubtaskById(id);
                        if (subtask == null) {
                            sendNotFound(exchange);
                            return;
                        }
                        String json = GSON.toJson(subtask);
                        sendText(exchange, json);
                        return;
                    }

                    sendResponse(exchange, "{\"error\":\"Not found\"}", 404);
                }

                case "POST" -> {
                    if ("/subtasks".equals(path)) {
                        try {
                            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                            Subtask newSubtask = GSON.fromJson(body, Subtask.class);
                            Subtask subtask = taskManager.createSubtask(newSubtask);
                            int id = subtask.getId();

                            sendResponse(exchange, GSON.toJson(Map.of("id", id)), 201);
                            return;
                        } catch (ManagerSaveException e) {
                            sendHasInteractions(exchange);
                            return;
                        }
                    }

                    if (path.matches("/subtasks/\\d+")) {
                        try {
                            int id = Integer.parseInt(path.split("/")[2]);
                            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                            Subtask subtask = GSON.fromJson(body, Subtask.class);
                            subtask.setId(id);
                            boolean updated = taskManager.updateSubtask(subtask, id);

                            if (!updated) {
                                if (taskManager.getSubtaskById(id) == null) {
                                    sendNotFound(exchange);
                                } else {
                                    sendHasInteractions(exchange);
                                }
                                return;
                            }

                            exchange.sendResponseHeaders(204, -1);
                            exchange.close();
                            return;
                        } catch (ManagerSaveException e) {
                            sendHasInteractions(exchange);
                            return;
                        }
                    }

                    sendResponse(exchange, "{\"error\":\"Not found\"}", 404);
                }

                case "DELETE" -> {
                    if ("/subtasks".equals(path)) {
                        taskManager.deleteAllSubtasks();
                        exchange.sendResponseHeaders(204, -1);
                        exchange.close();
                        return;
                    }

                    if (path.matches("/subtasks/\\d+")) {
                        int id = Integer.parseInt(path.split("/")[2]);
                        if (!taskManager.removeSubtaskById(id)) {
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