public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, InMemoryTaskManager taskManager, int epicId, Status status) {
        super(title, description, taskManager, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
