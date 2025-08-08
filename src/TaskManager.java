import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int taskCount;

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();


    public static int addTaskAndGetId() {
        taskCount++;
        return taskCount;
    }


    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void createTask(Task task) {
        if (task.getId() == 0) {
            task.setId(taskCount++);
        }
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void createEpic(Epic epic) {
        if (epic.getId() == 0) {
            epic.setId(taskCount++);
        }
        epics.put(epic.getId(), epic);
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            // updateEpicStatus(epic);
            epic.getStatus();
        }
    }

    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
        }
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public ArrayList<Subtask> getSubtasksByEpicId(int id) {
        ArrayList<Subtask> result = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getId() == id) {
                result.add(subtask);
            }
        }
        return result;
    }

    public void createSubtask(Subtask subtask) {
        if (subtask.getId() == 0) {
            subtask.setId(taskCount++);
        }
        subtasks.put(subtask.getId(), subtask);
    }

    public void deleteAllSubtask() {
        subtasks.clear();
    }

    public void removeSubtaskById(int id) {
        subtasks.remove(id);
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = getEpicById(subtask.getEpicId());
            epic.getStatus();
        }
    }
}