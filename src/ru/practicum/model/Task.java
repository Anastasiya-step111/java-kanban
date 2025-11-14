package ru.practicum.model;

import com.google.gson.annotations.Expose;
import ru.practicum.CustomDateTimeFormatter;
import ru.practicum.manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    @Expose
    private String title;
    @Expose
    private String description;
    @Expose
    private int id;
    @Expose
    private Status status;
    @Expose
    private LocalDateTime startTime;
    @Expose
    private Duration duration;

    protected transient TaskManager taskManager;

    public Task() {

    }

    public Task(String title, String description, TaskManager taskManager, Status status,
                LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.taskManager = taskManager;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String title, String description, int id, TaskManager taskManager, Status status,
                LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.taskManager = taskManager;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public Status getStatus() {

        return status;
    }

    public void setStatus(Status status) {

        this.status = status;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return String.format("Задача №%d: %s\n" +
                        "Статус: %s\n" +
                        "Описание: %s\n" +
                        "Начало: %s\n" +
                        "Длительность: %d ч. %d мин.\n",

                getId(), getTitle(), getStatus(), getDescription(),

                startTime != null ? startTime.format(CustomDateTimeFormatter.DATE_TIME_FORMATTER) : "Не задано",

                duration != null ? duration.toHours() : 0,
                duration != null ? duration.toMinutesPart() : 0
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        return id == task.id &&
                status == task.status &&
                Objects.equals(title, task.title) &&
                Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status);
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public String toCSVStr() {
        String startTimeStr = (getStartTime() != null) ? getStartTime().toString() : "";
        String durationStr = (getDuration() != null) ? String.valueOf(getDuration().toMinutes()) : "";
        return String.format("%d,%s,%s,%s,%s,%s,%s", id, getType(), title, status, description,
                startTimeStr, durationStr);
    }

}


