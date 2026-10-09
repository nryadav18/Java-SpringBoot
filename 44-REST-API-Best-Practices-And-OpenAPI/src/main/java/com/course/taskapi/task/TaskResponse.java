package com.course.taskapi.task;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * What the v1 API returns for one task.
 */
@Schema(description = "A task as returned by the API (v1)")
public record TaskResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Prepare sprint demo") String title,
        @Schema(example = "Slides + live demo") String description,
        @Schema(example = "TODO") TaskStatus status,
        @Schema(example = "HIGH") TaskPriority priority,
        @Schema(example = "2030-01-31") LocalDate dueDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static TaskResponse from(Task task) {
        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(),
                task.getPriority(), task.getDueDate(), task.getCreatedAt(), task.getUpdatedAt());
    }
}
