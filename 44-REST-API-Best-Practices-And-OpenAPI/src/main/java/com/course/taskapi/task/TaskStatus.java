package com.course.taskapi.task;

/*
 * The life of a task. Stored as text in the database (@Enumerated(STRING)).
 */
public enum TaskStatus {
    TODO,
    IN_PROGRESS,
    DONE
}
