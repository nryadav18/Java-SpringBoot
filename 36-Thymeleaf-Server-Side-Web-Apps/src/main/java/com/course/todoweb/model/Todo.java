package com.course.todoweb.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * Todo - the domain object stored by TodoService.
 *
 * Thymeleaf reads data through GETTERS: ${todo.title} calls getTitle(),
 * ${todo.done} calls isDone(). That is why this is a normal class with getters.
 */
public class Todo {

    private Long id;
    private String title;
    private String description;
    private Priority priority;
    private LocalDate dueDate;
    private boolean done;
    private final LocalDateTime createdAt = LocalDateTime.now();

    public Todo(Long id, String title, String description, Priority priority, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
    }

    /** Overdue = has a due date in the past and is not done yet. Used for a red badge. */
    public boolean isOverdue() {
        return !done && dueDate != null && dueDate.isBefore(LocalDate.now());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
