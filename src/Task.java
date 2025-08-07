public class Task {
    private String title;
    private String description;
    private int id;
    private Status status;

    private final TaskManager taskManager;

    public Task(String title, String description, TaskManager taskManager) {
        this.title = title;
        this.description = description;
        this.taskManager = taskManager;
        this.id = taskManager.addTaskAndGetId();
        this.status = Status.NEW;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}