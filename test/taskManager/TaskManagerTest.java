package taskManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.manager.HistoryManager;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @BeforeEach
    abstract void beforeEach();

    @Test
    public void testCreateTask() {
        LocalDateTime startTime1 = LocalDateTime.of(2029, 10, 1, 10, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2029, 10, 3, 11, 0);
        Duration duration2 = Duration.ofHours(2);

        Task task1 = taskManager.createTask(new Task("Купить продукты", "Хлеб яйца масло", taskManager, Status.NEW,
                startTime1, duration1));
        Task task2  = taskManager.createTask(new Task("Постирать вещи", "Разделить по цветам", taskManager,
                Status.NEW, startTime2, duration2));

        assertNotNull(task1);
        assertTrue(taskManager.getAllTasks().contains(task1));
        assertTrue(task1.getId() > 0);

        assertNotNull(task2);
        assertTrue(taskManager.getAllTasks().contains(task2));
        assertTrue(task2.getId() > 0);
        assertNotEquals(task1.getId(), task2.getId());

        assertEquals(2, taskManager.getAllTasks().size());

        LocalDateTime startTime3 = LocalDateTime.of(2029, 10, 13, 11, 0);  // 19 октября 2025, 11:00
        Duration duration3 = Duration.ofHours(2);

        Task newTask = taskManager.createTask(new Task("Купить продукты", "Молоко сыр творог",
                taskManager, Status.NEW, startTime3, duration3));
        assertNotSame(task1, newTask);
        assertEquals(3, taskManager.getAllTasks().size());
        assertTrue(taskManager.getPrioritizedTasks().contains(newTask),
                "Задача должна быть добавлена в prioritizedTasks");

    }

    @Test
    void testUpdateTask() {
        LocalDateTime startTime1 = LocalDateTime.of(2029, 10, 1, 10, 0);
        Duration duration1 = Duration.ofHours(1);

        Task task1 = taskManager.createTask(new Task("Купить продукты", "Хлеб яйца масло", taskManager, Status.NEW,
                startTime1, duration1));

        assertTrue(taskManager.getPrioritizedTasks().contains(task1),
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

        Task updatedTask = new Task(newTitle, newDescription, taskManager, newStatus, newStartTime,
                newDuration);
        updatedTask.setId(taskId);

        taskManager.updateTask(updatedTask);

        Task updatedFromManager = taskManager.getTaskById(taskId);

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

        assertTrue(taskManager.getPrioritizedTasks().contains(updatedFromManager),
                "Задача должна быть добавлена в prioritizedTasks после обновления");
    }

    @Test
    void testDeleteTask() {
        LocalDateTime startTime1 = LocalDateTime.of(2029, 10, 1, 10, 0);
        Duration duration1 = Duration.ofHours(1);

        Task task1 = taskManager.createTask(new Task("Купить продукты", "Хлеб яйца масло", taskManager, Status.NEW,
                startTime1, duration1));

        assertTrue(taskManager.getPrioritizedTasks().contains(task1),
                "Задача task1 должна быть добавлена в prioritizedTasks до обновления");

        int taskId = task1.getId();
        List<Task> allTasks = taskManager.getAllTasks();

        boolean taskExists = false;
        for (Task task : allTasks) {
            if (task.getId() == taskId) {
                taskExists = true;
                break;
            }
        }
        assertTrue(taskExists, "Задача должна существовать до удаления");

        int initialTaskCount = allTasks.size();

        taskManager.deleteTask(taskId);

        List<Task> updatedTasks = taskManager.getAllTasks();

        boolean taskStillExists = false;
        for (Task task : updatedTasks) {
            if (task.getId() == taskId) {
                taskStillExists = true;
                break;
            }
        }
        assertFalse(taskStillExists, "Задача должна быть удалена");
        assertFalse(taskManager.getPrioritizedTasks().contains(task1),
                "Задача task1 должна быть удалена из prioritizedTasks");

        assertEquals(initialTaskCount - 1, updatedTasks.size(), "Количество задач должно уменьшиться " +
                "на 1");

    }

    @Test
    void testDeleteAllTasks() {
        LocalDateTime startTime1 = LocalDateTime.of(2029, 10, 1, 10, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2029, 10, 3, 11, 0);
        Duration duration2 = Duration.ofHours(2);

        Task task1 = taskManager.createTask(new Task("Купить продукты", "Хлеб яйца масло", taskManager, Status.NEW,
                startTime1, duration1));
        Task task2  = taskManager.createTask(new Task("Постирать вещи", "Разделить по цветам", taskManager,
                Status.NEW, startTime2, duration2));

        int initialTaskCount = taskManager.getAllTasks().size();
        assertTrue(initialTaskCount > 0, "Изначально должно быть хотя бы несколько задач");

        LocalDateTime startTime3 = LocalDateTime.of(2029, 10, 5, 13, 0);  // 19 октября 2025, 13:00
        Duration duration3 = Duration.ofHours(1);

        LocalDateTime startTime4 = LocalDateTime.of(2029, 10, 9, 11, 0);  // 19 октября 2025, 11:00
        Duration duration4 = Duration.ofHours(2);

        taskManager.createTask(new Task("Новая задача 1", "Описание 1", taskManager, Status.NEW,
                startTime3, duration3));
        taskManager.createTask(new Task("Новая задача 2", "Описание 2", taskManager, Status.IN_PROGRESS,
                startTime4, duration4));

        int updatedTaskCount = taskManager.getAllTasks().size();
        assertTrue(updatedTaskCount > initialTaskCount, "Количество задач должно увеличиться после " +
                "создания новых");

        taskManager.deleteAllTasks();

        assertTrue(taskManager.getPrioritizedTasks().isEmpty(),
                "В prioritizedTasks не должно быть задач после удаления");

        assertTrue(
                taskManager.getPrioritizedTasks().stream().count() == 0,
                "Количество задач в prioritizedTasks должно быть равно нулю"
        );

        assertTrue(taskManager.getAllTasks().isEmpty(), "После удаления всех задач список должен быть пустым");
        assertEquals(0, taskManager.getAllTasks().size(), "Размер списка задач должен быть равен 0");

        taskManager.deleteAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty(), "Повторное удаление не должно нарушить состояние");

        LocalDateTime startTime6 = LocalDateTime.of(2029, 10, 11, 15, 0);  // 19 октября 2025, 13:00
        Duration duration6 = Duration.ofHours(1);

        Task newTask = taskManager.createTask(new Task("Проверка после очистки", "Тест", taskManager,
                Status.NEW, startTime6, duration6));
        assertFalse(taskManager.getAllTasks().isEmpty(), "После создания новой задачи список не должен быть " +
                "пустым");
        assertEquals(1, taskManager.getAllTasks().size(), "Должна быть только одна задача");
        assertNotNull(taskManager.getTaskById(newTask.getId()), "Созданная задача должна быть доступна");
    }

    @Test
    void testCreateSubtask() {
        LocalDateTime startTime1 = LocalDateTime.of(2027, 10, 1, 8, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2027, 10, 3, 9, 0);
        Duration duration2 = Duration.ofHours(2);

        LocalDateTime startTime4 = LocalDateTime.of(2027, 10, 7, 12, 0);
        Duration duration4 = Duration.ofHours(1);

        LocalDateTime startTime5 = LocalDateTime.of(2027, 10, 9, 13, 0);
        Duration duration5 = Duration.ofHours(2);

        Epic epic1 = taskManager.createEpic(new Epic("Учить английский", "Очень страшная задача", taskManager,
                Status.NEW));
        Epic epic2 = taskManager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", taskManager,
                Status.NEW));


        Subtask subtask1 = taskManager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы", taskManager,
                epic1.getId(), Status.NEW, startTime1, duration1));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Разбираться в IDEA", "Переводить все встреченные " +
                "слова", taskManager, epic1.getId(), Status.NEW, startTime2, duration2));

        assertNotNull(subtask1, "Подзадача должна быть создана");
        assertNotEquals(0, subtask1.getId(), "ID подзадачи не должен быть нулевым");

        Subtask subtaskDuplicate1 = taskManager.createSubtask(new Subtask("Найти репетитора",
                "Почитать отзывы", taskManager, epic1.getId(), Status.NEW, startTime1, duration1));
        assertEquals(subtask1, subtaskDuplicate1, "При добавлении идентичной подзадачи должна вернуться " +
                "существующая подзадача");

        Subtask emptyDescriptionSubtask = taskManager.createSubtask(
                new Subtask("Подзадача без описания", "", taskManager, epic2.getId(), Status.NEW,
                        startTime4, duration4));

        assertNotNull(emptyDescriptionSubtask, "Должна создать подзадачу с пустым описанием");

        Subtask emptyTitleSubtask = taskManager.createSubtask(
                new Subtask("", "Описание подзадачи без названия", taskManager, epic2.getId(), Status.NEW,
                        startTime5, duration5));

        assertNotNull(emptyTitleSubtask, "Должна создать подзадачу с пустым названием");

        List<Subtask> allSubtasks = taskManager.getAllSubtasks();
        assertTrue(allSubtasks.contains(subtask2), "Новая подзадача должна быть в списке");
        assertTrue(allSubtasks.contains(emptyDescriptionSubtask), "Подзадача с пустым описанием " +
                "должна быть в списке");
        assertTrue(allSubtasks.contains(emptyTitleSubtask), "Подзадача с пустым названием должна быть " +
                "в списке");

        Epic epic = taskManager.getEpicById(epic1.getId());
        assertTrue(epic.getSubtasks().contains(subtask2), "Подзадача должна быть добавлена в эпик");
    }

    @Test
    void testUpdateSubtask() {
        LocalDateTime startTime1 = LocalDateTime.of(2027, 10, 1, 8, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime4 = LocalDateTime.of(2027, 10, 7, 12, 0);
        Duration duration4 = Duration.ofHours(1);

        Epic epic1 = taskManager.createEpic(new Epic("Учить английский", "Очень страшная задача", taskManager,
                Status.NEW));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы", taskManager,
                epic1.getId(), Status.NEW, startTime1, duration1));

        assertEquals(Status.NEW, epic1.getStatus(), "Исходный статус эпика должен быть NEW");

        String originalTitle = subtask1.getTitle();
        String originalDescription = subtask1.getDescription();

        String newTitle = "Обновленное название";
        String newDescription = "Новое описание";
        Status newStatus = Status.DONE;

        Subtask updatedSubtask = new Subtask(
                newTitle,
                newDescription,
                taskManager,
                subtask1.getEpicId(),
                newStatus,
                startTime4,
                duration4
        );

        taskManager.updateSubtask(updatedSubtask, subtask1.getId());

        Subtask retrievedSubtask = taskManager.getSubtaskById(subtask1.getId());
        assertNotNull(retrievedSubtask, "Подзадача должна существовать после обновления");

        assertEquals(newTitle, retrievedSubtask.getTitle(), "Название не обновилось");
        assertEquals(newDescription, retrievedSubtask.getDescription(), "Описание не обновилось");
        assertEquals(newStatus, retrievedSubtask.getStatus(), "Статус не обновился");

        assertNotEquals(originalTitle, retrievedSubtask.getTitle());
        assertNotEquals(originalDescription, retrievedSubtask.getDescription());
    }

    @Test
    void testDeleteAllSubtasks() {
        LocalDateTime startTime1 = LocalDateTime.of(2027, 10, 1, 8, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2027, 10, 3, 9, 0);
        Duration duration2 = Duration.ofHours(2);

        LocalDateTime startTime3 = LocalDateTime.of(2027, 10, 5, 11, 0);
        Duration duration3 = Duration.ofHours(1);

        Epic epic1 = taskManager.createEpic(new Epic("Учить английский", "Очень страшная задача", taskManager,
                Status.NEW));
        Epic epic2 = taskManager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", taskManager,
                Status.NEW));


        Subtask subtask1 = taskManager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы", taskManager,
                epic1.getId(), Status.NEW, startTime1, duration1));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Разбираться в IDEA", "Переводить все встреченные " +
                "слова", taskManager, epic1.getId(), Status.NEW, startTime2, duration2));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", taskManager, epic2.getId(), Status.NEW, startTime3, duration3));

        List<Subtask> initialSubtasks = taskManager.getAllSubtasks();
        assertEquals(3, initialSubtasks.size(), "Изначально должно быть 3 подзадачи");

        assertTrue(taskManager.getAllSubtasks().contains(subtask1));
        assertTrue(taskManager.getAllSubtasks().contains(subtask2));
        assertTrue(taskManager.getAllSubtasks().contains(subtask3));

        assertTrue(epic1.getSubtasks().contains(subtask1));
        assertTrue(epic1.getSubtasks().contains(subtask2));
        assertTrue(epic2.getSubtasks().contains(subtask3));

        taskManager.deleteAllSubtasks();

        assertEquals(0, taskManager.getAllSubtasks().size(), "После удаления должно быть 0 подзадач");

        assertTrue(epic1.getSubtasks().isEmpty(), "В эпике 1 не должно быть подзадач");
        assertTrue(epic2.getSubtasks().isEmpty(), "В эпике 2 не должно быть подзадач");

        assertNull(taskManager.getSubtaskById(subtask1.getId()));
        assertNull(taskManager.getSubtaskById(subtask2.getId()));
        assertNull(taskManager.getSubtaskById(subtask3.getId()));
    }

    @Test
    void testGetAllSubtasks() {
        LocalDateTime startTime1 = LocalDateTime.of(2027, 10, 1, 8, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2027, 10, 3, 9, 0);
        Duration duration2 = Duration.ofHours(2);

        LocalDateTime startTime3 = LocalDateTime.of(2027, 10, 5, 11, 0);
        Duration duration3 = Duration.ofHours(1);

        Epic epic1 = taskManager.createEpic(new Epic("Учить английский", "Очень страшная задача", taskManager,
                Status.NEW));
        Epic epic2 = taskManager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", taskManager,
                Status.NEW));


        Subtask subtask1 = taskManager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы", taskManager,
                epic1.getId(), Status.NEW, startTime1, duration1));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Разбираться в IDEA", "Переводить все встреченные " +
                "слова", taskManager, epic1.getId(), Status.NEW, startTime2, duration2));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", taskManager, epic2.getId(), Status.NEW, startTime3, duration3));

        List<Subtask> allSubtasks = taskManager.getAllSubtasks();

        assertEquals(3, allSubtasks.size(), "Должны быть все созданные подзадачи");

        assertTrue(allSubtasks.contains(subtask1), "Подзадача 1 должна быть в списке");
        assertTrue(allSubtasks.contains(subtask2), "Подзадача 2 должна быть в списке");
        assertTrue(allSubtasks.contains(subtask3), "Подзадача 3 должна быть в списке");

        assertEquals(epic1.getId(), subtask1.getEpicId(), "Неверный эпик для подзадачи 1");
        assertEquals(epic1.getId(), subtask2.getEpicId(), "Неверный эпик для подзадачи 2");
        assertEquals(epic2.getId(), subtask3.getEpicId(), "Неверный эпик для подзадачи 3");

        assertNotEquals(0, subtask1.getId(), "ID подзадачи не должен быть нулевым");
        assertNotEquals(0, subtask2.getId(), "ID подзадачи не должен быть нулевым");
        assertNotEquals(0, subtask3.getId(), "ID подзадачи не должен быть нулевым");

        assertEquals(Status.NEW, subtask1.getStatus(), "Неверный статус для подзадачи 1");
        assertEquals(Status.NEW, subtask2.getStatus(), "Неверный статус для подзадачи 2");
        assertEquals(Status.NEW, subtask3.getStatus(), "Неверный статус для подзадачи 3");
    }

    @Test
    void testGetSubtaskById() {
        HistoryManager historyManager = taskManager.getHistoryManager();
        LocalDateTime startTime1 = LocalDateTime.of(2027, 10, 1, 8, 0);
        Duration duration1 = Duration.ofHours(1);

        Epic epic1 = taskManager.createEpic(new Epic("Учить английский", "Очень страшная задача", taskManager,
                Status.NEW));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы", taskManager,
                epic1.getId(), Status.NEW, startTime1, duration1));

        Subtask invalidSubtask = taskManager.getSubtaskById(9999);
        assertNull(invalidSubtask, "Должен быть возвращен null");
        assertTrue(historyManager.getHistory().isEmpty(),
                "В историю не должно быть добавлено ничего");

        Subtask retrievedSubtask = taskManager.getSubtaskById(subtask1.getId());
        assertNotNull(retrievedSubtask, "Должна быть возвращена подзадача");
        assertEquals(subtask1, retrievedSubtask, "Должна быть возвращена та же подзадача");
        assertTrue(historyManager.getHistory().contains(retrievedSubtask),
                "Задача должна быть добавлена в историю");
    }

    @Test
    void testSubtaskRemoveById() {
        LocalDateTime startTime1 = LocalDateTime.of(2027, 10, 1, 8, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2027, 10, 3, 9, 0);
        Duration duration2 = Duration.ofHours(2);

        LocalDateTime startTime3 = LocalDateTime.of(2027, 10, 5, 11, 0);
        Duration duration3 = Duration.ofHours(1);

        Epic epic1 = taskManager.createEpic(new Epic("Учить английский", "Очень страшная задача", taskManager,
                Status.NEW));
        Epic epic2 = taskManager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", taskManager,
                Status.NEW));


        Subtask subtask1 = taskManager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы", taskManager,
                epic1.getId(), Status.NEW, startTime1, duration1));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Разбираться в IDEA", "Переводить все встреченные " +
                "слова", taskManager, epic1.getId(), Status.NEW, startTime2, duration2));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", taskManager, epic2.getId(), Status.NEW, startTime3, duration3));

        List<Subtask> initialList = taskManager.getAllSubtasks();
        assertEquals(3, initialList.size(), "В списке должно быть 3 подзадачи");

        taskManager.removeSubtaskById(subtask2.getId());

        List<Subtask> updatedList = taskManager.getAllSubtasks();
        assertEquals(2, updatedList.size(), "После удаления должна остаться 2 подзадачи");
        assertFalse(updatedList.contains(subtask2), "Удаленная подзадача не должна быть в списке");

        assertTrue(updatedList.contains(subtask1));
        assertTrue(updatedList.contains(subtask3));
    }

    @Test
    void testEpicStatusNew() {
        LocalDateTime startTime1 = LocalDateTime.of(2027, 2, 1, 1, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2027, 2, 3, 5, 0);
        Duration duration2 = Duration.ofHours(2);

        LocalDateTime startTime3 = LocalDateTime.of(2027, 2, 5, 10, 0);
        Duration duration3 = Duration.ofHours(1);

        Epic epic1 = taskManager.createEpic(new Epic("Учить английский", "Очень страшная задача", taskManager,
                Status.NEW));
        Epic epic2 = taskManager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", taskManager,
                Status.NEW));

        Subtask subtask1 = taskManager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы",
                taskManager, epic1.getId(), Status.NEW, startTime1, duration1));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Разбираться в IDEA", "Переводить все " +
                "встреченные слова", taskManager, epic1.getId(), Status.NEW, startTime2, duration2));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", taskManager, epic2.getId(), Status.NEW, startTime3, duration3));

        assertEquals(Status.NEW, epic1.getStatus(), "Cтатус эпика должен быть NEW");
        assertEquals(Status.NEW, epic2.getStatus(), "Cтатус эпика должен быть NEW");
    }

    @Test
    void testEpicStatusDone() {
        LocalDateTime startTime1 = LocalDateTime.of(2027, 2, 1, 1, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2027, 2, 3, 5, 0);
        Duration duration2 = Duration.ofHours(2);

        LocalDateTime startTime3 = LocalDateTime.of(2027, 2, 5, 10, 0);
        Duration duration3 = Duration.ofHours(1);

        Epic epic1 = taskManager.createEpic(new Epic("Учить английский", "Очень страшная задача", taskManager,
                Status.NEW));
        Epic epic2 = taskManager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", taskManager,
                Status.NEW));

        Subtask subtask1 = taskManager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы",
                taskManager, epic1.getId(), Status.DONE, startTime1, duration1));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Разбираться в IDEA", "Переводить все " +
                "встреченные слова", taskManager, epic1.getId(), Status.DONE, startTime2, duration2));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", taskManager, epic2.getId(), Status.DONE, startTime3, duration3));

        assertEquals(Status.DONE, epic1.getStatus(), "Cтатус эпика должен быть DONE");
        assertEquals(Status.DONE, epic2.getStatus(), "Cтатус эпика должен быть DONE");
    }

    @Test
    void testEpicStatusNewAndDone() {
        LocalDateTime startTime1 = LocalDateTime.of(2027, 2, 1, 1, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2027, 2, 3, 5, 0);
        Duration duration2 = Duration.ofHours(2);

        LocalDateTime startTime3 = LocalDateTime.of(2027, 2, 5, 10, 0);
        Duration duration3 = Duration.ofHours(1);

        LocalDateTime startTime4 = LocalDateTime.of(2027, 2, 7, 15, 0);
        Duration duration4 = Duration.ofHours(1);

        Epic epic1 = taskManager.createEpic(new Epic("Учить английский", "Очень страшная задача", taskManager,
                Status.NEW));
        Epic epic2 = taskManager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", taskManager,
                Status.NEW));

        Subtask subtask1 = taskManager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы",
                taskManager, epic1.getId(), Status.NEW, startTime1, duration1));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Разбираться в IDEA", "Переводить все " +
                "встреченные слова", taskManager, epic1.getId(), Status.DONE, startTime2, duration2));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", taskManager, epic2.getId(), Status.NEW, startTime3, duration3));
        Subtask subtask4 = taskManager.createSubtask(new Subtask("Купить масло для замены",
                "Посоветуйся с механиком", taskManager, epic2.getId(), Status.DONE, startTime4, duration4));

        assertEquals(Status.IN_PROGRESS, epic2.getStatus(), "Cтатус эпика должен быть IN_PROGRESS");
        assertEquals(Status.IN_PROGRESS, epic2.getStatus(), "Cтатус эпика должен быть IN_PROGRESS");
    }

    @Test
    void testEpicStatusInProgress() {
        LocalDateTime startTime1 = LocalDateTime.of(2027, 2, 1, 1, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2027, 2, 3, 5, 0);
        Duration duration2 = Duration.ofHours(2);

        LocalDateTime startTime3 = LocalDateTime.of(2027, 2, 5, 10, 0);
        Duration duration3 = Duration.ofHours(1);

        Epic epic1 = taskManager.createEpic(new Epic("Учить английский", "Очень страшная задача", taskManager,
                Status.NEW));
        Epic epic2 = taskManager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", taskManager,
                Status.NEW));

        Subtask subtask1 = taskManager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы",
                taskManager, epic1.getId(), Status.IN_PROGRESS, startTime1, duration1));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Разбираться в IDEA", "Переводить все " +
                "встреченные слова", taskManager, epic1.getId(), Status.IN_PROGRESS, startTime2, duration2));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Изучить вопрос на драйв2", "Сделать " +
                "список вариантов", taskManager, epic2.getId(), Status.IN_PROGRESS, startTime3, duration3));

        assertEquals(Status.IN_PROGRESS, epic2.getStatus(), "Cтатус эпика должен быть IN_PROGRESS");
        assertEquals(Status.IN_PROGRESS, epic2.getStatus(), "Cтатус эпика должен быть IN_PROGRESS");
    }

    @Test
    void testCreateEpic() {
        Epic epic1 = taskManager.createEpic(new Epic("Учить английский", "Очень страшная задача", taskManager,
                Status.NEW));
        assertNotNull(epic1, "Эпик должен быть создан");
        assertNotEquals(0, epic1.getId(), "ID эпика не должен быть нулевым");

        Epic epicWithEmptyDescription = new Epic("Пустой эпик", "", taskManager, Status.NEW);
        Epic createdEmptyDescriptionEpic = taskManager.createEpic(epicWithEmptyDescription);

        assertNotNull(createdEmptyDescriptionEpic, "Должен создать эпик с пустым описанием");

        Epic epicWithEmptyTitle = new Epic("", "Описание пустого эпика", taskManager, Status.NEW);
        Epic createdEmptyTitleEpic = taskManager.createEpic(epicWithEmptyTitle);

        assertNotNull(createdEmptyTitleEpic, "Должен создать эпик с пустым названием");

        List<Epic> finalList = taskManager.getAllEpics();
        assertTrue(finalList.contains(epic1), "Новый эпик должен быть в системе");
        assertTrue(finalList.contains(createdEmptyDescriptionEpic), "Эпик с пустым описанием должен " +
                "быть в системе");
        assertTrue(finalList.contains(createdEmptyTitleEpic), "Эпик с пустым названием должен быть в системе");
    }

    @Test
    void testGetEpicById() {
        HistoryManager historyManager = taskManager.getHistoryManager();
        Epic epic1 = taskManager.createEpic(new Epic("Учить английский", "Очень страшная задача", taskManager,
                Status.NEW));
        Epic epic2 = taskManager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", taskManager,
                Status.NEW));

        Epic retrievedEpic1 = taskManager.getEpicById(epic1.getId());
        assertNotNull(retrievedEpic1, "Должен вернуть существующий эпик");
        assertEquals(epic1, retrievedEpic1, "Полученный эпик должен совпадать с исходным");

        Epic retrievedEpic2 = taskManager.getEpicById(epic2.getId());
        assertNotNull(retrievedEpic2, "Должен вернуть существующий эпик");
        assertEquals(epic2, retrievedEpic2, "Полученный эпик должен совпадать с исходным");

        Epic nonExistingEpic = taskManager.getEpicById(9999);
        assertNull(nonExistingEpic, "Должен вернуть null для несуществующего ID");

        Epic zeroIdEpic = taskManager.getEpicById(0);
        assertNull(zeroIdEpic, "Должен вернуть null для ID=0");

        assertEquals(Status.NEW, retrievedEpic1.getStatus(), "Статус эпика должен быть корректным");

        taskManager.deleteEpic(epic1.getId());
        Epic deletedEpic = taskManager.getEpicById(epic1.getId());
        assertNull(deletedEpic, "После удаления должен возвращать null");

        List<Task> historyAfterGet = historyManager.getHistory();
        assertEquals(2, historyAfterGet.size(), "В истории должно быть два элемента");
        assertTrue(historyAfterGet.contains(epic2), "Полученный эпик должен быть в истории");
    }

    @Test
    public void testDeleteEpic() {
        LocalDateTime startTime1 = LocalDateTime.of(2027, 1, 1, 1, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2027, 1, 3, 5, 0);
        Duration duration2 = Duration.ofHours(2);

        LocalDateTime startTime3 = LocalDateTime.of(2027, 1, 5, 10, 0);
        Duration duration3 = Duration.ofHours(1);

        Epic epic1 = taskManager.createEpic(new Epic("Учить английский", "Очень страшная задача", taskManager,
                Status.NEW));
        Epic epic2 = taskManager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", taskManager,
                Status.NEW));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы", taskManager,
                epic1.getId(), Status.NEW, startTime1, duration1));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Разбираться в IDEA",
                "Переводить все встреченные слова", taskManager, epic1.getId(), Status.NEW, startTime2, duration2));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", taskManager, epic2.getId(), Status.NEW, startTime3, duration3));

        int epicId = epic1.getId();
        taskManager.deleteEpic(epicId);

        assertNull(taskManager.getEpicById(epicId));

        assertFalse(taskManager.getAllSubtasks().contains(subtask1.getId()));
        assertFalse(taskManager.getAllSubtasks().contains(subtask2.getId()));

        assertNotNull(taskManager.getEpicById(epic2.getId()));
        assertNotNull(taskManager.getSubtaskById(subtask3.getId()));
    }

    @Test
    public void testDeleteAllEpics() {
        LocalDateTime startTime1 = LocalDateTime.of(2027, 1, 1, 1, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2027, 1, 3, 5, 0);
        Duration duration2 = Duration.ofHours(2);

        LocalDateTime startTime3 = LocalDateTime.of(2027, 1, 5, 10, 0);
        Duration duration3 = Duration.ofHours(1);

        Epic epic1 = taskManager.createEpic(new Epic("Учить английский", "Очень страшная задача", taskManager,
                Status.NEW));
        Epic epic2 = taskManager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", taskManager,
                Status.NEW));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы", taskManager,
                epic1.getId(), Status.NEW, startTime1, duration1));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Разбираться в IDEA",
                "Переводить все встреченные слова", taskManager, epic1.getId(), Status.NEW, startTime2, duration2));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", taskManager, epic2.getId(), Status.NEW, startTime3, duration3));

        taskManager.deleteAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty());
        assertTrue(taskManager.getAllSubtasks().isEmpty());

    }

    @Test
    public void testDeleteEpicDoesNotAffectOtherEpics() {
        LocalDateTime startTime3 = LocalDateTime.of(2027, 1, 5, 10, 0);
        Duration duration3 = Duration.ofHours(1);

        Epic epic1 = taskManager.createEpic(new Epic("Учить английский", "Очень страшная задача", taskManager,
                Status.NEW));
        Epic epic2 = taskManager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", taskManager,
                Status.NEW));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", taskManager, epic2.getId(), Status.NEW, startTime3, duration3));

        taskManager.deleteEpic(epic1.getId());

        assertNotNull(taskManager.getEpicById(epic2.getId()));
        assertNotNull(taskManager.getSubtaskById(subtask3.getId()));
    }

    @Test
    void testGetPrioritizedTask() {
        LocalDateTime startTime1 = LocalDateTime.of(2030, 5, 1, 8, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2030, 5, 2, 9, 0);
        Duration duration2 = Duration.ofHours(2);

        LocalDateTime startTime3 = LocalDateTime.of(2030, 5, 3, 11, 0);
        Duration duration3 = Duration.ofHours(1);

        LocalDateTime startTime4 = LocalDateTime.of(2030, 5, 4, 12, 0);
        Duration duration4 = Duration.ofHours(1);

        LocalDateTime startTime5 = LocalDateTime.of(2030, 5, 5, 13, 0);
        Duration duration5 = Duration.ofHours(2);

        Task task1 = taskManager.createTask(new Task("Купить продукты", "Хлеб яйца масло", taskManager, Status.NEW,
                startTime1, duration1));
        Task task2 = taskManager.createTask(new Task("Постирать вещи", "Разделить по цветам", taskManager,
                Status.NEW, startTime2, duration2));

        Epic epic1 = taskManager.createEpic(new Epic("Учить английский", "Очень страшная задача", taskManager,
                Status.NEW));
        Epic epic2 = taskManager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", taskManager,
                Status.NEW));


        Subtask subtask1 = taskManager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы", taskManager,
                epic1.getId(), Status.NEW, startTime3, duration3));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Разбираться в IDEA",
                "Переводить все встреченные слова", taskManager, epic1.getId(), Status.NEW, startTime4,
                duration4));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", taskManager, epic2.getId(), Status.NEW, startTime5, duration5));

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(task1, prioritizedTasks.get(0), "Не соблюден порядок сортировки");
        assertEquals(task2, prioritizedTasks.get(1), "Не соблюден порядок сортировки");
        assertEquals(subtask1, prioritizedTasks.get(2), "Не соблюден порядок сортировки");
        assertEquals(subtask2, prioritizedTasks.get(3), "Не соблюден порядок сортировки");
        assertEquals(subtask3, prioritizedTasks.get(4), "Не соблюден порядок сортировки");

    }

    @Test
    void testUpdatePrioritizedTask() {
        LocalDateTime startTime1 = LocalDateTime.of(2037, 5, 1, 8, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2038, 5, 2, 9, 0);
        Duration duration2 = Duration.ofHours(2);

        LocalDateTime startTime3 = LocalDateTime.of(2039, 5, 4, 12, 0);
        Duration duration3 = Duration.ofHours(1);

        LocalDateTime startTime4 = LocalDateTime.of(2040, 5, 6, 13, 0);
        Duration duration4 = Duration.ofHours(2);

        Task task1 = taskManager.createTask(new Task("Купить продукты", "Хлеб яйца масло", taskManager,
                Status.NEW, startTime1, duration1));
        Task task2 = taskManager.createTask(new Task("Постирать вещи", "Разделить по цветам", taskManager,
                Status.NEW, startTime2, duration2));
        Epic epic1 = taskManager.createEpic(new Epic("Учить английский", "Очень страшная задача", taskManager,
                Status.NEW));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы",
                taskManager, epic1.getId(), Status.NEW, startTime3, duration3));

        Task updateTask2 = taskManager.createTask(new Task("Постирать вещи", "Разделить по цветам", task2.getId(), taskManager,
                Status.NEW, startTime4, duration4));
        updateTask2.setId(task2.getId());
        taskManager.updateTask(updateTask2);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertTrue(taskManager.getPrioritizedTasks().contains(updateTask2),
                "Задача должна быть добавлена в prioritizedTasks после обновления");
        assertEquals(3, prioritizedTasks.size(), "Должно быть 3 элемента");

        Task extractedTask = prioritizedTasks.get(2);
        LocalDateTime extractedStartTime = extractedTask.getStartTime();
        Duration extractedDuration = extractedTask.getDuration();

        assertEquals(startTime4, extractedStartTime, "В 3 элементе должно храниться время внесенное " +
                "при обновлении");
        assertEquals(duration4, extractedDuration, "В 3 элементе должна храниться продолжительность " +
                "внесенная при обновлении");

    }

    @Test
    void testGetCurrentTaskCount() {
        int countAfterFirstCall = taskManager.getCurrentTaskCount();
        assertTrue(countAfterFirstCall > 0, "Счётчик должен быть больше нуля после первого вызова");
    }

    @Test
    void testGetHistoryManager() {
        HistoryManager historyManager = taskManager.getHistoryManager();
        LocalDateTime startTime1 = LocalDateTime.of(2030, 10, 19, 8, 0);
        Duration duration1 = Duration.ofHours(1);

        Subtask subtask1 = taskManager.createSubtask(new Subtask("Подзадача тест", "Описание подзадачи",
                taskManager, 1, Status.NEW, startTime1, duration1));
        Subtask subtaskDuplicate = taskManager.getSubtaskById(subtask1.getId());
        List<Task> historyAfterGet = historyManager.getHistory();
        List<Subtask> allSubtasks = taskManager.getAllSubtasks();
        assertTrue(allSubtasks.contains(subtask1), "Новая подзадача должна быть в списке");
        assertEquals(1, historyAfterGet.size(), "В истории должен быть 1 элемент");
        assertTrue(historyAfterGet.contains(subtask1), "Полученная подзадача должна быть в истории");
    }

    @Test
    void testGetHistory() {
        HistoryManager historyManager = taskManager.getHistoryManager();
        LocalDateTime startTime1 = LocalDateTime.of(2030, 5, 1, 8, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2030, 5, 2, 9, 0);
        Duration duration2 = Duration.ofHours(2);

        LocalDateTime startTime3 = LocalDateTime.of(2030, 5, 3, 11, 0);
        Duration duration3 = Duration.ofHours(1);

        LocalDateTime startTime4 = LocalDateTime.of(2030, 5, 4, 12, 0);
        Duration duration4 = Duration.ofHours(1);

        LocalDateTime startTime5 = LocalDateTime.of(2030, 5, 5, 13, 0);
        Duration duration5 = Duration.ofHours(2);

        Task task1 = taskManager.createTask(new Task("Купить продукты", "Хлеб яйца масло", taskManager, Status.NEW,
                startTime1, duration1));
        Task task2 = taskManager.createTask(new Task("Постирать вещи", "Разделить по цветам", taskManager,
                Status.NEW, startTime2, duration2));

        Epic epic1 = taskManager.createEpic(new Epic("Учить английский", "Очень страшная задача", taskManager,
                Status.NEW));
        Epic epic2 = taskManager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", taskManager,
                Status.NEW));


        Subtask subtask1 = taskManager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы", taskManager,
                epic1.getId(), Status.NEW, startTime3, duration3));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Разбираться в IDEA",
                "Переводить все встреченные слова", taskManager, epic1.getId(), Status.NEW, startTime4,
                duration4));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", taskManager, epic2.getId(), Status.NEW, startTime5, duration5));

        Task testTask1 = taskManager.getTaskById(task1.getId());
        Task testTask2 = taskManager.getTaskById(task2.getId());
        Task testTask3 = taskManager.getTaskById(task1.getId());
        Epic testEpic1 = taskManager.getEpicById(epic1.getId());
        Epic testEpic2 = taskManager.getEpicById(epic2.getId());
        Epic testEpic3 = taskManager.getEpicById(epic2.getId());
        Subtask testSubtask1 = taskManager.getSubtaskById(subtask1.getId());
        Subtask testSubtask2 = taskManager.getSubtaskById(subtask2.getId());
        Subtask testSubtask3 = taskManager.getSubtaskById(subtask3.getId());
        Subtask testSubtask4 = taskManager.getSubtaskById(subtask1.getId());

        List<Task> historyAfterGet = historyManager.getHistory();
        assertEquals(7, historyAfterGet.size(), "В истории должно быть 7 элементов");
        assertTrue(historyAfterGet.contains(task2), "Полученная задача должна быть в истории");
        assertTrue(historyAfterGet.contains(epic2), "Полученный эпик должен быть в истории");
        assertTrue(historyAfterGet.contains(subtask1), "Полученная подзадача должна быть в истории");

        List<Task> comparisonHistoryAfterGet = Arrays.asList(
                task2,
                task1,
                epic1,
                epic2,
                subtask2,
                subtask3,
                subtask1
        );
        assertEquals(comparisonHistoryAfterGet, historyAfterGet, "История сохраняется с ошибками");

        historyAfterGet = historyManager.getHistory();
        assertEquals(7, historyAfterGet.size(), "В истории должно быть 7 элементов");

        List<Task> comparisonHistoryAfterGet2 = Arrays.asList(
                task2,
                task1,
                epic1,
                epic2,
                subtask2,
                subtask3,
                subtask1
        );
        assertEquals(comparisonHistoryAfterGet2, historyAfterGet, "История сохраняется с ошибками");
    }
}
