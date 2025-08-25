import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int taskCount = 1;

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();


    public static int getCurrentTaskCount() {
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
        task.setId(taskCount++);
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
        epic.setId(taskCount++);
        epics.put(epic.getId(), epic);
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Status status = updateStatusEpic(epic);
            epic.setStatus(status);
            epics.put(epic.getId(), epic);
        }
    }
    private Status updateStatusEpic(Epic epic) {
        boolean allNew = true;
        boolean hasInProgress = false;
        boolean allDone = true;
        ArrayList<Subtask> subtasks = epic.getSubtasks();
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
        if(hasInProgress) {
            return Status.IN_PROGRESS;
        }
        if(allDone) {
            return Status.DONE;
        }
        if (allNew) {
            return Status.NEW;
        }
        return Status.NEW;
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
        subtask.setId(taskCount++);
        subtasks.put(subtask.getId(), subtask);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        epic.addSubtask(subtask);
        // Обновляем статус эпика
        Status newStatus = updateStatusEpic(epic);
        epic.setStatus(newStatus);
        // Сохраняем обновленный эпик обратно в мапу
        epics.put(epic.getId(), epic);
    }

    public void deleteAllSubtasks() {
        // Создаем копию списка ID подзадач, чтобы избежать модификации во время итерации
        ArrayList<Integer> subtaskIds = new ArrayList<>(subtasks.keySet());

        // Проходим по всем подзадачам
        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = subtasks.remove(subtaskId);

            if (subtask != null) {
                int epicId = subtask.getEpicId();
                Epic epic = epics.get(epicId);

                if (epic != null) {
                    // Удаляем подзадачу из списка эпика
                    epic.removeSubtask(subtask);

                    // Обновляем статус эпика
                    Status newStatus = updateStatusEpic(epic);
                    epic.setStatus(newStatus);

                    // Сохраняем обновленный эпик
                    epics.put(epic.getId(), epic);
                }
            }
        }

        // Очищаем коллекцию подзадач
        subtasks.clear();
    }

    public void removeSubtaskById(int id) {
        // Получаем подзадачу по ID
        Subtask subtask = subtasks.remove(id);

        if (subtask != null) {
            // Получаем ID эпика, к которому принадлежала подзадача
            int epicId = subtask.getEpicId();

            // Находим соответствующий эпик
            Epic epic = epics.get(epicId);

            if (epic != null) {
                // Удаляем подзадачу из списка эпика
                epic.removeSubtask(subtask);

                // Обновляем статус эпика
                Status newStatus = updateStatusEpic(epic);
                epic.setStatus(newStatus);

                // Сохраняем обновленный эпик
                epics.put(epic.getId(), epic);
            }
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = getEpicById(subtask.getEpicId());
            updateEpic(epic);
        }
    }
}