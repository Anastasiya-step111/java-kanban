package ru.practicum;

import ru.practicum.manager.Managers;
import ru.practicum.manager.HistoryManager;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        HistoryManager historyManager = manager.getHistoryManager();

        // Создаем задачи
        Task task1 = new Task("Купить продукты", "Хлеб яйца масло", manager, Status.NEW);
        Task task2 = new Task("Постирать вещи", "Разделить по цветам", manager, Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        // Создаем эпик с подзадачами
        Epic epic1 = new Epic("Учить английский", "Очень страшная задача", manager, Status.NEW);
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Найти репетитора", "Почитать отзывы", manager, epic1.getId(), Status.NEW);
        Subtask subtask2 = new Subtask("Разбираться в IDEA", "Переводить все встреченные слова", manager, epic1.getId(), Status.NEW);
        Subtask subtask3 = new Subtask("Читать книги", "Выбрать литературу", manager, epic1.getId(), Status.NEW);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        // Создаем пустой эпик
        Epic epic2 = new Epic("Разобраться с ошибкой", "Исследовать проблему", manager, Status.NEW);
        manager.createEpic(epic2);

        // Первый запрос задач
        manager.getTaskById(task1.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());
        manager.getTaskById(task2.getId());
        printHistory(historyManager);

        // Второй запрос в другом порядке
        manager.getEpicById(epic2.getId());
        manager.getSubtaskById(subtask2.getId());
        manager.getTaskById(task1.getId());
        printHistory(historyManager);

        // Удаляем задачу из истории
        manager.deleteTask(task2.getId());
        printHistory(historyManager);

        // Удаляем эпик с подзадачами
        manager.deleteEpic(epic1.getId());
        printHistory(historyManager);
    }

    private static void printHistory(HistoryManager historyManager) {
        System.out.println("\n===== Текущая история =====");
        List<Task> history = historyManager.getHistory();
        for (Task task : history) {
            System.out.println(task);
        }
        System.out.println("Размер истории: " + history.size());
    }
}