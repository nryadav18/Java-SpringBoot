package com.course.taskapi.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/*
 * Body of POST /api/v1/tasks.
 *
 * @Schema adds a description and an EXAMPLE value to the OpenAPI docs.
 * Swagger UI uses the examples to pre-fill the "Try it out" box - very handy.
 * Validation annotations (@NotBlank, @Size ...) are ALSO read by springdoc,
 * so the docs automatically say "required, max length 100".
 */
@Schema(description = "Data needed to create a new task")
public record TaskCreateRequest(

        @Schema(description = "Short title of the task", example = "Prepare sprint demo", maxLength = 100)
        @NotBlank @Size(max = 100)
        String title,

        @Schema(description = "Longer explanation (optional)", example = "Slides + live demo of the Task API")
        @Size(max = 1000)
        String description,

        @Schema(description = "Priority. Defaults to MEDIUM when missing", example = "HIGH")
        TaskPriority priority,

        @Schema(description = "Due date in ISO format yyyy-MM-dd (today or later)", example = "2030-01-31")
        @FutureOrPresent
        LocalDate dueDate) {
}
