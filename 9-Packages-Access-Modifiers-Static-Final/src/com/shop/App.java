/*
 * File: src/com/shop/App.java
 * Package: com.shop   (the "entry point" of our tiny shop program)
 *
 * What this file demonstrates:
 *   - Using classes from 3 other packages with "import".
 *   - A "static import": use a static member (GST_RATE) without the class name.
 *   - A fully qualified name (java.time.LocalDate) used WITHOUT an import.
 *   - What we are NOT allowed to touch (see the commented-out lines).
 *
 * How to run (from the topic folder, in a terminal):
 *   javac -d out -sourcepath src src/com/shop/App.java
 *   java -cp out com.shop.App
 *
 *   -d out          -> put the compiled .class files into folder "out"
 *                      (javac creates out/com/shop/... to match the packages)
 *   -sourcepath src -> "my source code lives under src, find the other classes there"
 *   -cp out         -> "classpath": where the JVM should look for .class files
 *   com.shop.App    -> the FULLY QUALIFIED class name (package + class), NOT a file path
 *
 * Output:
 *   Pen (Stationery) Rs.20.0
 *   Pen with GST: Rs.23.60
 *   GST rate used: 0.18
 *   Notebook in stock? false
 *   Headphones (Electronics) Rs.2000.0
 *   FLASH SALE on Electronics: 25% off!
 *   Sale price: Rs.1500.00
 *   Error: Price cannot be negative: -5.0
 *   Shop opened on: 2025-01-15
 */
package com.shop;

import com.shop.model.Product;           // single-type import (recommended)
import com.shop.sale.FlashSaleProduct;
import com.shop.util.PriceUtils;
import static com.shop.util.PriceUtils.GST_RATE;   // static import of one constant

public class App {

    public static void main(String[] args) {
        Product pen = new Product("Pen", 20.0, "Stationery");
        System.out.println(pen);
        System.out.println("Pen with GST: " + PriceUtils.formatRupees(PriceUtils.withGst(pen.getPrice())));
        System.out.println("GST rate used: " + GST_RATE);   // thanks to the static import

        Product notebook = new Product("Notebook", 45.0, "Stationery");
        System.out.println("Notebook in stock? " + notebook.isInStock());

        FlashSaleProduct headphones = new FlashSaleProduct("Headphones", 2000.0, "Electronics", 25);
        System.out.println(headphones);
        System.out.println(headphones.getBanner());
        System.out.println("Sale price: " + PriceUtils.formatRupees(headphones.getSalePrice()));

        // Validation in the setter protects our private field:
        try {
            pen.setPrice(-5);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // ---------- Lines that would NOT compile (try un-commenting one!) ----------
        // pen.price = 0;                 // price is private in Product
        // pen.internalCode = "X";        // internalCode is package-private (only com.shop.model)
        // pen.category = "Toys";         // protected: App is not in com.shop.model and is not a child of Product
        // new PriceUtils();              // the constructor is private
        // StockChecker.hasStock("SKU");  // StockChecker is not public, so it cannot even be imported

        // A fully qualified name (package + class) works WITHOUT any import line:
        java.time.LocalDate openingDay = java.time.LocalDate.of(2025, 1, 15);
        System.out.println("Shop opened on: " + openingDay);
    }
}
