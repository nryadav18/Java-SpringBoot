/*
 * File: src/com/shop/model/Product.java
 * Package: com.shop.model
 *
 * What this file demonstrates:
 *   - The "package" line (it MUST be the first statement in the file).
 *   - All 4 access levels on fields and methods:
 *       public            -> everyone can use it
 *       protected         -> same package + child classes (even in other packages)
 *       (default/package) -> ONLY classes in the same package (com.shop.model)
 *       private           -> ONLY this class
 *
 * How to run: this file has no main(). It is used by com.shop.App.
 *   See README.md or the lesson ("How to run") for the javac -d / java -cp commands.
 */
package com.shop.model;

public class Product {

    // private: nobody outside this class can touch the price directly.
    // They must go through getPrice()/setPrice(), where we can VALIDATE.
    private String name;
    private double price;

    // protected: child classes (like FlashSaleProduct in package com.shop.sale)
    // can read/write this, and so can classes in com.shop.model.
    protected String category;

    // no modifier = "default" or "package-private":
    // only classes inside com.shop.model can see it (for example StockChecker).
    // App (in package com.shop) can NOT see it.
    String internalCode;

    public Product(String name, double price, String category) {
        this.name = name;
        setPrice(price);                 // reuse validation from the setter
        this.category = category;
        // StockChecker is a package-private class in the same package -> allowed here
        this.internalCode = StockChecker.makeCode(name);
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative: " + price);
        }
        this.price = price;
    }

    // public method that USES the package-private helper internally.
    public boolean isInStock() {
        return StockChecker.hasStock(internalCode);
    }

    @Override
    public String toString() {
        return name + " (" + category + ") Rs." + price;
    }
}
