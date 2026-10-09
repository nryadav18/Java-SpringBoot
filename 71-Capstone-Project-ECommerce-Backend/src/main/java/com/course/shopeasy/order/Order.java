package com.course.shopeasy.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.course.shopeasy.common.exception.BusinessRuleException;
import com.course.shopeasy.common.persistence.AuditableEntity;
import com.course.shopeasy.product.Product;
import com.course.shopeasy.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

/**
 * A placed order. Table name "orders" because ORDER is an SQL keyword (ORDER BY).
 *
 * Unlike the cart, an order is a historical RECORD: it copies the product name and price at the moment
 * of purchase (see OrderItem). The total is calculated once, by the entity itself, and never sent by the client.
 */
@Entity
@Table(name = "orders")
public class Order extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "shipping_address", nullable = false, length = 500)
    private String shippingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {
    }

    public Order(User user, String shippingAddress) {
        this.user = user;
        this.shippingAddress = shippingAddress;
    }

    /** Adds a line with a snapshot of the product's current name and price, and updates the total. */
    public void addLine(Product product, int quantity) {
        OrderItem item = new OrderItem(this, product, quantity);
        items.add(item);
        totalAmount = totalAmount.add(item.getLineTotal());
    }

    /** Moves the order to a new status, following the rules in OrderStatus. */
    public void changeStatus(OrderStatus next) {
        if (!status.canMoveTo(next)) {
            throw new BusinessRuleException("Order %d cannot go from %s to %s".formatted(id, status, next));
        }
        this.status = next;
    }

    public boolean belongsTo(Long userId) {
        return user.getId().equals(userId);
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getShippingAddress() { return shippingAddress; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
}
