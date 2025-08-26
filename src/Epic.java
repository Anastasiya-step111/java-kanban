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

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    @Override
    public String toString() {
        return String.format("Эпик №%d: %s\n" +
                        "Статус: %s\n" +
                        "Описание: %s\n" +
                        "Количество подзадач: %d",
                getId(), getTitle(), getStatus(), getDescription(), getSubtasks().size());
    }
}
