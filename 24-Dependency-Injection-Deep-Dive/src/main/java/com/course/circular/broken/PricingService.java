/*
 * CIRCULAR DEPENDENCY - the other half (see InventoryService).
 * Pricing wants stock levels to give "last few items" surge prices -> needs InventoryService.
 */
package com.course.circular.broken;

import org.springframework.stereotype.Service;

@Service
public class PricingService {

    private final InventoryService inventoryService;

    public PricingService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public double priceOf(String item) {
        return inventoryService.stockOf(item) < 3 ? 120.0 : 100.0;
    }
}
