/*
 * ShopProperties.java  (Topic 62 - product-service)
 *
 * WHAT: Type-safe access to all "shop.*" settings (Topic 29), whatever file they come from:
 *       config-repo/application.yml, config-repo/product-service.yml, ...-dev.yml, or the
 *       local fallback in src/main/resources/application.yml.
 *
 * WHY A CLASS WITH SETTERS AND NOT A RECORD?
 *       When you call POST /actuator/refresh, Spring Cloud RE-BINDS every
 *       @ConfigurationProperties bean: it calls the setters again with the new values.
 *       A record is immutable (no setters), so it could NOT be updated in place.
 *       => For properties that must change at runtime, use a JavaBean (getters + setters).
 */
package com.course.configdemo.product;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "shop")
public class ShopProperties {

    private String companyName;
    private String supportEmail;
    private String welcomeMessage;
    private int discountPercent;
    private String paymentApiKey;
    private final Feature feature = new Feature();   // nested: shop.feature.*

    public static class Feature {
        private boolean newArrivals;                 // shop.feature.new-arrivals

        public boolean isNewArrivals() { return newArrivals; }
        public void setNewArrivals(boolean newArrivals) { this.newArrivals = newArrivals; }
    }

    // ----- getters and setters (relaxed binding: company-name -> companyName) -----
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getSupportEmail() { return supportEmail; }
    public void setSupportEmail(String supportEmail) { this.supportEmail = supportEmail; }

    public String getWelcomeMessage() { return welcomeMessage; }
    public void setWelcomeMessage(String welcomeMessage) { this.welcomeMessage = welcomeMessage; }

    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }

    public String getPaymentApiKey() { return paymentApiKey; }
    public void setPaymentApiKey(String paymentApiKey) { this.paymentApiKey = paymentApiKey; }

    public Feature getFeature() { return feature; }
}
