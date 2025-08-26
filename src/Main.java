public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Купить продукты", "Хлеб яйца масло", manager, Status.NEW);
        Task task2 = new Task("Постирать вещи", "Разделить по цветам", manager, Status.NEW);

        manager.createTask(task1);
        manager.createTask(task2);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        Epic epic1 = new Epic("Учить английский", "Очень страшная задача", manager, Status.NEW);
        manager.createEpic(epic1);
        manager.getEpicById(epic1.getId());

        Subtask subtask1 = new Subtask("Найти репетитора", "Почитать отзывы", manager,
                epic1.getId(), Status.NEW);
        Subtask subtask2 = new Subtask("Разбираться в IDEA", "Переводить все встреченные слова", manager,
                epic1.getId(), Status.NEW);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        epic1.getSubtasks();
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());

        Epic epic2 = new Epic("Разобраться с ошибкой по коробке в бехе", "Не горит", manager, Status.NEW);
        manager.createEpic(epic2);
        manager.getEpicById(epic2.getId());

        Subtask subtask3 = new Subtask("Изучить вопрос на драйв2", "Сделать список вариантов", manager,
                epic2.getId(), Status.NEW);
        manager.createSubtask(subtask3);
        epic2.getSubtasks();
        manager.getSubtaskById(subtask3.getId());

        printAllTasks(manager);

        manager.getEpicById(epic1.getId());
        manager.getEpicById(epic2.getId());

        task1.setStatus(Status.IN_PROGRESS);
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask3.setStatus(Status.DONE);

        manager.updateTask(task1);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);
        manager.updateSubtask(subtask3);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask3.getId());
        manager.getEpicById(epic2.getId());
        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());

        System.out.println("\nПосле изменения статусов:");
        System.out.println("Задача 1: " + task1.getStatus());
        System.out.println("Подзадача 1.1: " + subtask1.getStatus());
        System.out.println("Подзадача 1.2: " + subtask2.getStatus());
        System.out.println("Подзадача 2.1: " + subtask3.getStatus());
        System.out.println("Эпик 1: " + epic1.getStatus());
        System.out.println("Эпик 2: " + epic2.getStatus());

        manager.deleteTask(task2.getId());
        manager.deleteEpic(epic2.getId());

        printAllTasks(manager);

    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("\n===== Задачи =====");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("\n===== Эпики =====");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("\n===== Подзадачи =====");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("\n===== История =====");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
//        System.out.println("Список задач:");
//        for (Task task : manager.getAllTasks()) {
//            System.out.println("ID: " + task.getId() + ", Название: " + task.getTitle() + ", Статус: " +
//                    task.getStatus());
//        }
//
//        System.out.println("\nСписок эпиков:");
//        for (Epic epic : manager.getAllEpics()) {
//            System.out.println("ID: " + epic.getId() + ", Название: " + epic.getTitle() +
//                    ", Статус: " + epic.getStatus() +
//                    ", Подзадач: " + epic.getSubtasks().size());
//        }
//
//        System.out.println("\nСписок подзадач:");
//        for (Subtask subtask : manager.getAllSubtasks()) {
//            System.out.println("ID: " + subtask.getId() + ", Название: " + subtask.getTitle() +
//                    ", Эпик: " + subtask.getEpicId() +
//                    ", Статус: " + subtask.getStatus());
//        }




