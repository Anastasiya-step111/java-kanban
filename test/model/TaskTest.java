package model;

import ru.practicum.manager.HistoryManager;
import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Status;
import ru.practicum.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class TaskTest {
    TaskManager manager = Managers.getDefault();
    HistoryManager historyManager = manager.getHistoryManager();
    Task task1;
    Task task2;
    Task task3;

    @BeforeEach
    public void beforeEach() {

        task1 = manager.createTask(new Task("Купить продукты", "Хлеб яйца масло", manager, Status.NEW));
        task2 = manager.createTask(new Task("Постирать вещи", "Разделить по цветам", manager, Status.NEW));
        task3 = manager.createTask(new Task("Купить продукты", "Хлеб яйца масло", manager, Status.NEW));

    }

    @Test
    void shouldReturnCorrectTitle() {
        assertEquals("Купить продукты", task1.getTitle(), "Название задачи искажается при методе getTitle");
    }

    @Test
    void shouldReturnCorrectDescription() {
        assertEquals("Хлеб яйца масло", task1.getDescription(), "Описание искажается при методе getDescription");
    }

    @Test
    void shouldReturnCorrectId() {
        int originalId = task1.getId();
        Task retrievedTask = manager.getTaskById(originalId);
        int retrievedId = retrievedTask.getId();
        assertEquals(originalId, retrievedId, "ID не совпадает при получении через менеджер");
    }

    @Test
    void shouldReturnCorrectStatus() {
        Status originalStatus = task1.getStatus();
        int originalId = task1.getId();
        Task retrievedTask = manager.getTaskById(originalId);
        Status retrievedStatus = retrievedTask.getStatus();
        assertEquals(originalStatus, retrievedStatus, "model.Status не совпадает при получении через менеджер");
    }

    @Test
    void shouldCorrectEquals() {
        assertTrue(task1.equals(task1), "Задача не равна самой себе");
        assertTrue(task1.equals(task3), "Не обнаружены равные объекты");
        assertFalse(task1.equals(task2), "Обнаружены равные объекты при разных параметрах");
        assertFalse(task1.equals(null), "Равенство с null должно возвращать false");
        assertFalse(task1.equals("string"), "Равенство с другим типом должно возвращать false");
    }

    @Test
    void shouldCorrectHashCode() {
        if (task1.equals(task3)) {
            assertEquals(task1.hashCode(), task3.hashCode(), "Нарушение контракта: равные объекты имеют разные хеш-коды");
        }
    }

    @Test
    void testEqualsById() {
        assertEquals(task1.getId(), task3.getId(), "ID одинаковых экземпляров класса model.Task должны совпадать");
        assertTrue(task1.equals(task3), "Задачи с одинаковым ID должны быть равны");
    }

    @Test
    void testUpdateTask() {
        int taskId = task1.getId();
        String originalTitle = task1.getTitle();
        String originalDescription = task1.getDescription();
        Status originalStatus = task1.getStatus();

        String newTitle = "Купить продукты и приготовить ужин";
        String newDescription = "Хлеб, яйца, масло, овощи";
        Status newStatus = Status.IN_PROGRESS;

        Task updatedTask = new Task(newTitle, newDescription, manager, newStatus);
        updatedTask.setId(taskId);

        manager.updateTask(updatedTask);

        Task updatedFromManager = manager.getTaskById(taskId);

        assertNotNull(updatedFromManager, "Задача должна существовать после обновления");
        assertEquals(updatedTask.getId(), updatedFromManager.getId(), "ID должен совпадать");
        assertEquals(newTitle, updatedFromManager.getTitle(), "Название должно быть обновлено");
        assertEquals(newDescription, updatedFromManager.getDescription(), "Описание должно быть обновлено");
        assertEquals(newStatus, updatedFromManager.getStatus(), "Статус должен быть обновлен");

        assertNotEquals(originalTitle, updatedFromManager.getTitle(), "Исходное название должно отличаться");
        assertNotEquals(originalDescription, updatedFromManager.getDescription(), "Исходное описание должно отличаться");
        assertNotEquals(originalStatus, updatedFromManager.getStatus(), "Исходный статус должен отличаться");
    }

    @Test
    void testDeleteTask() {
        int taskId = task1.getId();
        List<Task> allTasks = manager.getAllTasks();

        boolean taskExists = false;
        for (Task task : allTasks) {
            if (task.getId() == taskId) {
                taskExists = true;
                break;
            }
        }
        assertTrue(taskExists, "Задача должна существовать до удаления");

        int initialTaskCount = allTasks.size();

        manager.deleteTask(taskId);

        List<Task> updatedTasks = manager.getAllTasks();

        boolean taskStillExists = false;
        for (Task task : updatedTasks) {
            if (task.getId() == taskId) {
                taskStillExists = true;
                break;
            }
        }
        assertFalse(taskStillExists, "Задача должна быть удалена");
        assertEquals(initialTaskCount - 1, updatedTasks.size(), "Количество задач должно уменьшиться на 1");
        manager.deleteTask(9999); // произвольный несуществующий ID
        assertEquals(initialTaskCount - 1, manager.getAllTasks().size(), "Удаление несуществующей задачи не должно менять размер");
    }

    @Test
    void testDeleteAllTasks() {
        int initialTaskCount = manager.getAllTasks().size();
        assertTrue(initialTaskCount > 0, "Изначально должно быть хотя бы несколько задач");

        manager.createTask(new Task("Новая задача 1", "Описание 1", manager, Status.NEW));
        manager.createTask(new Task("Новая задача 2", "Описание 2", manager, Status.IN_PROGRESS));

        int updatedTaskCount = manager.getAllTasks().size();
        assertTrue(updatedTaskCount > initialTaskCount, "Количество задач должно увеличиться после создания новых");

        manager.deleteAllTasks();

        assertTrue(manager.getAllTasks().isEmpty(), "После удаления всех задач список должен быть пустым");
        assertEquals(0, manager.getAllTasks().size(), "Размер списка задач должен быть равен 0");

        manager.deleteAllTasks();
        assertTrue(manager.getAllTasks().isEmpty(), "Повторное удаление не должно нарушить состояние");

        Task newTask = manager.createTask(new Task("Проверка после очистки", "Тест", manager, Status.NEW));
        assertFalse(manager.getAllTasks().isEmpty(), "После создания новой задачи список не должен быть пустым");
        assertEquals(1, manager.getAllTasks().size(), "Должна быть только одна задача");
        assertNotNull(manager.getTaskById(newTask.getId()), "Созданная задача должна быть доступна");
    }

    @Test
    void testSetTitle() {
        assertEquals("Купить продукты", task1.getTitle());

        int taskId = task1.getId();

        task1.setTitle("Купить лекарства");
        assertEquals("Купить лекарства", task1.getTitle());

        Task updatedTask = manager.getTaskById(taskId);
        assertNotNull(updatedTask);
        assertEquals("Купить лекарства", updatedTask.getTitle());
    }

    @Test
    void testSetDescription() {
        assertEquals("Хлеб яйца масло", task1.getDescription());
        int taskId = task1.getId();
        task1.setDescription("Молоко, сыр, хлеб");

        assertEquals("Молоко, сыр, хлеб", task1.getDescription());

        Task updatedTask = manager.getTaskById(taskId);
        assertEquals("Молоко, сыр, хлеб", updatedTask.getDescription());
    }

    @Test
    void testSetStatus() {
        assertEquals(Status.NEW, task1.getStatus());
        int taskId = task1.getId();
        task1.setStatus(Status.DONE);

        assertEquals(Status.DONE, task1.getStatus());

        Task updatedTask = manager.getTaskById(taskId);
        assertEquals(Status.DONE, updatedTask.getStatus());
    }

}

