package model;

import ru.practicum.manager.HistoryManager;
import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    TaskManager manager = Managers.getDefault();
    HistoryManager historyManager = manager.getHistoryManager();
    Epic epic1;
    Epic epic2;
    Epic epicDuplicate1;

    Subtask subtask1;
    Subtask subtask2;
    Subtask subtask3;
    Subtask subtaskDuplicate1;


    @BeforeEach
    public void beforeEach() {
        epic1 = manager.createEpic(new Epic("Учить английский", "Очень страшная задача", manager,
                Status.NEW));
        epicDuplicate1 = manager.createEpic(new Epic("Учить английский", "Очень страшная задача", manager,
                Status.NEW));
        epic2 = manager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", manager,
                Status.NEW));


        subtask1 = manager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы", manager,
                epic1.getId(), Status.NEW));
        subtaskDuplicate1 = manager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы", manager,
                epicDuplicate1.getId(), Status.NEW));
        subtask2 = manager.createSubtask(new Subtask("Разбираться в IDEA",
                "Переводить все встреченные слова", manager, epic1.getId(), Status.NEW));
        subtask3 = manager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", manager, epic2.getId(), Status.NEW));
    }

    @Test
    void testGetSubtasks() {
        List<Subtask> allSubtasks = epic1.getSubtasks();

        assertEquals(2, allSubtasks.size(), "В списке должно быть 2 подзадачи для epic1");
        assertTrue(allSubtasks.contains(subtask1), "Список должен содержать первую подзадачу");
        assertTrue(allSubtasks.contains(subtask2), "Список должен содержать вторую подзадачу");
        assertFalse(allSubtasks.contains(subtask3), "Список не должен содержать подзадачи другого эпика");
    }

    @Test
    void testAddSubtask() {
        List<Subtask> allSubtasks = epic1.getSubtasks();
        Subtask newSubtask = manager.createSubtask(
                new Subtask("Новая подзадача", "Новое описание", manager, epic1.getId(), Status.NEW));
        List<Subtask> updatedList = epic1.getSubtasks();

        assertEquals(3, updatedList.size(), "Размер списка должен увеличиться на 1");
        assertTrue(updatedList.contains(newSubtask), "Добавленная подзадача должна быть в списке");
    }

    @Test
    void testCannotAddEpicToItselfAsSubtask() {
        assertThrows(IllegalArgumentException.class, () -> {
            epic1.addSubtask(epic1);
        }, "Должна быть ошибка при попытке добавить эпик как подзадачу самому себе");

        assertEquals(2, epic1.getSubtasks().size(), "Список подзадач не должен измениться");
    }

    @Test
    void testRemoveSubtask() {
        List<Subtask> initialList = epic1.getSubtasks();
        assertEquals(2, initialList.size(), "Начальный размер должен быть 2");

        epic1.removeSubtask(subtask1);
        List<Subtask> updatedList = epic1.getSubtasks();
        assertEquals(1, updatedList.size(), "Размер должен уменьшиться на 1");
        assertFalse(updatedList.contains(subtask1), "Удаленная подзадача не должна быть в списке");

        epic1.removeSubtask(subtask3);
        assertEquals(1, updatedList.size(), "Удаление несуществующей подзадачи не должно менять размер");

        epic1.removeSubtask(subtask2);
        assertTrue(epic1.getSubtasks().isEmpty(), "После удаления всех подзадач список должен быть пустым");
    }

    @Test
    void testEpicEquals() {
        assertTrue(epic1.equals(epic1), "Объект должен быть равен самому себе");

        assertTrue(epic1.equals(epicDuplicate1), "Идентичные эпики должны быть равны");
        assertTrue(epicDuplicate1.equals(epic1), "Равенство должно быть симметричным");

        assertFalse(epic1.equals(null), "Объект не должен быть равен null");
        assertFalse(epic1.equals(new Object()), "Объект другого типа не должен быть равен");

        assertFalse(epic1.equals(epic2), "Эпики с разными полями не должны быть равны");
    }

    @Test
    void testEpicHashCode() {
        assertEquals(epic1.hashCode(), epicDuplicate1.hashCode(),
                "Равные объекты должны иметь одинаковые хэш-коды");
        assertNotEquals(epic1.hashCode(), epic2.hashCode(),
                "Разные объекты должны иметь разные хэш-коды");
    }

    @Test
    void testCreateEpic() {
        assertNotNull(epic1, "Эпик должен быть создан");
        assertNotEquals(0, epic1.getId(), "ID эпика не должен быть нулевым");
        assertEquals(epic1.getId(), epicDuplicate1.getId(), "Id должны совпасть");

        Epic epicWithEmptyDescription = new Epic("Пустой эпик", "", manager, Status.NEW);
        Epic createdEmptyDescriptionEpic = manager.createEpic(epicWithEmptyDescription);
        assertNotNull(createdEmptyDescriptionEpic, "Должен создать эпик с пустым описанием");

        Epic epicWithEmptyTitle = new Epic("", "Описание пустого эпика", manager, Status.NEW);
        Epic createdEmptyTitleEpic = manager.createEpic(epicWithEmptyTitle);
        assertNotNull(createdEmptyTitleEpic, "Должен создать эпик с пустым названием");

        List<Epic> finalList = manager.getAllEpics();
        assertTrue(finalList.contains(epic1), "Новый эпик должен быть в системе");
        assertTrue(finalList.contains(createdEmptyDescriptionEpic), "Эпик с пустым описанием должен быть в системе");
        assertTrue(finalList.contains(createdEmptyTitleEpic), "Эпик с пустым названием должен быть в системе");
    }

    @Test
    void testGetEpicById() {
        Epic retrievedEpic1 = manager.getEpicById(epic1.getId());
        assertNotNull(retrievedEpic1, "Должен вернуть существующий эпик");
        assertEquals(epic1, retrievedEpic1, "Полученный эпик должен совпадать с исходным");

        Epic retrievedEpic2 = manager.getEpicById(epic2.getId());
        assertNotNull(retrievedEpic2, "Должен вернуть существующий эпик");
        assertEquals(epic2, retrievedEpic2, "Полученный эпик должен совпадать с исходным");

        Epic nonExistingEpic = manager.getEpicById(9999);
        assertNull(nonExistingEpic, "Должен вернуть null для несуществующего ID");

        Epic zeroIdEpic = manager.getEpicById(0);
        assertNull(zeroIdEpic, "Должен вернуть null для ID=0");

        assertEquals(Status.NEW, retrievedEpic1.getStatus(), "Статус эпика должен быть корректным");

        manager.deleteEpic(epic1.getId());
        Epic deletedEpic = manager.getEpicById(epic1.getId());
        assertNull(deletedEpic, "После удаления должен возвращать null");

        List<Task> historyAfterGet = historyManager.getHistory();
        assertEquals(2, historyAfterGet.size(), "В истории должно быть два элемента");
        assertTrue(historyAfterGet.contains(epic2), "Полученный эпик должен быть в истории");
    }

    @Test
    void testUpdateEpicStatus() {
        assertEquals(Status.NEW, epic1.getStatus(), "Исходный статус эпика должен быть NEW");

        String newTitle1 = "Обновленное название";
        String newDescription1 = "Новое описание";
        Status newStatus1 = Status.IN_PROGRESS;
        int epicId1 = subtask1.getEpicId();

        Subtask updatedSubtask1 = new Subtask(
                newTitle1,
                newDescription1,
                manager,
                epicId1,
                newStatus1
        );

        manager.updateSubtask(updatedSubtask1, subtask1.getId());
        assertEquals(Status.IN_PROGRESS, epic1.getStatus(), "Статус эпика должен обновиться");

        String newTitle2 = "Обновленное название";
        String newDescription2 = "Новое описание";
        Status newStatus2 = Status.DONE;

        Subtask updatedSubtask2 = new Subtask(
                newTitle2,
                newDescription2,
                manager,
                subtask2.getEpicId(),
                newStatus2
        );

        manager.updateSubtask(updatedSubtask2, subtask2.getId());
        assertEquals(Status.IN_PROGRESS, epic1.getStatus(), "Статус эпика не должен измениться");

        String newTitle3 = "Обновленное название";
        String newDescription3 = "Новое описание";
        Status newStatus3 = Status.DONE;

        Subtask updatedSubtask3 = new Subtask(
                newTitle3,
                newDescription3,
                manager,
                subtask1.getEpicId(),
                newStatus3
        );

        manager.updateSubtask(updatedSubtask3, subtask1.getId());
        assertEquals(2, epic1.getSubtasks().size(), "подзадач должно быть 2");
        assertEquals(Status.DONE, epic1.getStatus(), "Статус эпика должен обновиться");
    }

    @Test
    void testEpicSetTitle() {
        assertEquals("Учить английский", epic1.getTitle());

        int epicId = epic1.getId();

        epic1.setTitle("Изучать английский язык");

        assertEquals("Изучать английский язык", epic1.getTitle());

        Epic updatedEpic = manager.getEpicById(epicId);
        assertNotNull(updatedEpic);
        assertEquals("Изучать английский язык", updatedEpic.getTitle());
    }

    @Test
    void testEpicSetDescription() {

        assertEquals("Очень страшная задача", epic1.getDescription());

        int epicId = epic1.getId();

        epic1.setDescription("Очень важная и страшная задача");

        assertEquals("Очень важная и страшная задача", epic1.getDescription());

        Epic updatedEpic = manager.getEpicById(epicId);
        assertNotNull(updatedEpic);
        assertEquals("Очень важная и страшная задача", updatedEpic.getDescription());
    }

    @Test
    void testEpicSetStatus() {
        assertEquals(Status.NEW, epic1.getStatus());

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask1, subtask1.getId());
        manager.updateSubtask(subtask2, subtask2.getId());

        assertEquals(Status.DONE, epic1.getStatus());

        subtask1.setStatus(Status.NEW);
        manager.updateSubtask(subtask1, subtask1.getId());

        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }
}
