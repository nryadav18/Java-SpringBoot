/*
 * CIRCULAR DEPENDENCY - the broken version.
 *
 *   InventoryService needs PricingService   (constructor)
 *   PricingService   needs InventoryService (constructor)
 *
 * To create A, Spring needs B. To create B, Spring needs A. Chicken and egg!
 * Spring detects this at startup and throws BeanCurrentlyInCreationException.
 *
 * NOTE: this package is OUTSIDE com.course.di, so the main app never scans it.
 * App.java starts a separate container just for this demo.
 */
package com.course.circular.broken;

import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private final PricingService pricingService;

    public InventoryService(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    public int stockOf(String item) {
        return 10;
    }
}
