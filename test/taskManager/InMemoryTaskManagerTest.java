package taskManager;

import org.junit.jupiter.api.BeforeEach;
import ru.practicum.manager.InMemoryTaskManager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void testGetCurrentTaskCount() {
        int countAfterFirstCall = taskManager.getCurrentTaskCount();
        assertTrue(countAfterFirstCall > 0, "Счётчик должен быть больше нуля после первого вызова");
    }
}
