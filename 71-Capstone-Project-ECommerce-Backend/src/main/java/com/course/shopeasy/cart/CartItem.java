package com.course.shopeasy.cart;

import java.math.BigDecimal;

import com.course.shopeasy.product.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * One line in a cart: "3 x Cotton T-Shirt". It stores only the product and the quantity -
 * the price always comes from the product, so the cart shows the CURRENT price.
 * This is the OWNING side of the Cart-CartItem relationship (it has the cart_id column).
 */
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    protected CartItem() {
    }

    /** Package-private: only Cart.addProduct creates items. */
    CartItem(Cart cart, Product product, int quantity) {
        this.cart = cart;
        this.product = product;
        changeQuantity(quantity);
    }

    public void changeQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        this.quantity = newQuantity;
    }

    public BigDecimal lineTotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public Long getId() { return id; }
    public Cart getCart() { return cart; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
}
