package com.course.pagination.dto;

import org.springframework.data.domain.Slice;

import java.util.List;

/*
 * SliceResponse<T> - for "Load more" / infinite scrolling screens.
 * No totalElements / totalPages: a Slice does not run the (possibly expensive) COUNT query.
 * It only knows whether there is a next slice.
 */
public record SliceResponse<T>(List<T> content, int page, int size, boolean hasNext) {

    public static <T> SliceResponse<T> from(Slice<T> slice) {
        return new SliceResponse<>(slice.getContent(), slice.getNumber(), slice.getSize(), slice.hasNext());
    }
}
