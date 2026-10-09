package com.course.shopeasy.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.course.shopeasy.category.Category;
import com.course.shopeasy.common.exception.InsufficientStockException;

/**
 * Tests the stock rules that live INSIDE the Product entity ("rich domain model").
 * No mocks needed at all - the cheapest and fastest kind of test.
 */
class ProductTest {

    private final Product product = new Product("BOOK-999", "Test Book", null,
            new BigDecimal("499.00"), 5, null, new Category("Books", null));

    @Test
    void decreaseStockReducesQuantity() {
        product.decreaseStock(3);
        assertThat(product.getStockQuantity()).isEqualTo(2);
    }

    @Test
    void cannotSellMoreThanInStock() {
        assertThatThrownBy(() -> product.decreaseStock(6))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessage("Not enough stock for 'Test Book': requested 6, available 5");
        assertThat(product.getStockQuantity()).isEqualTo(5);   // unchanged
    }

    @Test
    void quantityMustBePositive() {
        assertThatThrownBy(() -> product.decreaseStock(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> product.increaseStock(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void increaseStockAddsQuantity() {
        product.increaseStock(10);
        assertThat(product.getStockQuantity()).isEqualTo(15);
    }
}
