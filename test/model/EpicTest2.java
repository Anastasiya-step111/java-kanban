package model;

import org.junit.jupiter.api.Test;
import ru.practicum.manager.HistoryManager;
import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest2 {
    TaskManager manager = Managers.getDefault();
    HistoryManager historyManager = manager.getHistoryManager();

    @Test
    void testStatusNew() {
        LocalDateTime startTime1 = LocalDateTime.of(2027, 2, 1, 1, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2027, 2, 3, 5, 0);
        Duration duration2 = Duration.ofHours(2);

        LocalDateTime startTime3 = LocalDateTime.of(2027, 2, 5, 10, 0);
        Duration duration3 = Duration.ofHours(1);

        Epic epic1 = manager.createEpic(new Epic("Учить английский", "Очень страшная задача", manager,
                Status.NEW));
        Epic epic2 = manager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", manager,
                Status.NEW));

        Subtask subtask1 = manager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы",
                manager, epic1.getId(), Status.NEW, startTime1, duration1));
        Subtask subtask2 = manager.createSubtask(new Subtask("Разбираться в IDEA", "Переводить все " +
                "встреченные слова", manager, epic1.getId(), Status.NEW, startTime2, duration2));
        Subtask subtask3 = manager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", manager, epic2.getId(), Status.NEW, startTime3, duration3));

        assertEquals(Status.NEW, epic1.getStatus(), "Cтатус эпика должен быть NEW");
        assertEquals(Status.NEW, epic2.getStatus(), "Cтатус эпика должен быть NEW");
    }

    @Test
    void testStatusDone() {
        LocalDateTime startTime1 = LocalDateTime.of(2027, 2, 1, 1, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2027, 2, 3, 5, 0);
        Duration duration2 = Duration.ofHours(2);

        LocalDateTime startTime3 = LocalDateTime.of(2027, 2, 5, 10, 0);
        Duration duration3 = Duration.ofHours(1);

        Epic epic1 = manager.createEpic(new Epic("Учить английский", "Очень страшная задача", manager,
                Status.NEW));
        Epic epic2 = manager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", manager,
                Status.NEW));

        Subtask subtask1 = manager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы",
                manager, epic1.getId(), Status.DONE, startTime1, duration1));
        Subtask subtask2 = manager.createSubtask(new Subtask("Разбираться в IDEA", "Переводить все " +
                "встреченные слова", manager, epic1.getId(), Status.DONE, startTime2, duration2));
        Subtask subtask3 = manager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", manager, epic2.getId(), Status.DONE, startTime3, duration3));

        assertEquals(Status.DONE, epic1.getStatus(), "Cтатус эпика должен быть DONE");
        assertEquals(Status.DONE, epic2.getStatus(), "Cтатус эпика должен быть DONE");
    }

    @Test
    void testStatusNewAndDone() {
        LocalDateTime startTime1 = LocalDateTime.of(2027, 2, 1, 1, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2027, 2, 3, 5, 0);
        Duration duration2 = Duration.ofHours(2);

        LocalDateTime startTime3 = LocalDateTime.of(2027, 2, 5, 10, 0);
        Duration duration3 = Duration.ofHours(1);

        LocalDateTime startTime4 = LocalDateTime.of(2027, 2, 7, 15, 0);
        Duration duration4 = Duration.ofHours(1);

        Epic epic1 = manager.createEpic(new Epic("Учить английский", "Очень страшная задача", manager,
                Status.NEW));
        Epic epic2 = manager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", manager,
                Status.NEW));

        Subtask subtask1 = manager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы",
                manager, epic1.getId(), Status.NEW, startTime1, duration1));
        Subtask subtask2 = manager.createSubtask(new Subtask("Разбираться в IDEA", "Переводить все " +
                "встреченные слова", manager, epic1.getId(), Status.DONE, startTime2, duration2));
        Subtask subtask3 = manager.createSubtask(new Subtask("Изучить вопрос на драйв2",
                "Сделать список вариантов", manager, epic2.getId(), Status.NEW, startTime3, duration3));
        Subtask subtask4 = manager.createSubtask(new Subtask("Купить масло для замены",
                "Посоветуйся с механиком", manager, epic2.getId(), Status.DONE, startTime4, duration4));

        assertEquals(Status.IN_PROGRESS, epic2.getStatus(), "Cтатус эпика должен быть IN_PROGRESS");
        assertEquals(Status.IN_PROGRESS, epic2.getStatus(), "Cтатус эпика должен быть IN_PROGRESS");
    }

    @Test
    void testStatusInProgress() {
        LocalDateTime startTime1 = LocalDateTime.of(2027, 2, 1, 1, 0);
        Duration duration1 = Duration.ofHours(1);

        LocalDateTime startTime2 = LocalDateTime.of(2027, 2, 3, 5, 0);
        Duration duration2 = Duration.ofHours(2);

        LocalDateTime startTime3 = LocalDateTime.of(2027, 2, 5, 10, 0);
        Duration duration3 = Duration.ofHours(1);

        LocalDateTime startTime4 = LocalDateTime.of(2027, 2, 7, 15, 0);
        Duration duration4 = Duration.ofHours(1);

        Epic epic1 = manager.createEpic(new Epic("Учить английский", "Очень страшная задача", manager,
                Status.NEW));
        Epic epic2 = manager.createEpic(new Epic("Разобраться с ошибкой по коробке", "Не горит", manager,
                Status.NEW));

        Subtask subtask1 = manager.createSubtask(new Subtask("Найти репетитора", "Почитать отзывы",
                manager, epic1.getId(), Status.IN_PROGRESS, startTime1, duration1));
        Subtask subtask2 = manager.createSubtask(new Subtask("Разбираться в IDEA", "Переводить все " +
                "встреченные слова", manager, epic1.getId(), Status.IN_PROGRESS, startTime2, duration2));
        Subtask subtask3 = manager.createSubtask(new Subtask("Изучить вопрос на драйв2", "Сделать " +
                "список вариантов", manager, epic2.getId(), Status.IN_PROGRESS, startTime3, duration3));

        assertEquals(Status.IN_PROGRESS, epic2.getStatus(), "Cтатус эпика должен быть IN_PROGRESS");
        assertEquals(Status.IN_PROGRESS, epic2.getStatus(), "Cтатус эпика должен быть IN_PROGRESS");
    }
}
