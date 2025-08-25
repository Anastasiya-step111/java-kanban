import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

        int getCurrentTaskCount();

        ArrayList<Task> getAllTasks();
        Task getTaskById(int id);
        void createTask(Task task);
        void updateTask(Task task);
        void deleteTask(int id);
        void deleteAllTasks();

        ArrayList<Epic> getAllEpics();
        Epic getEpicById(int id);
        void createEpic(Epic epic);
        void updateEpic(Epic epic);
        void deleteEpic(int id);
        void deleteAllEpics();

        ArrayList<Subtask> getAllSubtasks();
        Subtask getSubtaskById(int id);
        ArrayList<Subtask> getSubtasksByEpicId(int id);
        void createSubtask(Subtask subtask);
        void deleteAllSubtasks();
        void removeSubtaskById(int id);
        void updateSubtask(Subtask subtask);
        List<Task> getHistory();
}
