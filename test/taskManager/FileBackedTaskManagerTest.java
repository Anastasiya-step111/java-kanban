package taskManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.manager.FileBackedTaskManager;
import ru.practicum.manager.HistoryManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;

    @BeforeEach
    void beforeEach() {
        assertDoesNotThrow(() -> {
            tempFile = Files.createTempFile("tasks", ".csv").toFile();
            taskManager = new FileBackedTaskManager(tempFile);
        });
    }

    @AfterEach
    void afterEach() {
        if (tempFile != null && tempFile.exists()) {
            assertTrue(tempFile.delete(), "Не удалось удалить временный файл");
        }
    }

    @Test
    void testSaveAndLoadTasksCorrectly() {
        HistoryManager historyManager = taskManager.getHistoryManager();
        LocalDateTime startTime1 = LocalDateTime.of(2027, 10, 1, 8, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2027, 10, 3, 9, 0);
        Duration duration2 = Duration.ofHours(2);

        Task task = taskManager.createTask(new Task("Купить продукты", "Хлеб яйца масло", taskManager, Status.NEW,
                startTime1, duration1));
        int taskId = task.getId();

        Epic epic = taskManager.createEpic(new Epic("Учить английский", "Очень страшная задача", taskManager,
                Status.NEW));
        int epicId = epic.getId();

        Subtask subtask = taskManager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы", taskManager,
                epic.getId(), Status.NEW, startTime2, duration2));
        int subtaskId = subtask.getId();

        Subtask subtaskDublicate = taskManager.getSubtaskById(subtaskId);
        Task taskDublicate = taskManager.getTaskById(taskId);
        Epic epicDuplicate = taskManager.getEpicById(epicId);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "В истории должно быть 3 элементов");

        assert tempFile.exists() : "Файл не найден";
        assert tempFile.length() > 0 : "Файл пуст";


        taskManager.loadFromFile(tempFile);

        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals(1, taskManager.getAllEpics().size());
        assertEquals(1, taskManager.getAllSubtasks().size());

        Task loadedTask = taskManager.getAllTasks().get(0);
        assertEquals(taskId, loadedTask.getId());
        assertEquals("Купить продукты", loadedTask.getTitle());
        assertEquals(startTime1, loadedTask.getStartTime());
        assertEquals(duration1, loadedTask.getDuration());

        Epic loadedEpic = taskManager.getAllEpics().get(0);
        assertEquals(epicId, loadedEpic.getId());
        assertEquals("Учить английский", loadedEpic.getTitle());

        Subtask loadedSubtask = taskManager.getAllSubtasks().get(0);
        assertEquals(subtaskId, loadedSubtask.getId());
        assertEquals("Найти репетитора", loadedSubtask.getTitle());
        assertEquals(epicId, loadedSubtask.getEpicId());
        assertEquals(startTime2, loadedSubtask.getStartTime());
        assertEquals(duration2, loadedSubtask.getDuration());

        List<Task> history1 = taskManager.getHistory();
        assertEquals(3, history1.size(), "История должна содержать ровно 3 элемента");
    }


}
