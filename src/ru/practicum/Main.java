package ru.practicum;

import ru.practicum.manager.Managers;
import ru.practicum.manager.HistoryManager;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException {
        File tempFile = File.createTempFile("test", ".csv");
        TaskManager manager = Managers.getFileBackedManager(tempFile);
        HistoryManager historyManager = manager.getHistoryManager();

        LocalDateTime startTime1 = LocalDateTime.of(2026, 7, 1, 8, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2026, 7, 3, 9, 0);
        Duration duration2 = Duration.ofHours(2);

        LocalDateTime startTime3 = LocalDateTime.of(2026, 7, 5, 11, 0);
        Duration duration3 = Duration.ofHours(1);

        LocalDateTime startTime4 = LocalDateTime.of(2026, 7, 7, 12, 0);
        Duration duration4 = Duration.ofHours(1);

        LocalDateTime startTime5 = LocalDateTime.of(2026, 7, 9, 13, 0);
        Duration duration5 = Duration.ofHours(2);

        Task task1 = manager.createTask(new Task("Купить продукты", "Хлеб яйца масло", manager,
                Status.NEW, startTime1, duration1));
        Task task2 = manager.createTask(new Task("Постирать вещи", "Разделить по цветам", manager,
                Status.NEW, startTime2, duration2));

        Epic epic1 = manager.createEpic(new Epic("Учить английский", "Очень страшная задача", manager,
                Status.NEW));
        Epic epic2 = manager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", manager,
                Status.NEW));
        Subtask subtask1 = manager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы",
                manager, epic1.getId(), Status.NEW, startTime3, duration3));
        Subtask subtask2 = manager.createSubtask(new Subtask("Разбираться в IDEA",
                "Переводить все встреченные слова", manager, epic1.getId(), Status.NEW, startTime4,
                duration4));
        Subtask subtask3 = manager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", manager, epic2.getId(), Status.NEW, startTime5, duration5));

        System.out.println("Создано " + manager.getAllTasks().size() + " задач.\n" +
                "Создано " + manager.getAllEpics().size() + " эпиков.\n" +
                "Создано " + manager.getAllSubtasks().size() + " подзадач.");

        TaskManager manager2 = Managers.getFileBackedManager(tempFile);

        System.out.println("В менеджере2 хранится: " + manager2.getAllTasks().size() + " задач.\n" +
                + manager2.getAllEpics().size() + " эпиков.\n" +
                + manager2.getAllSubtasks().size() + " подзадач.");

    }
}