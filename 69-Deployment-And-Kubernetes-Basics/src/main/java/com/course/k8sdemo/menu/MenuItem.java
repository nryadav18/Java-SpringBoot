package com.course.k8sdemo.menu;

import java.math.BigDecimal;

/*
 * MenuItem.java - one dish on the menu (record used as a DTO).
 */
public record MenuItem(int id, String name, BigDecimal price, boolean vegetarian) {
}
