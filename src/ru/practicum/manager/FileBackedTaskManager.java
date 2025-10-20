package ru.practicum.manager;

import ru.practicum.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
        loadFromFile(file);
    }

    @Override
    public Task createTask(Task task) {
        Task result = super.createTask(task);
        save();
        return result;
    }

    @Override
    public Task getTaskById(int id) {
        Task result = super.getTaskById(id);
        save();
        return result;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask result = super.createSubtask(subtask);
        save();
        return result;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask result = super.getSubtaskById(id);
        save();
        return result;
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask, int updateSubtaskId) {
        super.updateSubtask(updatedSubtask, updateSubtaskId);
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic result = super.createEpic(epic);
        save();
        return result;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic result = super.getEpicById(id);
        save();
        return result;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write("id,type,title,status,description,startTime,duration,epic\n");

            getAllTasks().stream()
                    .map(Task::toCSVStr)
                    .forEach(line -> {
                        try {
                            writer.write(line + "\n");
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });

            getAllSubtasks().stream()
                    .map(Subtask::toCSVStr)
                    .forEach(line -> {
                        try {
                            writer.write(line + "\n");
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });

            getAllEpics().stream()
                    .map(Epic::toCSVStr)
                    .forEach(line -> {
                        try {
                            writer.write(line + "\n");
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });

            List<Task> history = getHistory();
            if (!history.isEmpty()) {
                writer.write("\nHISTORY:\n");
                history.stream()
                        .map(Task::getId)
                        .map(String::valueOf)
                        .forEach(line -> {
                            try {
                                writer.write(line + "\n");
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        });
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить данные в файл", e);
        }
    }

    public void loadFromFile(File file) {
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            boolean isHeaderRead = false;
            List<Integer> historyIds = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (!isHeaderRead) {
                    isHeaderRead = true;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                System.out.println("Текущая строка: " + line);

                if (line.contains("HISTORY:")) {
                    while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                        try {
                            int id = Integer.parseInt(line.trim());
                            //historyIds.add(String.valueOf(id));
                        } catch (NumberFormatException e) {
                            System.err.println("Некорректный ID в истории: " + line);
                        }
                    }
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 5) {
                    System.err.println("Строка не соответствует формату: " + line);
                    continue;
                }

                switch (TaskType.valueOf(parts[1])) {
                    case TASK:
                        addTaskFromCSV(parts);
                        break;
                    case SUBTASK:
                        addSubtaskFromCSV(parts);
                        break;
                    case EPIC:
                        addEpicFromCSV(parts);
                        break;
                    default:
                        System.err.println("Неизвестный тип задачи: " + parts[1]);
                }
            }

            restoreHistory(historyIds);
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось прочитать данные из файла", e);
        }
    }

    private void addTaskFromCSV(String[] parts) {
        int id = Integer.parseInt(parts[0]);
        String title = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        LocalDateTime startTime = parts[5].isEmpty() ? null : LocalDateTime.parse(parts[5]);
        Duration duration = parts[6].isEmpty() ? null : Duration.ofMinutes(Long.parseLong(parts[6]));

        Task task = new Task(title, description, this, status, startTime, duration);
        task.setId(id);
        createTask(task);
    }

    private void addSubtaskFromCSV(String[] parts) {
        int id = Integer.parseInt(parts[0]);
        String title = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        LocalDateTime startTime = parts[5].isEmpty() ? null : LocalDateTime.parse(parts[5]);
        Duration duration = parts[6].isEmpty() ? null : Duration.ofMinutes(Long.parseLong(parts[6]));
        int epicId = Integer.parseInt(parts[7]);

        Subtask subtask = new Subtask(title, description, this, epicId, status, startTime, duration);
        subtask.setId(id);
        createSubtask(subtask);
    }

    private void addEpicFromCSV(String[] parts) {
        int id = Integer.parseInt(parts[0]);
        String title = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        LocalDateTime startTime = parts[5].isEmpty() ? null : LocalDateTime.parse(parts[5]);
        Duration duration = parts[6].isEmpty() ? null : Duration.ofMinutes(Long.parseLong(parts[6]));

        Epic epic = new Epic(title, description, this, status);
        epic.setId(id);
        epic.setStartTime(startTime);
        epic.setDuration(duration);
        createEpic(epic);
    }

    private void restoreHistory(List<Integer> historyIds) {
        for (int id : historyIds) {
            Task task = tasks.get(id);
            if (task != null) {
                historyManager.add(task);
                continue;
            }
            Epic epic = epics.get(id);
            if (epic != null) {
                historyManager.add(epic);
                continue;
            }
            Subtask subtask = subtasks.get(id);
            if (subtask != null) {
                historyManager.add(subtask);
            }
        }
    }
}
