import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    TaskManager manager = Managers.getDefault();
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
        assertEquals(originalStatus, retrievedStatus, "Status не совпадает при получении через менеджер");
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
        // экземпляры класса таск равны если их id одинаков
        assertEquals(task1.getId(), task3.getId(), "ID одинаковых экземпляров класса Task должны совпадать");
        assertTrue(task1.equals(task3), "Задачи с одинаковым ID должны быть равны");
    }
}

