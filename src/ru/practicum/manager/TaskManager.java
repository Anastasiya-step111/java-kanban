package ru.practicum.manager;

import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.List;

public interface TaskManager {

        int getCurrentTaskCount();

        public <T extends Task> void update(T task);

        List<Task> getAllTasks();

        Task getTaskById(int id);

        Task createTask(Task task);

        void updateTask(Task task);

        boolean deleteTask(int id);

        void deleteAllTasks();

        List<Epic> getAllEpics();

        Epic getEpicById(int id);

        Epic createEpic(Epic epic);

        boolean updateEpic(Epic epic);

        boolean deleteEpic(int id);

        void deleteAllEpics();

        List<Subtask> getAllSubtasks();

        Subtask getSubtaskById(int id);

        List<Subtask> getSubtasksByEpicId(int id);

        Subtask createSubtask(Subtask subtask);

        void deleteAllSubtasks();

        boolean removeSubtaskById(int id);

        boolean updateSubtask(Subtask subtask, int id);

        List<Task> getHistory();

        HistoryManager getHistoryManager();

        List<Task> getPrioritizedTasks();

}
