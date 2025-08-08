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

}
