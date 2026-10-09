package com.course.pagination.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/*
 * PageResponse<T> - OUR OWN, stable JSON shape for one page of results.
 * --------------------------------------------------------------------
 * Why not return Spring's Page<T> directly? Page is an internal Spring Data type. Its JSON
 * shape is not guaranteed to stay the same between versions (Spring even logs a warning if you
 * serialize PageImpl as-is). Your API clients deserve a contract YOU control.
 *
 * Example:
 * {
 *   "content": [ ...5 products... ],
 *   "page": 0, "size": 5, "totalElements": 60, "totalPages": 12,
 *   "first": true, "last": false, "sort": "price: DESC"
 * }
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        String sort
) {

    /** Converts a Spring Data Page (already mapped to DTOs) into our response record. */
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),          // current page index (0-based)
                page.getSize(),            // requested page size
                page.getTotalElements(),   // total matching rows (from the COUNT query)
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.getSort().toString());
    }
}
