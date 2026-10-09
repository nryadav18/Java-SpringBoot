/*
 * File: src/com/shop/model/StockChecker.java
 * Package: com.shop.model
 *
 * What this file demonstrates:
 *   - A PACKAGE-PRIVATE class: notice there is NO "public" before "class".
 *     Only classes inside com.shop.model (like Product) can use it.
 *     If App (package com.shop) tried "new StockChecker()" or
 *     "StockChecker.hasStock(...)", the compiler would say:
 *       error: StockChecker is not public in com.shop.model;
 *              cannot be accessed from outside package
 *   - This is how you hide "helper" classes that are internal details.
 *
 * How to run: no main() here. Used by Product.
 */
package com.shop.model;

class StockChecker {

    // static: we don't need an object of StockChecker to call these helpers.
    static String makeCode(String productName) {
        // "PEN" -> "SKU-PEN"  (a tiny fake product code)
        return "SKU-" + productName.toUpperCase().replace(" ", "-");
    }

    static boolean hasStock(String code) {
        // Pretend the warehouse is out of notebooks, everything else is in stock.
        return !code.equals("SKU-NOTEBOOK");
    }
}
