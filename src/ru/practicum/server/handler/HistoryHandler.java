package ru.practicum.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends HttpHandler {
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        if ("GET".equals(h.getRequestMethod()) && "/history".equals(h.getRequestURI().getPath())) {
            List<Task> history = taskManager.getHistory();
            String json = GSON.toJson(history);
            sendText(h, json);
        } else {
            sendResponse(h, "{\"error\":\"Method not allowed\"}", 405);
        }
    }
}