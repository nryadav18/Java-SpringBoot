package com.course.taskapi.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/*
 * One query for "search with OPTIONAL filters":
 * every filter that is null is simply ignored thanks to "(:x IS NULL OR ...)".
 * Pageable adds LIMIT/OFFSET and ORDER BY for us (topic 41).
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
            SELECT t FROM Task t
            WHERE (:status IS NULL OR t.status = :status)
              AND (:priority IS NULL OR t.priority = :priority)
              AND (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Task> search(@Param("status") TaskStatus status,
                      @Param("priority") TaskPriority priority,
                      @Param("search") String search,
                      Pageable pageable);
}
