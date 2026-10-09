package com.course.taskapi.task;

import com.course.taskapi.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * Version 2 of the list endpoint - URI VERSIONING.
 * Same data, different JSON shape (see TaskSummaryV2).
 * v1 keeps working, so old clients do not break.
 *
 * Notice: v2 reuses the SAME service. Versions are a concern of the web layer only.
 */
@RestController
@RequestMapping("/api/v2/tasks")
@Tag(name = "Tasks v2", description = "Newer, compact representation of tasks")
public class TaskV2Controller {

    private final TaskService service;

    public TaskV2Controller(TaskService service) {
        this.service = service;
    }

    @Operation(summary = "List tasks (compact v2 format)")
    @GetMapping
    public PageResponse<TaskSummaryV2> list(@RequestParam(required = false) String search,
                                            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return service.search(null, null, search, pageable).map(TaskSummaryV2::from);
    }
}
