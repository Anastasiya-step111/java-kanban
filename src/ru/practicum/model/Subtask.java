package ru.practicum.model;

import ru.practicum.CustomDateTimeFormatter;
import ru.practicum.manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, TaskManager taskManager, int epicId, Status status,
                   LocalDateTime startTime, Duration duration) {
        super(title, description, taskManager, status, startTime, duration);
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
                        "Начало: %s\n" +
                        "Длительность: %d ч. %d мин.\n" +
                        "Принадлежит эпику №%d\n",
                getId(), getTitle(), getStatus(), getDescription(),

                getStartTime() != null ? getStartTime().format(CustomDateTimeFormatter.DATE_TIME_FORMATTER) : "Не задано",

                getDuration() != null ? getDuration().toHours() : 0,
                getDuration() != null ? getDuration().toMinutesPart() : 0,

                getEpicId());
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

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toCSVStr() {
        String startTimeStr = (getStartTime() != null) ? getStartTime().toString() : "";
        String durationStr = (getDuration() != null) ? String.valueOf(getDuration().toMinutes()) : "";
        return String.format("%d,%s,%s,%s,%s,%d", getId(), getType(), getTitle(), getStatus(), getDescription(),
                startTimeStr, durationStr, getEpicId());
    }
}
