package model;

import ru.practicum.manager.HistoryManager;
import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


class SubtaskTest {
    TaskManager manager = Managers.getDefault();
    HistoryManager historyManager = manager.getHistoryManager();
    Epic epic1;
    Epic epic2;

    Subtask subtask1;
    Subtask subtask2;
    Subtask subtask3;

    LocalDateTime startTime1 = LocalDateTime.of(2027, 10, 1, 8, 0);
    Duration duration1 = Duration.ofHours(1);

    LocalDateTime startTime2 = LocalDateTime.of(2027, 10, 3, 9, 0);
    Duration duration2 = Duration.ofHours(2);

    LocalDateTime startTime3 = LocalDateTime.of(2027, 10, 5, 11, 0);
    Duration duration3 = Duration.ofHours(1);

    LocalDateTime startTime4 = LocalDateTime.of(2027, 10, 7, 12, 0);
    Duration duration4 = Duration.ofHours(1);

    LocalDateTime startTime5 = LocalDateTime.of(2027, 10, 9, 13, 0);
    Duration duration5 = Duration.ofHours(2);

    @BeforeEach
    public void beforeEach() {
        epic1 = manager.createEpic(new Epic("Учить английский", "Очень страшная задача", manager,
                Status.NEW));
        epic2 = manager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", manager,
                Status.NEW));


        subtask1 = manager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы", manager,
                epic1.getId(), Status.NEW, startTime1, duration1));
        subtask2 = manager.createSubtask(new Subtask("Разбираться в IDEA", "Переводить все встреченные " +
                "слова", manager, epic1.getId(), Status.NEW, startTime2, duration2));
        subtask3 = manager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", manager, epic2.getId(), Status.NEW, startTime3, duration3));
    }

    @Test
    void testSubtaskEquals() {
        assertTrue(subtask1.equals(subtask1), "Объект должен быть равен самому себе");

        Epic epicDuplicate1 = manager.createEpic(new Epic("Учить английский", "Очень страшная задача",
                manager, Status.NEW));
        Subtask subtaskDuplicate1 = manager.createSubtask(new Subtask("Найти репетитора",
                "Почитать отзывы", manager, epicDuplicate1.getId(), Status.NEW,  startTime1, duration1));

        assertTrue(subtask1.equals(subtaskDuplicate1), "Идентичные подзадачи должны быть равны");
        assertEquals(subtask1.getId(), subtaskDuplicate1.getId(), "IDs идентичных подзадач должны совпадать");
        assertTrue(subtaskDuplicate1.equals(subtask1), "Равенство должно быть симметричным");

        assertFalse(subtask1.equals(null), "Объект не должен быть равен null");
        assertFalse(subtask1.equals(new Object()), "Объект другого типа не должен быть равен");

        assertFalse(subtask1.equals(subtask2), "Подзадачи с разными полями не должны быть равны");
    }

    @Test
    void testSetEpicIdValidation() {
        subtask1.setEpicId(epic1.getId());
        assertEquals(epic1.getId(), subtask1.getEpicId(), "Валидный Id должен добавляться");

        assertThrows(IllegalArgumentException.class, () -> {
            subtask2.setEpicId(9999); // Не существующий ID
        }, "Должна быть ошибка для несуществующего ID");

        subtask2.setEpicId(epic2.getId());
        assertEquals(epic2.getId(), subtask2.getEpicId(), "Id существующего эпика должно добавляться");

        assertThrows(IllegalArgumentException.class, () -> {
            subtask3.setEpicId(0);
        }, "Должна быть ошибка для нулевого ID");

        int idSubtask3 = subtask2.getId();
        assertThrows(IllegalArgumentException.class, () -> {
            subtask2.setEpicId(idSubtask3);
        }, "Должна быть ошибка при добавлении Id принадлежащем объекту model.Subtask");
    }

    @Test
    void testGetAllSubtasks() {
        List<Subtask> allSubtasks = manager.getAllSubtasks();

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
        Subtask invalidSubtask = manager.getSubtaskById(9999);
        assertNull(invalidSubtask, "Должен быть возвращен null");
        assertTrue(historyManager.getHistory().isEmpty(),
                "В историю не должно быть добавлено ничего");

        Subtask retrievedSubtask = manager.getSubtaskById(subtask1.getId());
        assertNotNull(retrievedSubtask, "Должна быть возвращена подзадача");
        assertEquals(subtask1, retrievedSubtask, "Должна быть возвращена та же подзадача");
        assertTrue(historyManager.getHistory().contains(retrievedSubtask),
                "Задача должна быть добавлена в историю");
    }

    @Test
    void testSubtaskRemoveById() {
        List<Subtask> initialList = manager.getAllSubtasks();
        assertEquals(3, initialList.size(), "В списке должно быть 3 подзадачи");

        manager.removeSubtaskById(subtask2.getId());

        List<Subtask> updatedList = manager.getAllSubtasks();
        assertEquals(2, updatedList.size(), "После удаления должна остаться 2 подзадачи");
        assertFalse(updatedList.contains(subtask2), "Удаленная подзадача не должна быть в списке");

        assertTrue(updatedList.contains(subtask1));
        assertTrue(updatedList.contains(subtask3));
    }

    @Test
    void testCreateSubtask() {
        assertNotNull(subtask1, "Подзадача должна быть создана");
        assertNotEquals(0, subtask1.getId(), "ID подзадачи не должен быть нулевым");

        Subtask subtaskDuplicate1 = manager.createSubtask(new Subtask("Найти репетитора",
                "Почитать отзывы", manager, epic1.getId(), Status.NEW, startTime1, duration1));
        assertEquals(subtask1, subtaskDuplicate1, "При добавлении идентичной подзадачи должна вернуться " +
                "существующая подзадача");

        Subtask emptyDescriptionSubtask = manager.createSubtask(
                new Subtask("Подзадача без описания", "", manager, epic2.getId(), Status.NEW,
                        startTime4, duration4));

        assertNotNull(emptyDescriptionSubtask, "Должна создать подзадачу с пустым описанием");

        Subtask emptyTitleSubtask = manager.createSubtask(
                new Subtask("", "Описание подзадачи без названия", manager, epic2.getId(), Status.NEW,
                        startTime5, duration5));

        assertNotNull(emptyTitleSubtask, "Должна создать подзадачу с пустым названием");

        List<Subtask> allSubtasks = manager.getAllSubtasks();
        assertTrue(allSubtasks.contains(subtask2), "Новая подзадача должна быть в списке");
        assertTrue(allSubtasks.contains(emptyDescriptionSubtask), "Подзадача с пустым описанием " +
                "должна быть в списке");
        assertTrue(allSubtasks.contains(emptyTitleSubtask), "Подзадача с пустым названием должна быть " +
                "в списке");

        Epic epic = manager.getEpicById(epic1.getId());
        assertTrue(epic.getSubtasks().contains(subtask2), "Подзадача должна быть добавлена в эпик");
    }

    @Test
    void testDeleteAllSubtasks() {
        List<Subtask> initialSubtasks = manager.getAllSubtasks();
        assertEquals(3, initialSubtasks.size(), "Изначально должно быть 3 подзадачи");

        assertTrue(manager.getAllSubtasks().contains(subtask1));
        assertTrue(manager.getAllSubtasks().contains(subtask2));
        assertTrue(manager.getAllSubtasks().contains(subtask3));

        assertTrue(epic1.getSubtasks().contains(subtask1));
        assertTrue(epic1.getSubtasks().contains(subtask2));
        assertTrue(epic2.getSubtasks().contains(subtask3));

        manager.deleteAllSubtasks();

        assertEquals(0, manager.getAllSubtasks().size(), "После удаления должно быть 0 подзадач");

        assertTrue(epic1.getSubtasks().isEmpty(), "В эпике 1 не должно быть подзадач");
        assertTrue(epic2.getSubtasks().isEmpty(), "В эпике 2 не должно быть подзадач");

        assertNull(manager.getSubtaskById(subtask1.getId()));
        assertNull(manager.getSubtaskById(subtask2.getId()));
        assertNull(manager.getSubtaskById(subtask3.getId()));
    }

    @Test
    void testUpdateSubtask() {
        assertEquals(Status.NEW, epic1.getStatus(), "Исходный статус эпика должен быть NEW");

        String originalTitle = subtask1.getTitle();
        String originalDescription = subtask1.getDescription();

        String newTitle = "Обновленное название";
        String newDescription = "Новое описание";
        Status newStatus = Status.DONE;

        Subtask updatedSubtask = new Subtask(
                newTitle,
                newDescription,
                manager,
                subtask1.getEpicId(),
                newStatus,
                startTime4,
                duration4
        );

        manager.updateSubtask(updatedSubtask, subtask1.getId());

        Subtask retrievedSubtask = manager.getSubtaskById(subtask1.getId());
        assertNotNull(retrievedSubtask, "Подзадача должна существовать после обновления");

        assertEquals(newTitle, retrievedSubtask.getTitle(), "Название не обновилось");
        assertEquals(newDescription, retrievedSubtask.getDescription(), "Описание не обновилось");
        assertEquals(newStatus, retrievedSubtask.getStatus(), "Статус не обновился");

        assertNotEquals(originalTitle, retrievedSubtask.getTitle());
        assertNotEquals(originalDescription, retrievedSubtask.getDescription());

        Epic retrievedEpic = manager.getEpicById(retrievedSubtask.getEpicId());

        assertEquals(Status.IN_PROGRESS, retrievedEpic.getStatus(), "Статус эпика должен обновиться");
    }

    @Test
    void testSubtaskSetTitle() {
        assertEquals("Найти репетитора", subtask1.getTitle());

        subtask1.setTitle("Искать преподавателя");

        assertEquals("Искать преподавателя", subtask1.getTitle());

        Subtask updatedSubtask = manager.getSubtaskById(subtask1.getId());
        assertEquals("Искать преподавателя", updatedSubtask.getTitle());
    }

    @Test
    void testSubtaskSetDescription() {
        assertEquals("Почитать отзывы", subtask1.getDescription());

        subtask1.setDescription("Найти отзывы на сайтах");

        assertEquals("Найти отзывы на сайтах", subtask1.getDescription());

        Subtask updatedSubtask = manager.getSubtaskById(subtask1.getId());
        assertEquals("Найти отзывы на сайтах", updatedSubtask.getDescription());
    }

    @Test
    void testSubtaskSetStatus() {
        assertEquals(Status.NEW, subtask1.getStatus());

        subtask1.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, subtask1.getStatus());

        Subtask updatedSubtask = manager.getSubtaskById(subtask1.getId());
        assertEquals(Status.IN_PROGRESS, updatedSubtask.getStatus());
    }
}
