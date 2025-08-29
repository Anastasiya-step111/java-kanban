package ru.practicum;

import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Купить продукты", "Хлеб яйца масло", manager, Status.NEW);
        Task task2 = new Task("Постирать вещи", "Разделить по цветам", manager, Status.NEW);

        manager.createTask(task1);
        manager.createTask(task2);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        Epic epic1 = new Epic("Учить английский", "Очень страшная задача", manager, Status.NEW);
        manager.createEpic(epic1);
        manager.getEpicById(epic1.getId());

        Subtask subtask1 = new Subtask("Найти репетитора", "Почитать отзывы", manager,
                epic1.getId(), Status.NEW);
        Subtask subtask2 = new Subtask("Разбираться в IDEA", "Переводить все встреченные слова", manager,
                epic1.getId(), Status.NEW);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        epic1.getSubtasks();
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());

        Subtask subtaskUpdate1 = new Subtask("Найти репетитора", "Почитать отзывы", manager,
                epic1.getId(), Status.IN_PROGRESS);
        epic1.getSubtasks();
        manager.updateSubtask(subtaskUpdate1, subtask1.getId());
        epic1.getStatus();
        manager.updateEpic(epic1);

        Epic epic2 = new Epic("Разобраться с ошибкой по коробке", "Не горит", manager, Status.NEW);
        manager.createEpic(epic2);
        manager.getEpicById(epic2.getId());

        Subtask subtask3 = new Subtask("Изучить вопрос на драйв2", "Сделать список вариантов", manager,
                epic2.getId(), Status.NEW);
        manager.createSubtask(subtask3);
        epic2.getSubtasks();
        manager.getSubtaskById(subtask3.getId());

        printAllTasks(manager);

        manager.getEpicById(epic1.getId());
        manager.getEpicById(epic2.getId());

        manager.deleteTask(task2.getId());
        manager.deleteEpic(epic2.getId());

        printAllTasks(manager);

    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("\n===== Задачи =====");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("\n===== Эпики =====");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("\n===== Подзадачи =====");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("\n===== История =====");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}




