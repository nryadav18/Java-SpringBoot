package com.course.shopeasy.cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.course.shopeasy.common.persistence.AuditableEntity;
import com.course.shopeasy.product.Product;
import com.course.shopeasy.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

/**
 * A customer's shopping cart: exactly one per user (unique user_id), stored in the database so it
 * survives logouts and works on every device.
 *
 * Relationships (topic 40):
 *   Cart 1 --- 1 User          @OneToOne, the carts table holds the foreign key user_id
 *   Cart 1 --- * CartItem      @OneToMany(mappedBy = "cart"): CartItem owns the foreign key cart_id
 *
 * cascade = ALL + orphanRemoval = true: items live and die with their cart. Removing an item from the
 * list is enough - Hibernate deletes the row. That is why there is no CartItemRepository.
 *
 * "Aggregate root" idea (DDD-lite, topic 59): outside code changes items ONLY through Cart's methods,
 * so the rules (no duplicate lines, positive quantity) are enforced in one place.
 */
@Entity
@Table(name = "carts")
public class Cart extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<CartItem> items = new ArrayList<>();

    protected Cart() {
    }

    public Cart(User user) {
        this.user = user;
    }

    /** Adds a product, or increases the quantity if it is already in the cart (no duplicate lines). */
    public CartItem addProduct(Product product, int quantity) {
        Optional<CartItem> existing = findItem(product.getId());
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.changeQuantity(item.getQuantity() + quantity);
            return item;
        }
        CartItem item = new CartItem(this, product, quantity);
        items.add(item);
        return item;
    }

    public Optional<CartItem> findItem(Long productId) {
        return items.stream().filter(i -> i.getProduct().getId().equals(productId)).findFirst();
    }

    public boolean removeProduct(Long productId) {
        return items.removeIf(i -> i.getProduct().getId().equals(productId));
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    /** Current total, using TODAY's product prices (the order will take a snapshot of them). */
    public BigDecimal total() {
        return items.stream().map(CartItem::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() { return id; }
    public User getUser() { return user; }

    /** Read-only view: callers cannot bypass addProduct / removeProduct. */
    public List<CartItem> getItems() { return Collections.unmodifiableList(items); }
}
