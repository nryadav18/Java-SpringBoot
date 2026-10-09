package com.course.taskapi.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/*
 * Body of PUT /api/v1/tasks/{id}  = FULL replacement.
 * With PUT the client sends the WHOLE resource, so the important fields are required.
 * (Compare with TaskPatchRequest, where everything is optional.)
 */
@Schema(description = "Full replacement of a task (PUT)")
public record TaskUpdateRequest(

        @Schema(example = "Prepare sprint demo")
        @NotBlank @Size(max = 100)
        String title,

        @Schema(example = "Slides + live demo")
        @Size(max = 1000)
        String description,

        @Schema(example = "IN_PROGRESS")
        @NotNull
        TaskStatus status,

        @Schema(example = "HIGH")
        @NotNull
        TaskPriority priority,

        @Schema(example = "2030-01-31")
        LocalDate dueDate) {
}
