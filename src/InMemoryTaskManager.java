import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private static int taskCount = 1;

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public int getCurrentTaskCount() {
        taskCount++;
        return taskCount;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
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
    public void createTask(Task task) {
        task.setId(getCurrentTaskCount());
        tasks.put(task.getId(), task);
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
    public ArrayList<Epic> getAllEpics() {
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
    public void createEpic(Epic epic) {
        epic.setId(getCurrentTaskCount());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return;
        }

        ArrayList<Subtask> subtasks = epic.getSubtasks();

        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            epics.put(epic.getId(), epic);
            return;
        }

        boolean allNew = true;
        boolean hasInProgress = false;
        boolean allDone = true;

        for (Subtask subtask : subtasks) {
            Status subtaskStatus = subtask.getStatus();
            if (subtaskStatus != Status.NEW) {
                allNew = false;
            }
            if (subtaskStatus == Status.IN_PROGRESS) {
                hasInProgress = true;
            }
            if (subtaskStatus == Status.DONE) {
                allDone = false;
            }
        }

        Status newStatus;
        if (hasInProgress) {
            newStatus = Status.IN_PROGRESS;
        } else if (allDone) {
            newStatus = Status.DONE;
        } else if (allNew) {
            newStatus = Status.NEW;
        } else {
            newStatus = Status.NEW;
        }

        epic.setStatus(newStatus);
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
    public ArrayList<Subtask> getAllSubtasks() {

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
    public ArrayList<Subtask> getSubtasksByEpicId(int id) {
        ArrayList<Subtask> result = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getId() == id) {
                result.add(subtask);
            }
        }
        return result;
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(getCurrentTaskCount());
        subtasks.put(subtask.getId(), subtask);

        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);

        if (epic != null) {
            epic.addSubtask(subtask);
            updateEpic(epic);
        }
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
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);

            if (epic != null) {
                updateEpic(epic);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}