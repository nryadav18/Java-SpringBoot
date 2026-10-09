package com.course.taskapi.task;

import com.course.taskapi.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/*
 * Version 1 of the Task API.
 *
 * REST rules followed here (see the lesson):
 *   - plural NOUN in the URL (/tasks), never verbs (/getTasks, /createTask)
 *   - the HTTP METHOD says what to do (GET read, POST create, PUT replace, PATCH change, DELETE remove)
 *   - correct status codes (200, 201 + Location, 204, 400, 404)
 *   - filtering / sorting / paging with query parameters
 *   - version in the URL: /api/v1/...
 *
 * The @Tag / @Operation / @ApiResponse annotations ONLY add documentation.
 * The API would work the same without them - but the docs would be poorer.
 */
@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "Create, read, update and delete tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @Operation(summary = "List tasks",
            description = "Returns one page of tasks. All filters are optional and can be combined. "
                    + "Sort with e.g. sort=dueDate,asc&sort=priority,desc")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "A page of tasks"),
            @ApiResponse(responseCode = "400", description = "Invalid filter or sort value",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping
    public PageResponse<TaskResponse> list(
            @Parameter(description = "Only tasks with this status") @RequestParam(required = false) TaskStatus status,
            @Parameter(description = "Only tasks with this priority") @RequestParam(required = false) TaskPriority priority,
            @Parameter(description = "Text that must appear in the title (case-insensitive)", example = "demo")
            @RequestParam(required = false) String search,
            // @ParameterObject shows page, size and sort as three separate parameters in Swagger UI
            @ParameterObject @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.search(status, priority, search, pageable);
    }

    @Operation(summary = "Get one task by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The task"),
            @ApiResponse(responseCode = "404", description = "No task with this id",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    public TaskResponse getById(@Parameter(description = "Task id", example = "1") @PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Create a task",
            description = "Returns 201 Created with a Location header. Send an optional Idempotency-Key "
                    + "header to make retries safe: the same key never creates a second task.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created"),
            @ApiResponse(responseCode = "200", description = "Same Idempotency-Key used again: the existing task is returned"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @Parameter(description = "Unique id of this request, e.g. a UUID", example = "7f9c1d2e-0b1a-4c55-9f00-1234567890ab")
            @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody TaskCreateRequest request) {

        TaskService.CreateResult result = service.create(request, idempotencyKey);

        // Location: http://localhost:8080/api/v1/tasks/13  -> where the new resource lives
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.task().id())
                .toUri();

        if (result.replayed()) {
            return ResponseEntity.ok().location(location).body(result.task());
        }
        return ResponseEntity.created(location).body(result.task());
    }

    @Operation(summary = "Replace a task (full update)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task replaced"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
            @ApiResponse(responseCode = "404", description = "No task with this id", content = @Content)
    })
    @PutMapping("/{id}")
    public TaskResponse replace(@PathVariable Long id, @Valid @RequestBody TaskUpdateRequest request) {
        return service.replace(id, request);
    }

    @Operation(summary = "Partially update a task", description = "Only the fields you send are changed.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated"),
            @ApiResponse(responseCode = "404", description = "No task with this id", content = @Content)
    })
    @PatchMapping("/{id}")
    public TaskResponse patch(@PathVariable Long id, @Valid @RequestBody TaskPatchRequest request) {
        return service.patch(id, request);
    }

    @Operation(summary = "Delete a task")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted (no body)"),
            @ApiResponse(responseCode = "404", description = "No task with this id", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
