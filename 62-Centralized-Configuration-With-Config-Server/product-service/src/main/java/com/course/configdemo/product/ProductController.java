/*
 * ProductController.java  (Topic 62 - product-service)
 *
 * WHAT: A product API whose BEHAVIOUR is driven by central configuration:
 *         - shop.discount-percent      -> the discounted price we show
 *         - shop.feature.new-arrivals  -> a "feature flag": the endpoint is on or off
 *       Change these values in config-repo, call /actuator/refresh, and the API changes
 *       without a rebuild or restart.
 *
 * TRY:  GET http://localhost:8081/api/products
 *       GET http://localhost:8081/api/products/new-arrivals    (404 unless the flag is true)
 *       GET http://localhost:8081/api/products/secret-check    (shows the decrypted secret is present)
 */
package com.course.configdemo.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    /** What we send back: original price, discount and final price. */
    public record ProductView(Long id, String name, double price, int discountPercent, double finalPrice) {
    }

    // ShopProperties is a normal singleton, but Spring Cloud re-binds its fields on refresh,
    // so reading it on EVERY request always gives the latest values. No @RefreshScope needed.
    private final ShopProperties shop;

    public ProductController(ShopProperties shop) {
        this.shop = shop;
    }

    @GetMapping
    public List<ProductView> findAll() {
        int discount = shop.getDiscountPercent();
        log.debug("Building product list with discount {}%", discount);   // visible with the dev profile
        return List.of(
                view(1L, "Laptop", 55000.0, discount),
                view(2L, "Wireless Mouse", 799.0, discount),
                view(3L, "Mechanical Keyboard", 3499.0, discount));
    }

    @GetMapping("/new-arrivals")
    public List<ProductView> newArrivals() {
        if (!shop.getFeature().isNewArrivals()) {
            // Feature flag OFF -> behave as if the endpoint does not exist.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "New arrivals are not available yet");
        }
        return List.of(view(10L, "Smart Watch", 12999.0, shop.getDiscountPercent()));
    }

    @GetMapping("/secret-check")
    public Map<String, Object> secretCheck() {
        String key = shop.getPaymentApiKey();
        boolean decrypted = key != null && !key.startsWith("{cipher}") && !key.isBlank();
        // NEVER return a real secret in an API response or a log. We only show its first 4 chars.
        String masked = decrypted ? key.substring(0, Math.min(4, key.length())) + "****" : String.valueOf(key);
        return Map.of("paymentApiKeyReceived", decrypted, "maskedValue", masked);
    }

    private ProductView view(Long id, String name, double price, int discountPercent) {
        double finalPrice = Math.round(price * (100 - discountPercent)) / 100.0;
        return new ProductView(id, name, price, discountPercent, finalPrice);
    }
}
