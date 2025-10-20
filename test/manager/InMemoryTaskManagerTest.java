package manager;

import ru.practicum.manager.HistoryManager;
import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    TaskManager manager = Managers.getDefault();
    HistoryManager historyManager = manager.getHistoryManager();

    @Test
    void testGetCurrentTaskCount() {
        int countAfterFirstCall = manager.getCurrentTaskCount();
        assertTrue(countAfterFirstCall > 0, "Счётчик должен быть больше нуля после первого вызова");
    }

    @Test
    void testGetHistoryManager() {
        LocalDateTime startTime1 = LocalDateTime.of(2030, 10, 19, 8, 0);
        Duration duration1 = Duration.ofHours(1);

        HistoryManager historyManager = manager.getHistoryManager();
        Subtask subtask1 = manager.createSubtask(new Subtask("Подзадача тест", "Описание подзадачи",
                manager, 1, Status.NEW, startTime1, duration1));
        Subtask subtask2 = manager.getSubtaskById(subtask1.getId());
        List<Task> historyAfterGet = historyManager.getHistory();
        List<Subtask> allSubtasks = manager.getAllSubtasks();
        assertTrue(allSubtasks.contains(subtask1), "Новая подзадача должна быть в списке");
        assertEquals(1, historyAfterGet.size(), "В истории должен быть 1 элемент");
        assertTrue(historyAfterGet.contains(subtask1), "Полученная подзадача должна быть в истории");
    }

    @Test
    void testGetHistory() {
        HistoryManager historyManager = manager.getHistoryManager();

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

        Task task1 = manager.createTask(new Task("Купить продукты", "Хлеб яйца масло", manager, Status.NEW,
                startTime1, duration1));
        Task task2 = manager.createTask(new Task("Постирать вещи", "Разделить по цветам", manager,
                Status.NEW, startTime2, duration2));

        Epic epic1 = manager.createEpic(new Epic("Учить английский", "Очень страшная задача", manager,
                Status.NEW));
        Epic epic2 = manager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", manager,
                Status.NEW));


        Subtask subtask1 = manager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы", manager,
                epic1.getId(), Status.NEW, startTime3, duration3));
        Subtask subtask2 = manager.createSubtask(new Subtask("Разбираться в IDEA",
                "Переводить все встреченные слова", manager, epic1.getId(), Status.NEW, startTime4,
                duration4));
        Subtask subtask3 = manager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", manager, epic2.getId(), Status.NEW, startTime5, duration5));

        Task testTask1 = manager.getTaskById(task1.getId());
        Task testTask2 = manager.getTaskById(task2.getId());
        Task testTask3 = manager.getTaskById(task1.getId());
        Epic testEpic1 = manager.getEpicById(epic1.getId());
        Epic testEpic2 = manager.getEpicById(epic2.getId());
        Epic testEpic3 = manager.getEpicById(epic2.getId());
        Subtask testSubtask1 = manager.getSubtaskById(subtask1.getId());
        Subtask testSubtask2 = manager.getSubtaskById(subtask2.getId());
        Subtask testSubtask3 = manager.getSubtaskById(subtask3.getId());
        Subtask testSubtask4 = manager.getSubtaskById(subtask1.getId());

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

        Subtask testSubtask5 = manager.getSubtaskById(subtask1.getId());
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

        Task task1 = manager.createTask(new Task("Купить продукты", "Хлеб яйца масло", manager, Status.NEW,
                startTime1, duration1));
        Task task2 = manager.createTask(new Task("Постирать вещи", "Разделить по цветам", manager,
                Status.NEW, startTime2, duration2));

        Epic epic1 = manager.createEpic(new Epic("Учить английский", "Очень страшная задача", manager,
                Status.NEW));
        Epic epic2 = manager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", manager,
                Status.NEW));


        Subtask subtask1 = manager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы", manager,
                epic1.getId(), Status.NEW, startTime3, duration3));
        Subtask subtask2 = manager.createSubtask(new Subtask("Разбираться в IDEA",
                "Переводить все встреченные слова", manager, epic1.getId(), Status.NEW, startTime4,
                duration4));
        Subtask subtask3 = manager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", manager, epic2.getId(), Status.NEW, startTime5, duration5));

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

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

        Task task1 = manager.createTask(new Task("Купить продукты", "Хлеб яйца масло", manager,
                Status.NEW, startTime1, duration1));
        Task task2 = manager.createTask(new Task("Постирать вещи", "Разделить по цветам", manager,
                Status.NEW, startTime2, duration2));
        Epic epic1 = manager.createEpic(new Epic("Учить английский", "Очень страшная задача", manager,
                Status.NEW));
        Subtask subtask1 = manager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы",
                manager, epic1.getId(), Status.NEW, startTime3, duration3));

        Task updateTask2 = manager.createTask(new Task("Постирать вещи", "Разделить по цветам", task2.getId(), manager,
                Status.NEW, startTime4, duration4));
        updateTask2.setId(task2.getId());
        manager.updateTask(updateTask2);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertTrue(manager.getPrioritizedTasks().contains(updateTask2),
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

}

