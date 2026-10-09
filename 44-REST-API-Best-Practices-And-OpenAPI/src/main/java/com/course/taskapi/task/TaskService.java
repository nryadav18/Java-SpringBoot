package com.course.taskapi.task;

import com.course.taskapi.common.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Business logic for tasks. The controller stays thin; all rules live here.
 */
@Service
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository repository;

    /*
     * Idempotency-Key support for POST (see the lesson, section "Idempotency").
     * Key = the client's unique request id, value = the id of the task it created.
     * A real system would store this in the database or Redis with an expiry time;
     * a ConcurrentHashMap is enough to SHOW the idea.
     */
    private final Map<String, Long> createdByIdempotencyKey = new ConcurrentHashMap<>();

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    /** Result of a create: the task, and whether it was just created or "replayed". */
    public record CreateResult(TaskResponse task, boolean replayed) {
    }

    /*
     * WHITELIST of fields a client may sort by. Never pass a client's sort field
     * straight to the database: unknown names would cause a 500 error, and sorting
     * by an un-indexed column on a big table can be very slow.
     */
    private static final Set<String> SORTABLE_FIELDS =
            new TreeSet<>(Set.of("id", "title", "status", "priority", "dueDate", "createdAt"));

    public PageResponse<TaskResponse> search(TaskStatus status, TaskPriority priority,
                                             String search, Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            if (!SORTABLE_FIELDS.contains(order.getProperty())) {
                throw new InvalidSortPropertyException(order.getProperty(), SORTABLE_FIELDS);
            }
        }
        String cleanSearch = (search == null || search.isBlank()) ? null : search.trim();
        return PageResponse.of(
                repository.search(status, priority, cleanSearch, pageable).map(TaskResponse::from));
    }

    public TaskResponse findById(Long id) {
        return TaskResponse.from(getOrThrow(id));
    }

    @Transactional
    public CreateResult create(TaskCreateRequest request, String idempotencyKey) {
        // Same key seen before? Return the SAME task again instead of creating a duplicate.
        if (idempotencyKey != null) {
            Long existingId = createdByIdempotencyKey.get(idempotencyKey);
            if (existingId != null) {
                return new CreateResult(findById(existingId), true);
            }
        }
        Task saved = repository.save(new Task(request.title(), request.description(),
                request.priority(), request.dueDate()));
        if (idempotencyKey != null) {
            createdByIdempotencyKey.put(idempotencyKey, saved.getId());
        }
        return new CreateResult(TaskResponse.from(saved), false);
    }

    /** PUT = replace every field. Calling it twice gives the same result (idempotent). */
    @Transactional
    public TaskResponse replace(Long id, TaskUpdateRequest request) {
        Task task = getOrThrow(id);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());
        // The entity is "managed" inside the transaction, so Hibernate would write the
        // changes at commit anyway (dirty checking, topics 38/42). We call saveAndFlush
        // only to run the UPDATE now, so @UpdateTimestamp is filled in the response.
        return TaskResponse.from(repository.saveAndFlush(task));
    }

    /** PATCH = change only the fields that were sent (not null). */
    @Transactional
    public TaskResponse patch(Long id, TaskPatchRequest request) {
        Task task = getOrThrow(id);
        if (request.title() != null) task.setTitle(request.title());
        if (request.description() != null) task.setDescription(request.description());
        if (request.status() != null) task.setStatus(request.status());
        if (request.priority() != null) task.setPriority(request.priority());
        if (request.dueDate() != null) task.setDueDate(request.dueDate());
        return TaskResponse.from(repository.saveAndFlush(task));
    }

    @Transactional
    public void delete(Long id) {
        Task task = getOrThrow(id);
        repository.delete(task);
    }

    private Task getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }
}
