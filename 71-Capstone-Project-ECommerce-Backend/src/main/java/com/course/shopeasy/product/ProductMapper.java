package com.course.shopeasy.product;

import com.course.shopeasy.category.Category;

/**
 * Entity <-> DTO conversion for products (topic 33). Must be called inside a transaction,
 * because it touches the lazy "category" association.
 */
public final class ProductMapper {

    private ProductMapper() {
    }

    public static ProductResponse toResponse(Product product) {
        Category category = product.getCategory();
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getStockQuantity() > 0,
                product.getImageUrl(),
                category.getId(),
                category.getName(),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }

    public static Product toEntity(ProductRequest request, Category category) {
        return new Product(
                request.sku().trim().toUpperCase(),
                request.name().trim(),
                request.description(),
                request.price(),
                request.stockQuantity(),
                request.imageUrl(),
                category);
    }
}
