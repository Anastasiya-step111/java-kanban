package ru.practicum.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

public class EpicHandler extends HttpHandler {
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            HttpMethod httpMethod;

            try {
                httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());
            } catch (IllegalArgumentException e) {
                sendResponse(exchange, "{\"error\":\"Method not allowed\"}", 405);
                return;
            }

            switch (httpMethod) {
                case GET -> {
                    if ("/epics".equals(path)) {
                        List<Epic> epics = taskManager.getAllEpics();
                        String json = GSON.toJson(epics);
                        sendText(exchange, json);
                        return;
                    }

                    if (Pattern.matches("/epics/\\d+", path)) {
                        int id = Integer.parseInt(path.split("/")[2]);
                        Epic epic = taskManager.getEpicById(id);
                        if (epic == null) {
                            sendNotFound(exchange);
                            return;
                        }
                        String json = GSON.toJson(epic);
                        sendText(exchange, json);
                        return;
                    }

                    if (Pattern.matches("/epics/\\d+/subtasks", path)) {
                        int id = Integer.parseInt(path.split("/")[2]);
                        Epic epic = taskManager.getEpicById(id);
                        if (epic == null) {
                            sendNotFound(exchange);
                            return;
                        }
                        List<Subtask> subtasks = epic.getSubtasks();
                        String json = GSON.toJson(subtasks);
                        sendText(exchange, json);
                        return;
                    }

                    sendResponse(exchange, "{\"error\":\"Not found\"}", 404);
                    return;
                }

                case POST -> {
                    if ("/epics".equals(path)) {
                        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Epic newEpic = GSON.fromJson(body, Epic.class);
                        Epic epic = taskManager.createEpic(newEpic);
                        int id = epic.getId();
                        sendResponse(exchange, "{\"id\":" + id + "}", 201);
                        return;
                    }

                    if (Pattern.matches("/epics/\\d+", path)) {
                        int id = Integer.parseInt(path.split("/")[2]);
                        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Epic epic = GSON.fromJson(body, Epic.class);
                        epic.setId(id);
                        if (!taskManager.updateEpic(epic)) {
                            sendNotFound(exchange);
                            return;
                        }
                        exchange.sendResponseHeaders(204, -1);
                        exchange.close();
                        return;
                    }

                    sendResponse(exchange, "{\"error\":\"Not found\"}", 404);
                    return;
                }

                case DELETE -> {
                    if ("/epics".equals(path)) {
                        taskManager.deleteAllEpics();
                        exchange.sendResponseHeaders(204, -1);
                        exchange.close();
                        return;
                    }

                    if (Pattern.matches("/epics/\\d+", path)) {
                        int id = Integer.parseInt(path.split("/")[2]);
                        if (!taskManager.deleteEpic(id)) {
                            sendNotFound(exchange);
                            return;
                        }
                        exchange.sendResponseHeaders(204, -1);
                        exchange.close();
                        return;
                    }

                    sendResponse(exchange, "{\"error\":\"Not found\"}", 404);
                    return;
                }

                default -> sendResponse(exchange, "{\"error\":\"Method not allowed\"}", 405);
            }
        } catch (Exception e) {
            sendServerError(exchange);
        }
    }
}