package com.course.jparelationships.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/*
 * PurchaseOrder - one order placed by one customer, containing many products.
 * ===========================================================================
 * Why not call it "Order"? Because ORDER is a reserved SQL word (ORDER BY).
 * A table called "order" breaks many databases. So: class PurchaseOrder, table "orders".
 *
 * 1) @ManyToOne  PurchaseOrder -> Customer       ("many orders belong to one customer")
 *    - This is the OWNING side of the Customer/Order relationship: the foreign key column
 *      orders.customer_id lives in THIS table. In a one-to-many, the "many" side always owns it.
 *    - fetch = LAZY: @ManyToOne is EAGER by default in JPA - we change it (best practice).
 *
 * 2) @ManyToMany PurchaseOrder <-> Product
 *    - A many-to-many needs a third "join table" order_products(order_id, product_id).
 *    - This side declares @JoinTable, so it is the OWNING side. Product uses mappedBy.
 *    - NO cascade here: deleting an order must never delete the products in the catalogue!
 */
@Entity
@Table(name = "orders")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 20)
    private String orderNumber;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)      // FK column orders.customer_id
    @JsonBackReference                                       // "back" part: NOT serialized (breaks the loop)
    private Customer customer;

    @ManyToMany
    @JoinTable(
            name = "order_products",                                  // the join table
            joinColumns = @JoinColumn(name = "order_id"),             // FK to THIS entity
            inverseJoinColumns = @JoinColumn(name = "product_id"))    // FK to the OTHER entity
    private Set<Product> products = new LinkedHashSet<>();

    protected PurchaseOrder() {
    }

    public PurchaseOrder(String orderNumber) {
        this.orderNumber = orderNumber;
        this.orderDate = LocalDateTime.now();
    }

    // helper: keep both sides of the many-to-many in sync
    public void addProduct(Product product) {
        products.add(product);
        product.getOrders().add(this);
    }

    public BigDecimal getTotal() {
        return products.stream().map(Product::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public Customer getCustomer() { return customer; }
    void setCustomer(Customer customer) { this.customer = customer; }   // package-private: use Customer.addOrder()
    public Set<Product> getProducts() { return products; }
}
