public class Task {
    private String title;
    private String description;
    private int id;
    private Status status;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.id = TaskManager.taskCount++;
        this.status = Status.NEW;
    }
    // Геттеры и сеттеры
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}