package com.course.jparelationships.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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

import java.util.LinkedHashSet;
import java.util.Set;

/*
 * Customer - the centre of our little shop model.
 * ===============================================
 *
 * 1) @OneToOne  Customer -> Address
 *    - Customer is the OWNING side: its table has the foreign key column  customers.address_id
 *    - cascade = ALL: saving/deleting a customer also saves/deletes the address
 *    - orphanRemoval = true: setting a NEW address deletes the old one (no orphan rows)
 *    - fetch = LAZY: don't load the address until somebody calls getAddress()
 *
 * 2) @OneToMany Customer -> PurchaseOrder   (the "one" side of a bidirectional one-to-many)
 *    - mappedBy = "customer": "the foreign key is managed by the field 'customer' in PurchaseOrder".
 *      So this is the INVERSE (non-owning) side. Changing only this list does NOT change the database!
 *    - That's why we have addOrder()/removeOrder() helpers that update BOTH sides.
 *
 * About @JsonManagedReference: used ONLY by the /raw endpoint to show one way of breaking
 * the JSON infinite loop Customer -> orders -> customer -> orders ... The normal endpoints use DTOs.
 */
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", unique = true)          // FK column in the customers table
    private Address address;

    // Why a Set and not a List?  When we JOIN FETCH orders AND orders.products in one query
    // (see CustomerRepository.findWithDetailsById), a List ("bag") would contain the same order
    // once per product (duplicates!), and fetching two Lists at once even throws
    // MultipleBagFetchException. A Set removes the duplicates. @OrderBy keeps a stable order.
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)  // LAZY by default
    @OrderBy("id")
    @JsonManagedReference                                    // "forward" part: serialized normally
    private Set<PurchaseOrder> orders = new LinkedHashSet<>(); // always initialise collections!

    protected Customer() {
    }

    public Customer(String name, String email, Address address) {
        this.name = name;
        this.email = email;
        this.address = address;
    }

    // ---------- helper methods keep BOTH sides of the bidirectional relationship in sync ----------
    public void addOrder(PurchaseOrder order) {
        orders.add(order);          // inverse side (Java object graph)
        order.setCustomer(this);    // owning side  (this is what Hibernate writes to orders.customer_id)
    }

    public void removeOrder(PurchaseOrder order) {
        orders.remove(order);
        order.setCustomer(null);    // with orphanRemoval = true the order row will be DELETED
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    public Set<PurchaseOrder> getOrders() { return orders; }
}
