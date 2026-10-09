/*
 * Fixed version: depends only on StockLevels (no longer on InventoryService).
 */
package com.course.circular.fixed;

import org.springframework.stereotype.Service;

@Service
public class FixedPricingService {

    private final StockLevels stockLevels;

    public FixedPricingService(StockLevels stockLevels) {
        this.stockLevels = stockLevels;
    }

    public double priceOf(String item) {
        return stockLevels.stockOf(item) < 3 ? 120.0 : 100.0;   // "only a few left" price
    }
}
