public class Subtask extends Task {
    private Epic epic;

    public Subtask(String title, String description, TaskManager taskManager) {
        super(title, description, taskManager);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }
}