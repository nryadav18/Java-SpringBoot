/*
 * File: src/com/shop/sale/FlashSaleProduct.java
 * Package: com.shop.sale   (a DIFFERENT package from Product)
 *
 * What this file demonstrates:
 *   - "import" of a class from another package (com.shop.model.Product).
 *   - "protected" in action: a CHILD class in ANOTHER package can use the
 *     protected field "category" that it inherited from Product.
 *   - It can NOT use "internalCode" (package-private) or "price" (private).
 *
 * How to run: no main() here. Used by com.shop.App.
 */
package com.shop.sale;

import com.shop.model.Product;   // without this line we'd have to write com.shop.model.Product everywhere

public class FlashSaleProduct extends Product {

    private final int discountPercent;   // final: set once in the constructor, never changed

    public FlashSaleProduct(String name, double price, String category, int discountPercent) {
        super(name, price, category);
        this.discountPercent = discountPercent;
    }

    public double getSalePrice() {
        // getPrice() is public -> fine.
        // this.price would NOT compile -> price is private in Product.
        return getPrice() - (getPrice() * discountPercent / 100.0);
    }

    public String getBanner() {
        // "category" is protected in Product -> a child class may use it,
        // even though we are in a different package.
        return "FLASH SALE on " + category + ": " + discountPercent + "% off!";

        // The line below would NOT compile, because internalCode is package-private
        // (visible only inside com.shop.model):
        // return internalCode;
    }
}
