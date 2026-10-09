package com.course.taskapi.common;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/*
 * ONE consistent shape for every list endpoint in the API:
 *
 * {
 *   "content": [ ... ],
 *   "page": 0, "size": 10,
 *   "totalElements": 12, "totalPages": 2,
 *   "first": true, "last": false
 * }
 *
 * Why not return Spring's Page<T> directly? Its JSON contains many internal
 * fields (pageable, sort, numberOfElements ...) and Spring itself warns that
 * the format is not stable. Our own record is small, clear and stable.
 */
@Schema(description = "A page of results")
public record PageResponse<T>(
        List<T> content,
        @Schema(example = "0") int page,
        @Schema(example = "10") int size,
        @Schema(example = "12") long totalElements,
        @Schema(example = "2") int totalPages,
        boolean first,
        boolean last) {

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }

    // Convert the content (e.g. TaskResponse -> TaskSummaryV2) but keep the paging numbers.
    public <R> PageResponse<R> map(Function<T, R> mapper) {
        return new PageResponse<>(content.stream().map(mapper).toList(), page, size,
                totalElements, totalPages, first, last);
    }
}
