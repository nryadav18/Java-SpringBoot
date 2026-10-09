/*
 * CIRCULAR DEPENDENCY - the FIX: redesign.
 *
 * Both services really needed only one small piece of knowledge: the stock level.
 * We extract that piece into its own bean. Now the arrows only point one way:
 *
 *     InventoryService ──► StockLevels ◄── PricingService
 *
 * No cycle, no problem. Redesigning is the best fix (better than @Lazy tricks).
 */
package com.course.circular.fixed;

import org.springframework.stereotype.Component;

@Component
public class StockLevels {

    public int stockOf(String item) {
        return "Paneer Tikka".equals(item) ? 2 : 10;
    }
}
