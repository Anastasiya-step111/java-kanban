package ru.practicum.model;

import ru.practicum.manager.TaskManager;

import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, TaskManager taskManager, int epicId, Status status) {
        super(title, description, taskManager, status);
        this.epicId = epicId;
    }

    public int getEpicId() {

        return epicId;
    }

    public void setEpicId(int epicId) {
        boolean found = false;
        for (Epic epic : taskManager.getAllEpics()) {
            if (epic.getId() == epicId) {
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("model.Epic с указанным ID не существует");
        }

        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return String.format("Подзадача №%d: %s\n" +
                        "Статус: %s\n" +
                        "Описание: %s\n" +
                        "Принадлежит эпику №%d",
                getId(), getTitle(), getStatus(), getDescription(), getEpicId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subtask subtask = (Subtask) o;

        return super.equals(o) &&
                epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

}
