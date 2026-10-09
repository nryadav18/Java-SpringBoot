/*
 * @Value - injecting SETTINGS (not beans) from application.properties.
 *
 *   @Value("${shop.name}")                    -> must exist in the file
 *   @Value("${shop.free-delivery-above:500}") -> ":500" is the DEFAULT if the key is missing
 *   @Value("${shop.support-email:help@quickbite.example}") -> key is NOT in the file, default used
 *
 * Spring converts the text from the file into the parameter type (double, int, String[] ...).
 * Constructor injection works with @Value too, so the fields can be final.
 */
package com.course.di.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ShopSettings {

    private final String shopName;
    private final double deliveryFee;
    private final double freeDeliveryAbove;
    private final int maxItemsPerOrder;
    private final String supportEmail;
    private final String[] cities;

    public ShopSettings(@Value("${shop.name}") String shopName,
                        @Value("${shop.delivery-fee}") double deliveryFee,
                        @Value("${shop.free-delivery-above:500}") double freeDeliveryAbove,
                        @Value("${shop.max-items-per-order}") int maxItemsPerOrder,
                        @Value("${shop.support-email:help@quickbite.example}") String supportEmail,
                        @Value("${shop.cities}") String[] cities) {
        this.shopName = shopName;
        this.deliveryFee = deliveryFee;
        this.freeDeliveryAbove = freeDeliveryAbove;
        this.maxItemsPerOrder = maxItemsPerOrder;
        this.supportEmail = supportEmail;
        this.cities = cities;
    }

    /** Business rule driven by configuration: free delivery for big orders. */
    public double deliveryFeeFor(double foodTotal) {
        return foodTotal >= freeDeliveryAbove ? 0 : deliveryFee;
    }

    public String describe() {
        return "shop=" + shopName
                + ", deliveryFee=" + deliveryFee
                + ", freeDeliveryAbove=" + freeDeliveryAbove
                + ", maxItems=" + maxItemsPerOrder
                + ", supportEmail=" + supportEmail
                + ", cities=" + String.join("|", cities);
    }
}
