package ru.practicum.manager;

import ru.practicum.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        private Task task;
        private Node prev;
        private Node next;

        public Node(Task task) {
            this.task = task;
        }
    }

    private Node head;
    private Node tail;
    private Map<Integer, Node> history = new HashMap<>();

    @Override
    public void add(Task task) {
        Integer taskId = task.getId();

        if (history.containsKey(taskId)) {
            removeNode(history.get(taskId));
        }

        Node newNode = new Node(task);
        linkLast(newNode);
        history.put(taskId, newNode);
    }

    private void removeNode(Node node) {
        if (node == null) return;

        Node prev = node.prev;
        Node next = node.next;

        if (prev != null) {
            prev.next = next;
        }
        if (next != null) {
            next.prev = prev;
        }

        if (node == head) {
            head = next;
        }
        if (node == tail) {
            tail = prev;
        }

        node.prev = null;
        node.next = null;
        history.remove(node.task.getId());
    }

    private void linkLast(Node node) {
        node.prev = tail;
        node.next = null;

        if (tail == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        Node current = head;

        while (current != null) {
            result.add(current.task);
            current = current.next;
        }

        return result;
    }
}