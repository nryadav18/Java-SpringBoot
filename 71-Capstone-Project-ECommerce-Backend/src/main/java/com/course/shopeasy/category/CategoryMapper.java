package com.course.shopeasy.category;

/**
 * Converts between the Category entity and its DTOs (topic 33: manual mapping).
 *
 * Hand-written mappers are plain, debuggable Java. In bigger projects a library such as MapStruct
 * generates this code at compile time - same idea, less typing.
 */
public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }

    public static Category toEntity(CategoryRequest request) {
        return new Category(request.name().trim(), request.description());
    }
}
