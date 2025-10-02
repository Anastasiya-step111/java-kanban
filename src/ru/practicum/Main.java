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
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        File tempFile = File.createTempFile("test", ".csv");
        TaskManager manager = Managers.getFileBackedManager(tempFile);
        HistoryManager historyManager = manager.getHistoryManager();

        Task task1 = manager.createTask(new Task("Купить продукты", "Хлеб яйца масло", manager,
                Status.NEW));
        Task task2 = manager.createTask(new Task("Постирать вещи", "Разделить по цветам", manager,
                Status.NEW));

        Epic epic1 = manager.createEpic(new Epic("Учить английский", "Очень страшная задача", manager,
                Status.NEW));
        Epic epic2 = manager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", manager,
                Status.NEW));
        Subtask subtask1 = manager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы",
                manager, epic1.getId(), Status.NEW));
        Subtask subtask2 = manager.createSubtask(new Subtask("Разбираться в IDEA",
                "Переводить все встреченные слова", manager, epic1.getId(), Status.NEW));
        Subtask subtask3 = manager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", manager, epic2.getId(), Status.NEW));

        System.out.println("Создано " + manager.getAllTasks().size() + " задач.\n" +
                "Создано " + manager.getAllEpics().size() + " эпиков.\n" +
                "Создано " + manager.getAllSubtasks().size() + " подзадач.");

        TaskManager manager2 = Managers.getFileBackedManager(tempFile);

        System.out.println("В менеджере2 хранится: " + manager.getAllTasks().size() + " задач.\n" +
                + manager.getAllEpics().size() + " эпиков.\n" +
                + manager.getAllSubtasks().size() + " подзадач.");

    }
}