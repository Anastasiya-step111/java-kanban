import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private static int taskCount = 1;

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    // Поле для хранения истории просмотров
    private List<Task> history = new ArrayList<>();
    private static final int MAX_HISTORY_SIZE = 10;

    public int getCurrentTaskCount() {
        taskCount++;
        return taskCount;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        updateHistory(task);
        return task;
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
        Epic epic = epics.get(id);
        updateHistory(epic);
        return epic;
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
        ArrayList<Subtask> subtasks = epic.getSubtasks();

        // Если подзадачи отсутствуют, возвращаем NEW
        if (subtasks.isEmpty()) {
            return Status.NEW;
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
        Subtask subtask = subtasks.get(id);
        updateHistory(subtask);
        return subtask;
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
                // Сохраняем текущий статус эпика
                Status oldStatus = epic.getStatus();

                // Удаляем подзадачу из списка эпика
                epic.removeSubtask(subtask);

                // Обновляем статус эпика
                Status newStatus = updateStatusEpic(epic);

                // Сохраняем новый статус
                epic.setStatus(newStatus);

                // Сохраняем обновленный эпик только если статус изменился
                if (!oldStatus.equals(newStatus)) {
                    epics.put(epic.getId(), epic);
                }
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

    public List<Task> getHistory(){
        return new ArrayList<>(history);
    }

    // Вспомогательный метод для обновления истории
    private void updateHistory(Task task) {
        if (task == null) {
            return;
        }

        // Добавляем задачу в конец списка
        history.add(task);

        // Если превышен лимит, удаляем самый старый элемент
        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
    }
}