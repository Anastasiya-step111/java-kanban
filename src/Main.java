public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1", manager, Status.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", manager, Status.NEW);

        manager.createTask(task1);
        manager.createTask(task2);


        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", manager, Status.NEW);
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", manager,
                epic1.getId(), Status.NEW);
        Subtask subtask2 = new Subtask("Подзадача 1.2", "Описание подзадачи 1.2", manager,
                epic1.getId(), Status.NEW);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        epic1.getSubtasks().add(subtask1);
        epic1.getSubtasks().add(subtask2);

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", manager, Status.NEW);
        manager.createEpic(epic2);

        Subtask subtask3 = new Subtask("Подзадача 2.1", "Описание подзадачи 2.1", manager,
                epic2.getId(), Status.NEW);
        manager.createSubtask(subtask3);
        epic2.getSubtasks().add(subtask3);

        System.out.println("Список задач:");
        for (Task task : manager.getAllTasks()) {
            System.out.println("ID: " + task.getId() + ", Название: " + task.getTitle() + ", Статус: " +
                    task.getStatus());
        }

        System.out.println("\nСписок эпиков:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println("ID: " + epic.getId() + ", Название: " + epic.getTitle() +
                    ", Статус: " + epic.getStatus() +
                    ", Подзадач: " + epic.getSubtasks().size());
        }

        System.out.println("\nСписок подзадач:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println("ID: " + subtask.getId() + ", Название: " + subtask.getTitle() +
                    ", Эпик: " + subtask.getEpicId() +
                    ", Статус: " + subtask.getStatus());
        }

        task1.setStatus(Status.IN_PROGRESS);
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask3.setStatus(Status.DONE);

        manager.updateTask(task1);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);
        manager.updateSubtask(subtask3);

        System.out.println("\nПосле изменения статусов:");
        System.out.println("Задача 1: " + task1.getStatus());
        System.out.println("Подзадача 1.1: " + subtask1.getStatus());
        System.out.println("Подзадача 1.2: " + subtask2.getStatus());
        System.out.println("Подзадача 2.1: " + subtask3.getStatus());
        System.out.println("Эпик 1: " + epic1.getStatus());
        System.out.println("Эпик 2: " + epic2.getStatus());

        manager.deleteTask(task2.getId());
        manager.deleteEpic(epic2.getId());

    }
}
