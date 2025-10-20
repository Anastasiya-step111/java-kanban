package ru.practicum.model;

import ru.practicum.CustomDateTimeFormatter;
import ru.practicum.manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Subtask> subtasks;

    public Epic(String title, String description, TaskManager taskManager, Status status) {
        super(title, description, taskManager, status, null, null);
        this.subtasks = new ArrayList<>();
    }

    @Override
    public LocalDateTime getStartTime() {
        if (subtasks.isEmpty()) {
            return null;
        }
        
        return subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public Duration getDuration() {
        if (subtasks.isEmpty()) {
            return null;
        }
        long totalMinutes = subtasks.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .mapToLong(Duration::toMinutes)
                .sum();
        return Duration.ofMinutes(totalMinutes);
    }

    @Override
    public LocalDateTime getEndTime() {
        if (subtasks.isEmpty()) {
            return null;
        }
        return subtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    public List<Subtask> getSubtasks() {

        return subtasks;
    }

    public void addSubtask(Object subtask) {
        if (subtask == this) {
            throw new IllegalArgumentException("Нельзя добавить эпик как подзадачу самому себе");
        }

        if (!(subtask instanceof Subtask)) {
            throw new IllegalArgumentException("Можно добавлять только подзадачи");
        }

        Subtask task = (Subtask) subtask;

        if (!subtasks.contains(task)) {
            subtasks.add(task);
        }
    }

    public void removeSubtask(Subtask subtask) {

        subtasks.remove(subtask);
    }

    @Override
    public String toString() {
        return String.format("Эпик №%d: %s\n" +
                        "Статус: %s\n" +
                        "Описание: %s\n" +
                        "Начало: %s\n" +
                        "Длительность: %d ч. %d мин.\n" +
                        "Количество подзадач: %d",

                getId(), getTitle(), getStatus(), getDescription(),

                getStartTime() != null ? getStartTime().format(CustomDateTimeFormatter.DATE_TIME_FORMATTER) : "Не задано",

                getDuration() != null ? getDuration().toHours() : 0,
                getDuration() != null ? getDuration().toMinutesPart() : 0,

                getSubtasks().size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Epic epic = (Epic) o;

        return super.equals(o) &&
                Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toCSVStr() {
        String startTimeStr = (getStartTime() != null) ? getStartTime().toString() : "";
        String durationStr = (getDuration() != null) ? String.valueOf(getDuration().toMinutes()) : "";
        return String.format("%d,%s,%s,%s,%s,%s,%s", getId(), getType(), getTitle(), getStatus(),
                getDescription(), startTimeStr, durationStr);
    }
}
