package com.course.jparelationships.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/*
 * Address - the "other side" of a ONE-TO-ONE relationship with Customer.
 * ---------------------------------------------------------------------
 * This side knows NOTHING about Customer: the relationship is UNIDIRECTIONAL.
 * Customer holds the reference and the foreign key column (customers.address_id).
 *
 * We could make it bidirectional by adding:
 *     @OneToOne(mappedBy = "address") private Customer customer;
 * but we don't need to navigate Address -> Customer, so we keep it simple.
 * (Rule: only make a relationship bidirectional if you really navigate both ways.)
 */
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false, length = 80)
    private String city;

    @Column(name = "zip_code", length = 20)
    private String zipCode;

    @Column(nullable = false, length = 60)
    private String country;

    protected Address() {
    }

    public Address(String street, String city, String zipCode, String country) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
    }

    public Long getId() { return id; }
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getZipCode() { return zipCode; }
    public String getCountry() { return country; }
}
