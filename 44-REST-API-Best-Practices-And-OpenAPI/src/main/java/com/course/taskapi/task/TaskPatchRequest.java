package com.course.taskapi.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/*
 * Body of PATCH /api/v1/tasks/{id}  = PARTIAL update.
 * Every field is optional. A null field means "do not change it".
 * Example: {"status":"DONE"} only changes the status.
 */
@Schema(description = "Partial update of a task (PATCH). Send only the fields you want to change.")
public record TaskPatchRequest(

        @Schema(example = "Prepare sprint demo v2")
        @Size(min = 1, max = 100)
        String title,

        @Size(max = 1000)
        String description,

        @Schema(example = "DONE")
        TaskStatus status,

        TaskPriority priority,

        LocalDate dueDate) {
}
