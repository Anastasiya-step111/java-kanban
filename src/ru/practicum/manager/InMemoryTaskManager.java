package ru.practicum.manager;

import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    protected static int taskCount = 1;

    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();

    protected HistoryManager historyManager;

    private final TreeSet<Task> prioritizedTasks = new TreeSet<>((task1, task2) -> {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime start2 = task2.getStartTime();

        if (start1 == null && start2 == null) {
            return Integer.compare(task1.getId(), task2.getId());
        }
        if (start1 == null) {
            return 1;
        }
        if (start2 == null) {
            return -1;
        }
        int result = start1.compareTo(start2);
        return result != 0 ? result : Integer.compare(task1.getId(), task2.getId());
    });

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
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Task createTask(Task task) {
        Optional<Task> existingTask = tasks.values().stream()
                .filter(existing ->
                        Objects.equals(existing.getDescription(), task.getDescription()) &&
                                Objects.equals(existing.getTitle(), task.getTitle()) &&
                                Objects.equals(existing.getStartTime(), task.getStartTime()) &&
                                Objects.equals(existing.getDuration(), task.getDuration())
                )
                .findFirst();

        if (existingTask.isPresent()) {
            return existingTask.get();
        }
        Task conflictingTask = findConflictingTask(task);
        if (conflictingTask != null) {
            String conflictMessage = String.format(
                    "Подзадача пересекается по времени с существующей задачей: %s (%s - %s)",
                    conflictingTask.getTitle(),
                    conflictingTask.getStartTime(),
                    conflictingTask.getEndTime()
            );
            throw new ManagerSaveException(conflictMessage);
        }
        int id = getCurrentTaskCount();
        task.setId(id);
        addPrioritizedTask(task);
        tasks.put(id, task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task oldTask = getTaskById(task.getId());

            removeTaskFromPrioritized(oldTask);
            addPrioritizedTask(task);
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public boolean deleteTask(int id) {
        Task removed = tasks.remove(id);
        if (removed != null) {
            removeTaskFromPrioritized(removed);
            return true;
        }
        return false;
    }

    @Override
    public void deleteAllTasks() {
        List<Task> taskList = new ArrayList<>(getAllTasks());

        taskList.stream()
                .forEach(task -> {
                    removeTaskFromPrioritized(task);
                });

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
        Epic existingEpic = epics.values().stream()
                .filter(existing ->
                        Objects.equals(existing.getDescription(), epic.getDescription()) &&
                                Objects.equals(existing.getTitle(), epic.getTitle())
                )
                .findFirst()
                .orElse(null);

        if (existingEpic != null) {
            return existingEpic;
        }
        int id = getCurrentTaskCount();
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return false;
        }

        List<Subtask> subtasksInEpic = epic.getSubtasks();

        if (subtasksInEpic.isEmpty()) {
            epic.setStatus(Status.NEW);
            epics.put(epic.getId(), epic);
            return true;
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
        return true;
    }

    @Override
    public boolean deleteEpic(int id) {
        Epic epic = epics.remove(id);

        if (epic != null) {
            return false;
        }

        if (epic != null && epic.getSubtasks() != null) {

            epic.getSubtasks()
                    .stream()
                    .filter(Objects::nonNull) // Фильтруем null значения
                    .forEach(subtask -> {
                        subtasks.remove(subtask.getId());
                        removeTaskFromPrioritized(subtask);
                    });
        }
        return true;
    }

    @Override
    public void deleteAllEpics() {
        getAllEpics().forEach(epic -> {
            epic.getSubtasks().forEach(subtask -> {
                subtasks.remove(subtask.getId());
                removeTaskFromPrioritized(subtask);
            });
        });

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
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        return epic.getSubtasks();
    }

    @Override
    public Subtask createSubtask(Subtask newSubtask) {
        Subtask existingSubtask = subtasks.values().stream()
                .filter(subtask ->
                        Objects.equals(subtask.getTitle(), newSubtask.getTitle()) &&
                                Objects.equals(subtask.getDescription(), newSubtask.getDescription()) &&
                                subtask.getEpicId() == newSubtask.getEpicId() &&
                                Objects.equals(subtask.getStartTime(), newSubtask.getStartTime()) &&
                                Objects.equals(subtask.getDuration(), newSubtask.getDuration())
                )
                .findFirst()
                .orElse(null);

        if (existingSubtask != null) {
            return existingSubtask;
        }
        Task conflictingTask = findConflictingTask(newSubtask);
        if (conflictingTask != null) {
            String conflictMessage = String.format(
                    "Подзадача пересекается по времени с существующей задачей: %s (%s - %s)",
                    conflictingTask.getTitle(),
                    conflictingTask.getStartTime(),
                    conflictingTask.getEndTime()
            );
            throw new ManagerSaveException(conflictMessage);
        }
        int id = getCurrentTaskCount();
        newSubtask.setId(id);
        addPrioritizedTask(newSubtask);
        subtasks.put(id, newSubtask);

        int epicId = newSubtask.getEpicId();
        Epic epic = epics.get(epicId);

        if (epic != null) {
            epic.addSubtask(newSubtask);
            updateEpic(epic);
        }

        return newSubtask;
    }


    @Override
    public void deleteAllSubtasks() {
        List<Integer> subtaskIds = new ArrayList<>(subtasks.keySet());

        subtaskIds.stream()
                .map(subtasks::remove)
                .filter(Objects::nonNull)
                .forEach(subtask -> {
                    removeTaskFromPrioritized(subtask);
                    int epicId = subtask.getEpicId();
                    Epic epic = epics.get(epicId);

                    if (epic != null) {
                        epic.removeSubtask(subtask);
                        updateEpic(epic);
                    }
                });

        subtasks.clear();
    }

    @Override
    public boolean removeSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        removeTaskFromPrioritized(subtask);

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
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean updateSubtask(Subtask updatedSubtask, int updateSubtaskId) {
        if (updatedSubtask == null) {
            return false;
        }

        int epicId = updatedSubtask.getEpicId();
        Epic epic = epics.get(epicId);

        if (epic == null) {
            throw new IllegalArgumentException("Эпик с ID " + epicId + " не найден");
        }

        epic.getSubtasks().removeIf(subtask -> subtask.getId() == updateSubtaskId);

        updatedSubtask.setId(updateSubtaskId);

        updatePrioritizedTask(updatedSubtask);
        subtasks.put(updateSubtaskId, updatedSubtask);
        epic.addSubtask(updatedSubtask);
        updateEpic(epic);
        return true;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isTimeConflict(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime end2 = task2.getEndTime();

        return !end1.isBefore(task2.getStartTime()) && !end2.isBefore(task1.getStartTime());
    }

    private Task findConflictingTask(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getEndTime() == null) {
            return null;
        }

        return getPrioritizedTasks()
                .stream()
                .filter(task -> isTimeConflict(task, newTask))
                .findFirst()
                .orElse(null);
    }

    private void addPrioritizedTask(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeTaskFromPrioritized(Task task) {
        prioritizedTasks.remove(task);
    }

    private void updatePrioritizedTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Задача не может быть null");
        }

        TreeSet<Task> tempTasks = new TreeSet<>(prioritizedTasks);

        tempTasks.stream()
                .filter(existingTask ->
                        existingTask.getTitle().equals(task.getTitle()) &&
                                existingTask.getDescription().equals(task.getDescription())
                )
                .forEach(this::removeTaskFromPrioritized);

        Task conflictingTask = findConflictingTask(task);
        if (conflictingTask != null) {
            String conflictMessage = String.format(
                    "Подзадача пересекается по времени с существующей задачей: %s (%s - %s)",
                    conflictingTask.getTitle(),
                    conflictingTask.getStartTime(),
                    conflictingTask.getEndTime()
            );
            throw new ManagerSaveException(conflictMessage);
        }
        addPrioritizedTask(task);
    }

}