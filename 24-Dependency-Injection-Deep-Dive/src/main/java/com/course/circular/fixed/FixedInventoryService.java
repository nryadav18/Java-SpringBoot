/*
 * Fixed version: depends only on StockLevels (no longer on PricingService).
 */
package com.course.circular.fixed;

import org.springframework.stereotype.Service;

@Service
public class FixedInventoryService {

    private final StockLevels stockLevels;

    public FixedInventoryService(StockLevels stockLevels) {
        this.stockLevels = stockLevels;
    }

    public boolean inStock(String item) {
        return stockLevels.stockOf(item) > 0;
    }
}
