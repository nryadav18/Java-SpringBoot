package com.course.shopeasy.cart;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.course.shopeasy.security.UserPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * The logged-in customer's cart. Notice: no user id in any URL. The cart is always "MY cart",
 * and "me" comes from the verified JWT via @AuthenticationPrincipal. Clients cannot ask for another
 * user's cart because there is no way to even express it (secure by design).
 */
@RestController
@RequestMapping("/api/v1/cart")
@Tag(name = "Cart", description = "The logged-in customer's shopping cart (CUSTOMER)")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = "Show my cart")
    public CartResponse getCart(@AuthenticationPrincipal UserPrincipal user) {
        return cartService.getCart(user.id());
    }

    @PostMapping("/items")
    @Operation(summary = "Add a product to my cart (adds to the quantity if it is already there)")
    public CartResponse addItem(@AuthenticationPrincipal UserPrincipal user,
                                @Valid @RequestBody AddCartItemRequest request) {
        return cartService.addItem(user.id(), request);
    }

    @PutMapping("/items/{productId}")
    @Operation(summary = "Change the quantity of a product in my cart")
    public CartResponse updateItem(@AuthenticationPrincipal UserPrincipal user,
                                   @PathVariable Long productId,
                                   @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItem(user.id(), productId, request);
    }

    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove a product from my cart")
    public CartResponse removeItem(@AuthenticationPrincipal UserPrincipal user, @PathVariable Long productId) {
        return cartService.removeItem(user.id(), productId);
    }

    @DeleteMapping
    @Operation(summary = "Empty my cart")
    public ResponseEntity<Void> clear(@AuthenticationPrincipal UserPrincipal user) {
        cartService.clear(user.id());
        return ResponseEntity.noContent().build();
    }
}
