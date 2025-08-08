import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    public Epic(String title, String description, TaskManager taskManager, Status status) {
        super(title, description, taskManager, status);
        this.subtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public Status getStatus() {
        // Проверяем статус эпика по статусам подзадач
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != Status.DONE) {
                return Status.IN_PROGRESS;
            }
        }
        return Status.DONE;
    }
}
