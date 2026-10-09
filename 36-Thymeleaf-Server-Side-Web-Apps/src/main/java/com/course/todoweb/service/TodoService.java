package com.course.todoweb.service;

import com.course.todoweb.exception.TodoNotFoundException;
import com.course.todoweb.model.Priority;
import com.course.todoweb.model.Todo;
import com.course.todoweb.web.TodoForm;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/*
 * TodoService - in-memory storage + business logic (layers from topic 33).
 * Exactly the same kind of service you would use behind a REST API.
 * Only the CONTROLLER is different: it returns HTML views instead of JSON.
 */
@Service
public class TodoService {

    private final Map<Long, Todo> todos = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong();

    public TodoService() {
        // A few sample todos so the page is not empty on first start
        save(new Todo(null, "Learn Thymeleaf basics", "th:text, th:each, th:if", Priority.HIGH,
                LocalDate.now().plusDays(1)));
        save(new Todo(null, "Buy groceries", "Milk, rice, vegetables", Priority.MEDIUM,
                LocalDate.now().plusDays(3)));
        Todo done = save(new Todo(null, "Install JDK 17", null, Priority.LOW, null));
        done.setDone(true);
    }

    /**
     * filter = "all", "active" or "done".
     * Sorted: open todos first, then by priority (HIGH first), then by id.
     */
    public List<Todo> findAll(String filter) {
        return todos.values().stream()
                .filter(t -> switch (filter) {
                    case "active" -> !t.isDone();
                    case "done" -> t.isDone();
                    default -> true;
                })
                .sorted(Comparator.comparing(Todo::isDone)
                        .thenComparing(Todo::getPriority, Comparator.reverseOrder())
                        .thenComparing(Todo::getId))
                .toList();
    }

    public Todo findById(Long id) {
        Todo todo = todos.get(id);
        if (todo == null) {
            throw new TodoNotFoundException(id);
        }
        return todo;
    }

    public boolean titleExists(String title, Long ignoreId) {
        return todos.values().stream()
                .anyMatch(t -> t.getTitle().equalsIgnoreCase(title.trim()) && !t.getId().equals(ignoreId));
    }

    public Todo create(TodoForm form) {
        Todo todo = new Todo(null, form.getTitle().trim(), form.getDescription(), form.getPriority(),
                form.getDueDate());
        return save(todo);
    }

    public Todo update(Long id, TodoForm form) {
        Todo todo = findById(id);
        todo.setTitle(form.getTitle().trim());
        todo.setDescription(form.getDescription());
        todo.setPriority(form.getPriority());
        todo.setDueDate(form.getDueDate());
        todo.setDone(form.isDone());
        return todo;
    }

    public Todo toggle(Long id) {
        Todo todo = findById(id);
        todo.setDone(!todo.isDone());
        return todo;
    }

    public void delete(Long id) {
        findById(id);          // 404 if it does not exist
        todos.remove(id);
    }

    public long countOpen() {
        return todos.values().stream().filter(t -> !t.isDone()).count();
    }

    /** Copies a Todo into a form object, used to pre-fill the edit form. */
    public TodoForm toForm(Todo todo) {
        TodoForm form = new TodoForm();
        form.setId(todo.getId());
        form.setTitle(todo.getTitle());
        form.setDescription(todo.getDescription());
        form.setPriority(todo.getPriority());
        form.setDueDate(todo.getDueDate());
        form.setDone(todo.isDone());
        return form;
    }

    private Todo save(Todo todo) {
        if (todo.getId() == null) {
            todo.setId(ids.incrementAndGet());
        }
        todos.put(todo.getId(), todo);
        return todo;
    }
}
