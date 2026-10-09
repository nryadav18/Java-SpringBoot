package com.course.shopeasy.cart;

import java.util.List;

import com.course.shopeasy.product.Product;

/** Entity -> DTO conversion for the cart (topic 33). Call it inside a transaction (lazy associations). */
public final class CartMapper {

    private CartMapper() {
    }

    public static CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream().map(CartMapper::toItemResponse).toList();
        int totalQuantity = items.stream().mapToInt(CartItemResponse::quantity).sum();
        return new CartResponse(items, totalQuantity, cart.total());
    }

    private static CartItemResponse toItemResponse(CartItem item) {
        Product p = item.getProduct();
        return new CartItemResponse(p.getId(), p.getSku(), p.getName(), p.getPrice(),
                item.getQuantity(), item.lineTotal(), p.getStockQuantity());
    }
}
