package ru.practicum.manager;

import java.io.File;

public final class Managers {
    private Managers() {
    }

    public static TaskManager getDefault() {

        return new InMemoryTaskManager();
    }

    public static FileBackedTaskManager getFileBackedManager(File file) {
        return new FileBackedTaskManager(file);
    }

    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }
}
