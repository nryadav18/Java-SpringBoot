package com.course.shopeasy.common.web;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * A small, stable JSON shape for paginated results (topic 41).
 *
 * Why not return Spring's Page directly? Its JSON (PageImpl) contains many internal fields and Spring
 * itself warns that the format is not guaranteed to stay the same. Our own record is a clear API contract:
 * <pre>
 * { "content": [...], "page": 0, "size": 10, "totalElements": 12, "totalPages": 2, "first": true, "last": false }
 * </pre>
 *
 * @param <T> the DTO type inside the page (ProductResponse, OrderResponse ...)
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last) {

    /** Converts a Spring Data Page (already mapped to DTOs) into our response record. */
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }
}
