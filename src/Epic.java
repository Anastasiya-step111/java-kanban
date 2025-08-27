import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    public Epic(String title, String description, TaskManager taskManager, Status status) {
        super(title, description, taskManager, status);
        this.subtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    // public void addSubtask(Subtask subtask) {
    //    subtasks.add(subtask); }

    public void addSubtask(Object subtask) {
        // Проверяем, что добавляемый объект не является самим эпиком
        if (subtask == this) {
            throw new IllegalArgumentException("Нельзя добавить эпик как подзадачу самому себе");
        }

        // Проверяем, что добавляемый объект является подзадачей
        if (!(subtask instanceof Subtask)) {
            throw new IllegalArgumentException("Можно добавлять только подзадачи");
        }

        Subtask task = (Subtask) subtask;

        // Добавляем только если подзадачи еще нет в списке
        if (!subtasks.contains(task)) {
            subtasks.add(task);
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Epic epic = (Epic) o;

        return super.equals(o) &&
                Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }
}
