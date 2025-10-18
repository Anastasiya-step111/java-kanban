package ru.practicum.manager;

import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.time.LocalDateTime;
import java.util.*;

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
        historyManager.add(task);
        return task;
    }

    @Override
    public Task createTask(Task task) {
        Optional<Task> existingTask = tasks.values().stream()
                .filter(existing ->
                        Objects.equals(existing.getDescription(), task.getDescription()) &&
                                Objects.equals(existing.getTitle(), task.getTitle())
                )
                .findFirst();

        if (existingTask.isPresent()) {
            return existingTask.get();
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
            updatePriotizedTask(task);
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void deleteTask(int id) {

        prioritizedTasks.remove(getTaskById(id));
        tasks.remove(id);
    }

    @Override
    public void deleteAllTasks() {

        tasks.values().stream().forEach(task -> prioritizedTasks.remove(task));
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
        addPrioritizedTask(epic);
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
        updatePriotizedTask(epic);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);

        if (epic != null && epic.getSubtasks() != null) {
            prioritizedTasks.remove(epic);

            epic.getSubtasks()
                    .forEach(subtask -> {
                        if (subtask != null) {
                            subtasks.remove(subtask.getId());
                            prioritizedTasks.remove(subtask);
                        }
                    });
        }
    }

    @Override
    public void deleteAllEpics() {
        getAllEpics().forEach(epic -> {
            prioritizedTasks.remove(epic);

            epic.getSubtasks().forEach(subtask -> {
                subtasks.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
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
        Subtask existingSubtask = subtasks.values().stream()
                .filter(existing ->
                        Objects.equals(existing.getTitle(), subtask.getTitle()) &&
                                Objects.equals(existing.getDescription(), subtask.getDescription()) &&
                                existing.getEpicId() == subtask.getEpicId()
                )
                .findFirst()
                .orElse(null);

        if (existingSubtask != null) {
            return existingSubtask;
        }

        int id = getCurrentTaskCount();
        subtask.setId(id);
        addPrioritizedTask(subtask);
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
        subtasks.values().stream()
                .filter(Objects::nonNull)
                .peek(subtask -> prioritizedTasks.remove(subtask))
                .forEach(subtask -> {
                    Epic epic = epics.get(subtask.getEpicId());
                    if (epic != null) {
                        epic.removeSubtask(subtask);
                        updateEpic(epic);
                    }
                });

        subtasks.clear();
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        prioritizedTasks.remove(subtask);

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
        updatePriotizedTask(updatedSubtask);
        subtasks.put(updateSubtaskId, updatedSubtask);
        epic.addSubtask(updatedSubtask);
        updateEpic(epic);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        if (prioritizedTasks == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public boolean checkForConflicts(Task task) { // Метод вернет true, если есть хотя бы один конфликт
        return prioritizedTasks.stream()
                .anyMatch(existingTask -> task.isTimeConflict(existingTask));
    }

    @Override
    public void addPrioritizedTask(Task task) {
        try {
            if (task == null) {
                throw new NullPointerException("Задача не может быть null");
            }

            if (checkForConflicts(task)) {
                throw new IllegalArgumentException("Конфликт времени с существующей задачей");
            }

            prioritizedTasks.add(task);

        } catch (NullPointerException e) {
            System.err.println("Ошибка: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Произошла непредвиденная ошибка: " + e.getMessage());
        }
    }

    @Override
    public void updatePriotizedTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Задача не может быть null");
        }

        TreeSet<Task> tempTasks = new TreeSet<>(prioritizedTasks);

        tempTasks.stream()
                .filter(existingTask ->
                        existingTask.getTitle().equals(task.getTitle()) &&
                                existingTask.getDescription().equals(task.getDescription())
                )
                .forEach(existingTask -> prioritizedTasks.remove(existingTask));

        addPrioritizedTask(task);
    }

}
