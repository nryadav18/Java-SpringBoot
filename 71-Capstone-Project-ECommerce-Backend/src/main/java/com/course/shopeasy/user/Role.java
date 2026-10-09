package com.course.shopeasy.user;

/**
 * The two kinds of users in ShopEasy (topic 48: roles).
 *
 * ADMIN    - manages the catalog (categories, products) and all orders.
 * CUSTOMER - browses products, manages their own cart, places and cancels their own orders.
 *
 * Stored as text in the "role" column (@Enumerated(EnumType.STRING)) - never as a number,
 * because reordering the enum would silently change everyone's role.
 * Spring Security expects authorities like "ROLE_ADMIN"; {@link #authority()} adds that prefix.
 */
public enum Role {
    ADMIN,
    CUSTOMER;

    public String authority() {
        return "ROLE_" + name();
    }
}
