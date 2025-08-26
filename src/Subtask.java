public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, TaskManager taskManager, int epicId, Status status) {
        super(title, description, taskManager, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return String.format("Подзадача №%d: %s\n" +
                        "Статус: %s\n" +
                        "Описание: %s\n" +
                        "Принадлежит эпику №%d",
                getId(), getTitle(), getStatus(), getDescription(), getEpicId());
    }
}
