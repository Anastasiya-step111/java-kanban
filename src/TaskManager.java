import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

        int getCurrentTaskCount();

        List<Task> getAllTasks();
        Task getTaskById(int id);
        Task createTask(Task task);
        void updateTask(Task task);
        void deleteTask(int id);
        void deleteAllTasks();

        List<Epic> getAllEpics();
        Epic getEpicById(int id);
        Epic createEpic(Epic epic);
        void updateEpic(Epic epic);
        void deleteEpic(int id);
        void deleteAllEpics();

        List<Subtask> getAllSubtasks();
        Subtask getSubtaskById(int id);
        List<Subtask> getSubtasksByEpicId(int id);
        Subtask createSubtask(Subtask subtask);
        void deleteAllSubtasks();
        void removeSubtaskById(int id);
        void updateSubtask(Subtask subtask, int id);
        List<Task> getHistory();

        HistoryManager getHistoryManager();
}
