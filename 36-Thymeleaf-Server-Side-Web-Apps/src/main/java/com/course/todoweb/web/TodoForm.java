package com.course.todoweb.web;

import com.course.todoweb.model.Priority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/*
 * TodoForm - the "form backing object" (a DTO for an HTML form).
 * ---------------------------------------------------------------
 * Thymeleaf's th:field="*{title}" needs to READ the value (getTitle) to fill
 * the input, and Spring needs to WRITE submitted values (setTitle).
 * So form objects are usually classic classes with a no-arg constructor,
 * getters and setters (not records).
 *
 * The same validation annotations as topic 34 work here. Errors are shown
 * next to each field in the form instead of being returned as JSON.
 */
public class TodoForm {

    private Long id;   // null when creating, set when editing

    @NotBlank(message = "Please enter a title")
    @Size(max = 60, message = "Title must be at most {max} characters")
    private String title;

    @Size(max = 300, message = "Description must be at most {max} characters")
    private String description;

    @NotNull(message = "Please choose a priority")
    private Priority priority = Priority.MEDIUM;   // default selection in the dropdown

    // <input type="date"> sends "2026-12-31" (ISO format). @DateTimeFormat tells
    // Spring how to convert that text to a LocalDate - and back again for display.
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @FutureOrPresent(message = "Due date cannot be in the past")
    private LocalDate dueDate;

    private boolean done;

    public TodoForm() {
        // required: Spring creates an empty form object, then calls the setters
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
}
