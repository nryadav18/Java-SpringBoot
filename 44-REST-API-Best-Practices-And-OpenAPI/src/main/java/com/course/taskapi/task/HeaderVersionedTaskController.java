package com.course.taskapi.task;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/*
 * HEADER VERSIONING demo.
 * The URL stays the same (/api/tasks/{id}); the client picks the version
 * with a request header:   X-API-Version: 2
 *
 *   curl http://localhost:8080/api/tasks/1                         -> v1 shape (default)
 *   curl -H "X-API-Version: 1" http://localhost:8080/api/tasks/1   -> v1 shape
 *   curl -H "X-API-Version: 2" http://localhost:8080/api/tasks/1   -> v2 shape
 *
 * "headers = ..." is a mapping CONDITION: Spring only calls the method when the
 * header has that value. When several methods match, the more specific one wins.
 */
@RestController
@Tag(name = "Versioning demo", description = "Same URL, version chosen by the X-API-Version header")
public class HeaderVersionedTaskController {

    private final TaskService service;

    public HeaderVersionedTaskController(TaskService service) {
        this.service = service;
    }

    @Operation(summary = "Get a task - default (v1 shape) when no X-API-Version header is sent")
    @GetMapping("/api/tasks/{id}")
    public TaskResponse getDefault(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Get a task - X-API-Version: 1")
    @GetMapping(value = "/api/tasks/{id}", headers = "X-API-Version=1")
    public TaskResponse getV1(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Get a task - X-API-Version: 2")
    @GetMapping(value = "/api/tasks/{id}", headers = "X-API-Version=2")
    public TaskSummaryV2 getV2(@PathVariable Long id) {
        return TaskSummaryV2.from(service.findById(id));
    }
}
