package taskManager;

import org.junit.jupiter.api.Test;
import ru.practicum.manager.HistoryManager;
import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void getDefaultReturnsTaskManager() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "getDefault() должен возвращать рабочий TaskManager");
    }

    @Test
    void getDefaultHistoryReturnsHistoryManager() {
        HistoryManager history = Managers.getDefaultHistory();
        assertNotNull(history, "getDefaultHistory() должен возвращать рабочий HistoryManager");
    }

    @Test
    void getFileBackedManager() throws IOException {
        File tempFile = File.createTempFile("test", ".csv");
        TaskManager manager2 = Managers.getFileBackedManager(tempFile);
        assertNotNull(manager2, "getDefaultHistory() должен возвращать рабочий FileBackedManager");
    }
}

