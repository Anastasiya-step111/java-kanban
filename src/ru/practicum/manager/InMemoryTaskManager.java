package ru.practicum.manager;

import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected static int taskCount = 1;

    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();

    protected HistoryManager historyManager;

    public InMemoryTaskManager() {

        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public int getCurrentTaskCount() {
        taskCount++;
        return taskCount;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getHistory() {

        return historyManager.getHistory();
    }

    @Override
    public <T extends Task> void update(T task) {
        if (task instanceof Task) {
            updateTask(task);
        } else if (task instanceof Epic) {
            updateEpic((Epic) task);
        } else if (task instanceof Subtask) {
            updateSubtask((Subtask) task, task.getId());
        } else {
            throw new IllegalArgumentException("Неизвестный тип задачи");
        }
    }

    @Override
    public List<Task> getAllTasks() {

        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Task createTask(Task task) {
        for (Task existingTask : tasks.values()) {
            if ((Objects.equals(existingTask.getDescription(), task.getDescription()) &&
                    Objects.equals(existingTask.getTitle(), task.getTitle()))) {

                return existingTask;
            }
        }

        int id = getCurrentTaskCount();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void deleteTask(int id) {

        tasks.remove(id);
    }

    @Override
    public void deleteAllTasks() {

        tasks.clear();
    }

    @Override
    public List<Epic> getAllEpics() {

        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        for (Epic existingEpic : epics.values()) {
            if (Objects.equals(existingEpic.getDescription(), epic.getDescription()) &&
                    Objects.equals(existingEpic.getTitle(), epic.getTitle())) {

                return existingEpic;
            }
        }

        int id = getCurrentTaskCount();
        epic.setId(id);
        epics.put(id, epic);

        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return;
        }

        List<Subtask> subtasksInEpic = epic.getSubtasks();

        if (subtasksInEpic.isEmpty()) {
            epic.setStatus(Status.NEW);
            epics.put(epic.getId(), epic);
            return;
        }

        int subtaskCount = subtasksInEpic.size();
        int subtaskCountNew = 0;
        int subtaskCountDone = 0;

        for (Subtask subtask : subtasksInEpic) {
            Status subtaskStatus = subtask.getStatus();

            if (subtaskStatus == Status.NEW) {
                subtaskCountNew++;
            } else if (subtaskStatus == Status.DONE) {
                subtaskCountDone++;
            }
        }

        Status newStatus;
        if (subtaskCountNew == subtaskCount) {
            newStatus = Status.NEW;
        } else if (subtaskCountDone == subtaskCount) {
            newStatus = Status.DONE;
        } else {
            newStatus = Status.IN_PROGRESS;
        }

        epic.setStatus(newStatus);
        epic.setStartTime(epic.getStartTime());
        epic.setDuration(epic.getDuration());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
        }
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public List<Subtask> getAllSubtasks() {

        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int id) {
        List<Subtask> result = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getId() == id) {
                result.add(subtask);
            }
        }
        return result;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        for (Subtask existingSubtask : subtasks.values()) {
            if (Objects.equals(existingSubtask.getTitle(), subtask.getTitle()) &&
                    Objects.equals(existingSubtask.getDescription(), subtask.getDescription()) &&
                    existingSubtask.getEpicId() == subtask.getEpicId()) {
                return existingSubtask;
            }
        }

        int id = getCurrentTaskCount();
        subtask.setId(id);
        subtasks.put(id, subtask);

        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);

        if (epic != null) {
            epic.addSubtask(subtask);
            updateEpic(epic);
        }

        return subtask;
    }


    @Override
    public void deleteAllSubtasks() {
        ArrayList<Integer> subtaskIds = new ArrayList<>(subtasks.keySet());

        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = subtasks.remove(subtaskId);

            if (subtask != null) {
                int epicId = subtask.getEpicId();
                Epic epic = epics.get(epicId);
                if (epic != null) {
                    epic.removeSubtask(subtask);
                    updateEpic(epic);
                }
            }
        }

        subtasks.clear();
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);

        if (subtask != null) {
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);

            if (epic != null) {
                Status oldStatus = epic.getStatus();
                epic.removeSubtask(subtask);
                updateEpic(epic);

                    if (!oldStatus.equals(epic.getStatus())) {
                        epics.put(epic.getId(), epic);
                    }
            }
        }
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask, int updateSubtaskId) {
        int epicId = updatedSubtask.getEpicId();
        Epic epic = epics.get(epicId);

        if (epic == null) {
            throw new IllegalArgumentException("Эпик с ID " + epicId + " не найден");
        }

        epic.getSubtasks().removeIf(subtask -> subtask.getId() == updateSubtaskId);



        updatedSubtask.setId(updateSubtaskId);
        subtasks.put(updateSubtaskId, updatedSubtask);
        epic.addSubtask(updatedSubtask);
        updateEpic(epic);
    }
}
