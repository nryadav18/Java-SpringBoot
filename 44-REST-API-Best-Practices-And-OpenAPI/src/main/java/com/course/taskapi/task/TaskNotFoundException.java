package com.course.taskapi.task;

/*
 * Thrown by the service when an id does not exist.
 * GlobalExceptionHandler turns it into a 404 ProblemDetail.
 */
public class TaskNotFoundException extends RuntimeException {

    private final Long taskId;

    public TaskNotFoundException(Long taskId) {
        super("Task with id " + taskId + " was not found");
        this.taskId = taskId;
    }

    public Long getTaskId() {
        return taskId;
    }
}
