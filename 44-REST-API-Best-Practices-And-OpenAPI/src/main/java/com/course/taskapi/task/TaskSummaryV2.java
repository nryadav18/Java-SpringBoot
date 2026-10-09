package com.course.taskapi.task;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/*
 * The v2 shape of a task. Imagine the mobile team asked for:
 *   - "name" instead of "title"
 *   - a simple boolean "done" instead of the status enum
 * Renaming a field is a BREAKING change for old clients, so we do NOT change v1.
 * We publish a new version (v2) and keep v1 running until clients have moved.
 */
@Schema(description = "Compact task representation (v2)")
public record TaskSummaryV2(
        @Schema(example = "1") Long id,
        @Schema(example = "Prepare sprint demo") String name,
        @Schema(example = "false") boolean done,
        @Schema(example = "HIGH") TaskPriority priority,
        @Schema(example = "2030-01-31") LocalDate dueDate) {

    public static TaskSummaryV2 from(TaskResponse t) {
        return new TaskSummaryV2(t.id(), t.title(), t.status() == TaskStatus.DONE, t.priority(), t.dueDate());
    }
}
