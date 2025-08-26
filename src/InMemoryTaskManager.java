import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
    public Task createTask(Task task) {
        // Ищем существующую задачу
        for (Task existingTask : tasks.values()) {
            if ((Objects.equals(existingTask.getDescription(), task.getDescription()) &&
                    Objects.equals(existingTask.getTitle(), task.getTitle()))) {
                // Возвращаем существующую задачу вместо создания новой
                return existingTask;
            }
        }

        // Если задача не найдена, создаем новую
        int id = getCurrentTaskCount();
        task.setId(id);
        tasks.put(id, task);
        return task; // Возвращаем созданную задачу
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
            System.out.println("Получен эпик с ID: " + id + ", статус: " + epic.getStatus());
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        // Ищем существующий Epic
        for (Epic existingEpic : epics.values()) {
            if ( Objects.equals(existingEpic.getDescription(), epic.getDescription()) &&
                    Objects.equals(existingEpic.getTitle(), epic.getTitle())) {
                // Возвращаем существующий Epic
                return existingEpic;
            }
        }

        // Если не нашли, создаем новый
        int id = getCurrentTaskCount();
        epic.setId(id);

        // Проверяем исходный статус
        System.out.println("Перед сохранением статус: " + epic.getStatus());

        epics.put(id, epic);

        // Проверяем статус после сохранения
        System.out.println("После сохранения статус: " + epic.getStatus());

        return epic; // Возвращаем созданный Epic
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

        int subtaskCount = subtasks.size();
        int subtaskCountNew = 0;
        int subtaskCountInProgress = 0;
        int subtaskCountDone = 0;

        for (Subtask subtask : subtasks) {
            Status subtaskStatus = subtask.getStatus();

            if (subtaskStatus == Status.NEW) {
                subtaskCountNew++;
            } else if (subtaskStatus == Status.IN_PROGRESS) {
                subtaskCountInProgress++;
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
    public Subtask createSubtask(Subtask subtask) {
        // Ищем существующий Subtask
        for (Subtask existingSubtask : subtasks.values()) {
            if (Objects.equals(existingSubtask.getTitle(), subtask.getTitle()) &&
                    Objects.equals(existingSubtask.getDescription(), subtask.getDescription()) &&
                    existingSubtask.getEpicId() == subtask.getEpicId()) {
                return existingSubtask;
            }
        }

        // Если не нашли, создаем новый
        int id = getCurrentTaskCount();
        subtask.setId(id);
        subtasks.put(id, subtask);

        // Обновляем связанный Epic
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);

        if (epic != null) {
            epic.addSubtask(subtask);
            updateEpic(epic);
        }

        return subtask; // Возвращаем созданный Subtask
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