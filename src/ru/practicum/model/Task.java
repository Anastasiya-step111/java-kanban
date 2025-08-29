package ru.practicum.model;

import ru.practicum.manager.TaskManager;

import java.util.Objects;

public class Task {
    private String title;
    private String description;
    private int id;
    private Status status;

    protected TaskManager taskManager;

    public Task(String title, String description, TaskManager taskManager, Status status) {
        this.title = title;
        this.description = description;
        this.taskManager = taskManager;
        this.status = status;
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

    @Override
    public String toString() {
        return String.format("Задача №%d: %s\n" +
                        "Статус: %s\n" +
                        "Описание: %s",
                getId(), getTitle(), getStatus(), getDescription());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Проверка на идентичность
        if (o == null || getClass() != o.getClass()) return false; // Проверка на null и тип

        Task task = (Task) o; // Приведение типа

        // Сравнение всех значимых полей
        return id == task.id &&
                status == task.status &&
                Objects.equals(title, task.title) &&
                Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status);
    }
}


