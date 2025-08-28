import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    TaskManager manager = Managers.getDefault();
    HistoryManager historyManager = manager.getHistoryManager();

    Task initialTask;
    Task task2;

    Epic epic1;
    Epic epic2;


    Subtask subtask1;
    Subtask subtask2;
    Subtask subtask3;


    @BeforeEach
    public void beforeEach() {
        initialTask = manager.createTask(new Task("Купить продукты", "Хлеб яйца масло", manager, Status.NEW));
        task2 = manager.createTask(new Task("Постирать вещи", "Разделить по цветам", manager, Status.NEW));

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


        Task testTask1 = manager.getTaskById(initialTask.getId());
        Task testTask2 = manager.getTaskById(task2.getId());
        Task testTask3 = manager.getTaskById(initialTask.getId());

        List<Task> historyAfterGet = historyManager.getHistory();
        assertEquals(3, historyAfterGet.size(), "В истории должно быть 3 элемента");
        assertTrue(historyAfterGet.contains(task2), "Полученная задача должна быть в истории");

        Epic testEpic1 = manager.getEpicById(epic1.getId());
        Epic testEpic2 = manager.getEpicById(epic2.getId());
        Epic testEpic3 = manager.getEpicById(epic2.getId());

        historyAfterGet = historyManager.getHistory();
        assertEquals(6, historyAfterGet.size(), "В истории должно быть 6 элементов");
        assertTrue(historyAfterGet.contains(epic2), "Полученный эпик должен быть в истории");
        assertTrue(historyAfterGet.contains(epic1), "Полученный эпик должен быть в истории");

        Subtask testSubtask1 = manager.getSubtaskById(subtask1.getId());
        Subtask testSubtask2 = manager.getSubtaskById(subtask2.getId());
        Subtask testSubtask3 = manager.getSubtaskById(subtask3.getId());
        Subtask testSubtask4 = manager.getSubtaskById(subtask1.getId());

        historyAfterGet = historyManager.getHistory();
        assertEquals(10, historyAfterGet.size(), "В истории должно быть 10 элементов");
        assertTrue(historyAfterGet.contains(subtask1), "Полученная подзадача должна быть в истории");
        assertTrue(historyAfterGet.contains(subtask2), "Полученная подзадача должна быть в истории");
        assertTrue(historyAfterGet.contains(subtask3), "Полученная подзадача должна быть в истории");

        List<Task> comparisonHistoryAfterGet = Arrays.asList(
                initialTask,
                task2,
                initialTask,
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
                initialTask,
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

    @Test
    void testHistoryManagerVersioning() {

        Task task = manager.createTask(new Task("Купить продукты", "Хлеб яйца масло",
                manager, Status.NEW));
        int taskId = task.getId();
        Task firstVersion = manager.getTaskById(taskId);
        String originalTitle = firstVersion.getTitle();
        String originalDescription = firstVersion.getDescription();
        Status originalStatus = firstVersion.getStatus();

        String newTitle = "Купить продукты и приготовить ужин";
        String newDescription = "Хлеб, яйца, масло, овощи";
        Status newStatus = Status.IN_PROGRESS;

        Task updatedTask = new Task(newTitle, newDescription, manager, newStatus);
        updatedTask.setId(taskId);

        manager.updateTask(updatedTask);

        Task updatedFromManager = manager.getTaskById(taskId);


        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "В истории должно быть 2 версии задачи");

        Task firstHistoryEntry = history.get(0);
        assertEquals(firstVersion.getTitle(), firstVersion.getTitle(), "Заголовок первой версии не совпадает");
        assertEquals(firstVersion.getDescription(), firstVersion.getDescription(), "Описание первой версии не совпадает");
        assertEquals(firstVersion.getStatus(), Status.NEW, "Статус первой версии не совпадает");

        Task secondHistoryEntry = history.get(1);
        assertEquals(updatedFromManager.getTitle(), "Купить продукты и приготовить ужин", "Заголовок второй версии не совпадает");
        assertEquals(updatedFromManager.getDescription(), "Хлеб, яйца, масло, овощи", "Описание второй версии не совпадает");
        assertEquals(updatedFromManager.getStatus(), Status.IN_PROGRESS, "Статус второй версии не совпадает");

        assertNotEquals(firstHistoryEntry, secondHistoryEntry, "Версии должны быть разными объектами");
        assertNotEquals(firstHistoryEntry.getDescription(), secondHistoryEntry.getDescription(), "Описания версий должны отличаться");
        assertNotEquals(firstHistoryEntry.getStatus(), secondHistoryEntry.getStatus(), "Статусы версий должны отличаться");

        Task currentTask = manager.getTaskById(firstVersion.getId());
        assertEquals(currentTask.getTitle(), "Купить продукты и приготовить ужин", "В менеджере задач должна быть актуальная версия");
    }

}
