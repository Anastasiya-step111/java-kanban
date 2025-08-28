import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    TaskManager manager = Managers.getDefault();
    HistoryManager historyManager = manager.getHistoryManager();

    @Test
    void testGetCurrentTaskCount() {
        int initialCount = manager.getCurrentTaskCount();
        assertEquals(2, initialCount, "Начальный счетчик должен быть равен 2");

        int countAfterFirstCall = manager.getCurrentTaskCount();
        assertEquals(3, countAfterFirstCall, "После первого вызова счетчик должен увеличиться на 1");

        int countAfterSecondCall = manager.getCurrentTaskCount();
        assertEquals(4, countAfterSecondCall, "После второго вызова счетчик должен увеличиться еще на 1");

        int countAfterThirdCall = manager.getCurrentTaskCount();
        assertEquals(5, countAfterThirdCall, "Каждый вызов должен увеличивать счетчик на 1");

        Task task1 = manager.createTask(new Task("Тест", "Описание", manager, Status.NEW));
        assertEquals(6, task1.getId(), "Создание задачи должно увеличивать счетчик");

        Epic epic1 = manager.createEpic(new Epic("Эпик тест", "Описание эпика",  manager, Status.NEW));
        assertEquals(7, epic1.getId(), "Создание эпика должно увеличивать счетчик");

        Subtask subtask1 = manager.createSubtask(new Subtask("Подзадача тест", "Описание подзадачи",
                manager, 1, Status.NEW));
        assertEquals(8, subtask1.getId(), "Создание подзадачи должно увеличивать счетчик");
    }

    @Test
    void testGetHistoryManager() {
        HistoryManager historyManager = manager.getHistoryManager();
        Subtask subtask1 = manager.createSubtask(new Subtask("Подзадача тест", "Описание подзадачи",
                manager, 1, Status.NEW));
        Subtask testSubtask1 = manager.getSubtaskById(subtask1.getId());
        List<Task> historyAfterGet = historyManager.getHistory();
        assertEquals(1, historyAfterGet.size(), "В истории должен быть 1 элемент");
        assertTrue(historyAfterGet.contains(subtask1), "Полученная подзадача должна быть в истории");
    }

    @Test
    void testGetHistory() {
        HistoryManager historyManager = manager.getHistoryManager();

        Task task1 = manager.createTask(new Task("Купить продукты", "Хлеб яйца масло", manager, Status.NEW));
        Task task2 = manager.createTask(new Task("Постирать вещи", "Разделить по цветам", manager, Status.NEW));

        Epic epic1 = manager.createEpic(new Epic("Учить английский", "Очень страшная задача", manager,
                Status.NEW));
        Epic epic2 = manager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", manager,
                Status.NEW));
        Subtask subtask1 = manager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы", manager,
                epic1.getId(), Status.NEW));
        Subtask subtask2 = manager.createSubtask(new Subtask("Разбираться в IDEA",
                "Переводить все встреченные слова", manager, epic1.getId(), Status.NEW));
        Subtask subtask3 = manager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", manager, epic2.getId(), Status.NEW));

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
        assertEquals(10, historyAfterGet.size(), "В истории должно быть 10 элементов");
        assertTrue(historyAfterGet.contains(task2), "Полученная задача должна быть в истории");
        assertTrue(historyAfterGet.contains(epic2), "Полученный эпик должен быть в истории");
        assertTrue(historyAfterGet.contains(subtask1), "Полученная подзадача должна быть в истории");

        List<Task> comparisonHistoryAfterGet = Arrays.asList(
                task1,
                task2,
                task1,
                epic1,
                epic2,
                epic2,
                subtask1,
                subtask2,
                subtask3,
                subtask1
        );
        assertEquals(comparisonHistoryAfterGet, historyAfterGet, "История сохраняется с ошибками");

        Subtask testSubtask5 = manager.getSubtaskById(subtask1.getId());
        historyAfterGet = historyManager.getHistory();
        assertEquals(10, historyAfterGet.size(), "В истории должно быть 10 элементов");

        List<Task> comparisonHistoryAfterGet2 = Arrays.asList(
                task2,
                task1,
                epic1,
                epic2,
                epic2,
                subtask1,
                subtask2,
                subtask3,
                subtask1,
                subtask1
        );
        assertEquals(comparisonHistoryAfterGet2, historyAfterGet, "История сохраняется с ошибками");
    }
}

