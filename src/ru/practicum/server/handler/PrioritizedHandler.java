package ru.practicum.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        if ("GET".equals(h.getRequestMethod()) && "/prioritized".equals(h.getRequestURI().getPath())) {
            List<Task> prioritized = taskManager.getPrioritizedTasks();
            String json = GSON.toJson(prioritized);
            sendText(h, json);
        } else {
            sendResponse(h, "{\"error\":\"Method not allowed\"}", 405);
        }
    }
}