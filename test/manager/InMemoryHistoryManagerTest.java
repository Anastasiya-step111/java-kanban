package manager;

import ru.practicum.manager.HistoryManager;
import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    TaskManager manager = Managers.getDefault();
    HistoryManager historyManager = manager.getHistoryManager();

    Task task1;
    Task task2;

    Epic epic1;
    Epic epic2;


    Subtask subtask1;
    Subtask subtask2;
    Subtask subtask3;


    @BeforeEach
    public void beforeEach() {
        task1 = manager.createTask(new Task("Купить продукты", "Хлеб яйца масло", manager, Status.NEW));
        task2 = manager.createTask(new Task("Постирать вещи", "Разделить по цветам", manager,
                Status.NEW));

        epic1 = manager.createEpic(new Epic("Учить английский", "Очень страшная задача", manager,
                Status.NEW));
        epic2 = manager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", manager,
                Status.NEW));


        subtask1 = manager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы", manager,
                epic1.getId(), Status.NEW));
        subtask2 = manager.createSubtask(new Subtask("Разбираться в IDEA",
                "Переводить все встреченные слова", manager, epic1.getId(), Status.NEW));
        subtask3 = manager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", manager, epic2.getId(), Status.NEW));
    }
    @Test
    void testAddTaskAndGetHistory() {


        Task testTask1 = manager.getTaskById(task1.getId());
        Task testTask2 = manager.getTaskById(task2.getId());
        Task testTask3 = manager.getTaskById(task1.getId());

        List<Task> historyAfterGet = historyManager.getHistory();
        assertEquals(2, historyAfterGet.size(), "В истории должно быть 2 элемента");
        assertTrue(historyAfterGet.contains(task2), "Полученная задача должна быть в истории");

        Epic testEpic1 = manager.getEpicById(epic1.getId());
        Epic testEpic2 = manager.getEpicById(epic2.getId());
        Epic testEpic3 = manager.getEpicById(epic2.getId());

        historyAfterGet = historyManager.getHistory();
        assertEquals(4, historyAfterGet.size(), "В истории должно быть 4 элемента");
        assertTrue(historyAfterGet.contains(epic2), "Полученный эпик должен быть в истории");
        assertTrue(historyAfterGet.contains(epic1), "Полученный эпик должен быть в истории");

        Subtask testSubtask1 = manager.getSubtaskById(subtask1.getId());
        Subtask testSubtask2 = manager.getSubtaskById(subtask2.getId());
        Subtask testSubtask3 = manager.getSubtaskById(subtask3.getId());
        Subtask testSubtask4 = manager.getSubtaskById(subtask1.getId());

        historyAfterGet = historyManager.getHistory();
        assertEquals(7, historyAfterGet.size(), "В истории должно быть 7 элементов");
        assertTrue(historyAfterGet.contains(subtask1), "Полученная подзадача должна быть в истории");
        assertTrue(historyAfterGet.contains(subtask2), "Полученная подзадача должна быть в истории");
        assertTrue(historyAfterGet.contains(subtask3), "Полученная подзадача должна быть в истории");

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

    }

    @Test
    void testRemoveTask() {
        historyManager.add(task1);
        assertTrue(historyManager.getHistory().contains(task1));

        historyManager.remove(task1.getId());
        assertFalse(historyManager.getHistory().contains(task1));
        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    void testAddAndRemoveEpic() {
        historyManager.add(epic1);
        assertTrue(historyManager.getHistory().contains(epic1));

        historyManager.remove(epic1.getId());
        assertFalse(historyManager.getHistory().contains(epic1));
    }

    @Test
    void testAddAndRemoveSubtask() {
        historyManager.add(subtask1);
        assertTrue(historyManager.getHistory().contains(subtask1));

        historyManager.remove(subtask1.getId());
        assertFalse(historyManager.getHistory().contains(subtask1));
    }

    @Test
    void testAddExistingTask() {
        historyManager.add(task1);
        historyManager.add(task1);

        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task1, historyManager.getHistory().get(0));
    }

    @Test
    void testEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
        assertEquals(0, historyManager.getHistory().size());
    }

}
