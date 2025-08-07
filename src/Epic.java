import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    public Epic(String title, String description, TaskManager taskManager) {
        super(title, description, taskManager);
        this.subtasks = new ArrayList<>();
    }

    // Методы для работы с подзадачами
    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        subtask.setEpic(this);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
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

 /* состоит из подзадач Subtask
  Для каждой подзадачи известно, в рамках какого эпика она выполняется.
Каждый эпик знает, какие подзадачи в него входят.
Завершение всех подзадач эпика считается завершением эпика.*/

/*«для подклассов Subtask и Epic наследуем сразу имплементацию» означает,
что эти подклассы наследуют уже готовую реализацию методов от класса Task,
и им не нужно реализовывать эти методы заново. Это упрощает создание
новых классов на основе Task, так как они автоматически получают
доступ к функциональности базового класса. */