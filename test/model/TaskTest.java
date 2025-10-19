package model;

import ru.practicum.manager.HistoryManager;
import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Status;
import ru.practicum.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class TaskTest {
    TaskManager manager = Managers.getDefault();
    HistoryManager historyManager = manager.getHistoryManager();
    Task task1;
    Task task2;
    Task task3;

    LocalDateTime startTime1 = LocalDateTime.of(2029, 10, 1, 10, 0);
    Duration duration1 = Duration.ofHours(1);

    LocalDateTime startTime2 = LocalDateTime.of(2029, 10, 3, 11, 0);
    Duration duration2 = Duration.ofHours(2);

    @BeforeEach
    public void beforeEach() {

        task1 = manager.createTask(new Task("Купить продукты", "Хлеб яйца масло", manager, Status.NEW,
                startTime1, duration1));
        task2 = manager.createTask(new Task("Постирать вещи", "Разделить по цветам", manager,
                Status.NEW, startTime2, duration2));
    }

    @Test
    void testGetTitle() {
        assertEquals("Купить продукты", task1.getTitle(), "Название задачи искажается при методе " +
                "getTitle");
    }

    @Test
    void testGetDescription() {
        assertEquals("Хлеб яйца масло", task1.getDescription(), "Описание искажается при методе " +
                "getDescription");
    }

    @Test
    void testGetId() {
        int originalId = task1.getId();
        Task retrievedTask = manager.getTaskById(originalId);
        int retrievedId = retrievedTask.getId();
        assertEquals(originalId, retrievedId, "ID не совпадает при получении через менеджер");
    }

    @Test
    void testGetStatus() {
        Status originalStatus = task1.getStatus();
        int originalId = task1.getId();
        Task retrievedTask = manager.getTaskById(originalId);
        Status retrievedStatus = retrievedTask.getStatus();
        assertEquals(originalStatus, retrievedStatus, "model.Status не совпадает при получении через менеджер");
    }

    @Test
    void testGetEquals() {
        assertTrue(task1.equals(task1), "Задача не равна самой себе");
        assertFalse(task1.equals(task2), "Обнаружены равные объекты при разных параметрах");
        assertFalse(task1.equals(null), "Равенство с null должно возвращать false");
        assertFalse(task1.equals("string"), "Равенство с другим типом должно возвращать false");
    }

    @Test
    void testUpdateTask() {
        assertTrue(manager.getPrioritizedTasks().contains(task1),
                "Задача task1 должна быть добавлена в prioritizedTasks до обновления");

        int taskId = task1.getId();
        String originalTitle = task1.getTitle();
        String originalDescription = task1.getDescription();
        Status originalStatus = task1.getStatus();
        LocalDateTime originalStartTime = task1.getStartTime();
        Duration originalDuration = task1.getDuration();

        String newTitle = "Купить продукты и приготовить ужин";
        String newDescription = "Хлеб, яйца, масло, овощи";
        Status newStatus = Status.IN_PROGRESS;
        LocalDateTime newStartTime = LocalDateTime.of(2029, 10, 7, 10, 0); // Новое время без конфликта
        Duration newDuration = Duration.ofHours(2);

        Task updatedTask = new Task(newTitle, newDescription, manager, newStatus, newStartTime,
                newDuration);
        updatedTask.setId(taskId);

        manager.updateTask(updatedTask);

        Task updatedFromManager = manager.getTaskById(taskId);

        assertNotNull(updatedFromManager, "Задача должна существовать после обновления");
        assertEquals(updatedTask.getId(), updatedFromManager.getId(), "ID должен совпадать");
        assertEquals(newTitle, updatedFromManager.getTitle(), "Название должно быть обновлено");
        assertEquals(newDescription, updatedFromManager.getDescription(), "Описание должно быть обновлено");
        assertEquals(newStatus, updatedFromManager.getStatus(), "Статус должен быть обновлен");
        assertEquals(newStartTime, updatedFromManager.getStartTime(), "Время начала должно быть обновлено");
        assertEquals(newDuration, updatedFromManager.getDuration(), "Длительность должна быть обновлена");

        assertNotEquals(originalTitle, updatedFromManager.getTitle(), "Исходное название должно отличаться");
        assertNotEquals(originalDescription, updatedFromManager.getDescription(), "Исходное описание " +
                "должно отличаться");
        assertNotEquals(originalStatus, updatedFromManager.getStatus(), "Исходный статус должен отличаться");
        assertNotEquals(originalStartTime, updatedFromManager.getStartTime(), "Исходное время должно отличаться");
        assertNotEquals(originalDuration, updatedFromManager.getDuration(), "Исходная длительность должна " +
                "отличаться");

        assertTrue(manager.getPrioritizedTasks().contains(updatedFromManager),
                "Задача должна быть добавлена в prioritizedTasks после обновления");
    }

    @Test
    void testDeleteTask() {
        assertTrue(manager.getPrioritizedTasks().contains(task1),
                "Задача task1 должна быть добавлена в prioritizedTasks до обновления");

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
        assertFalse(manager.getPrioritizedTasks().contains(task1),
                "Задача task1 должна быть удалена из prioritizedTasks");

        assertEquals(initialTaskCount - 1, updatedTasks.size(), "Количество задач должно уменьшиться " +
                "на 1");

    }

    @Test
    void testDeleteAllTasks() {
        int initialTaskCount = manager.getAllTasks().size();
        assertTrue(initialTaskCount > 0, "Изначально должно быть хотя бы несколько задач");

        LocalDateTime startTime3 = LocalDateTime.of(2029, 10, 5, 13, 0);  // 19 октября 2025, 13:00
        Duration duration3 = Duration.ofHours(1);

        LocalDateTime startTime4 = LocalDateTime.of(2029, 10, 9, 11, 0);  // 19 октября 2025, 11:00
        Duration duration4 = Duration.ofHours(2);

        manager.createTask(new Task("Новая задача 1", "Описание 1", manager, Status.NEW,
                startTime3, duration3));
        manager.createTask(new Task("Новая задача 2", "Описание 2", manager, Status.IN_PROGRESS,
                startTime4, duration4));

        int updatedTaskCount = manager.getAllTasks().size();
        assertTrue(updatedTaskCount > initialTaskCount, "Количество задач должно увеличиться после " +
                "создания новых");

        manager.deleteAllTasks();

        assertTrue(manager.getPrioritizedTasks().isEmpty(),
                "В prioritizedTasks не должно быть задач после удаления");

        assertTrue(
                manager.getPrioritizedTasks().stream().count() == 0,
                "Количество задач в prioritizedTasks должно быть равно нулю"
        );

        assertTrue(manager.getAllTasks().isEmpty(), "После удаления всех задач список должен быть пустым");
        assertEquals(0, manager.getAllTasks().size(), "Размер списка задач должен быть равен 0");

        manager.deleteAllTasks();
        assertTrue(manager.getAllTasks().isEmpty(), "Повторное удаление не должно нарушить состояние");

        LocalDateTime startTime6 = LocalDateTime.of(2029, 10, 11, 15, 0);  // 19 октября 2025, 13:00
        Duration duration6 = Duration.ofHours(1);

        Task newTask = manager.createTask(new Task("Проверка после очистки", "Тест", manager,
                Status.NEW, startTime6, duration6));
        assertFalse(manager.getAllTasks().isEmpty(), "После создания новой задачи список не должен быть " +
                "пустым");
        assertEquals(1, manager.getAllTasks().size(), "Должна быть только одна задача");
        assertNotNull(manager.getTaskById(newTask.getId()), "Созданная задача должна быть доступна");
    }

    @Test
    void testSetTitle() {
        assertEquals("Купить продукты", task1.getTitle(), "Изначально название таска должно совпадать");

        int taskId = task1.getId();

        task1.setTitle("Купить лекарства");
        assertEquals("Купить лекарства", task1.getTitle(), "Название должно измениться");

        Task updatedTask = manager.getTaskById(taskId);
        assertNotNull(updatedTask);
        assertEquals("Купить лекарства", updatedTask.getTitle(), "После переименования новый " +
                "экземпляр должен сохраниться");
    }

    @Test
    void testSetDescription() {
        assertEquals("Хлеб яйца масло", task1.getDescription(), "Изначально описание таска должно " +
                "совпадать");
        int taskId = task1.getId();
        task1.setDescription("Молоко, сыр, хлеб");

        assertEquals("Молоко, сыр, хлеб", task1.getDescription(), "Описание должно измениться");

        Task updatedTask = manager.getTaskById(taskId);
        assertEquals("Молоко, сыр, хлеб", updatedTask.getDescription(), "После изменения описания " +
                "экземпляр должен заменить предыдущий");
    }

    @Test
    void testSetStatus() {
        assertEquals(Status.NEW, task1.getStatus(), "Статус должен совпадать");
        int taskId = task1.getId();
        task1.setStatus(Status.DONE);

        assertEquals(Status.DONE, task1.getStatus(), "Статус должен измениться");

        Task updatedTask = manager.getTaskById(taskId);
        assertEquals(Status.DONE, updatedTask.getStatus(), "После изменение статуса экземпляр с новым статусом " +
                "должен заменить предыдущий");
    }

    @Test
    public void testCreateTask() {
        assertNotNull(task1);
        assertTrue(manager.getAllTasks().contains(task1));
        assertTrue(task1.getId() > 0);

        assertNotNull(task2);
        assertTrue(manager.getAllTasks().contains(task2));
        assertTrue(task2.getId() > 0);
        assertNotEquals(task1.getId(), task2.getId());

        assertEquals(2, manager.getAllTasks().size());

        LocalDateTime startTime7 = LocalDateTime.of(2029, 10, 13, 11, 0);  // 19 октября 2025, 11:00
        Duration duration7 = Duration.ofHours(2);

        Task newTask = manager.createTask(new Task("Купить продукты", "Молоко сыр творог",
                manager, Status.NEW, startTime7, duration7));
        assertNotSame(task1, newTask);
        assertEquals(3, manager.getAllTasks().size());
        assertTrue(manager.getPrioritizedTasks().contains(newTask),
                "Задача должна быть добавлена в prioritizedTasks");

    }
}


