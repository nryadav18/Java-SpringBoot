package com.course.k8sdemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/*
 * AppProperties.java - type-safe configuration (topic 29) for everything under "app.*".
 *
 * Where do the values come from?
 *   - on your laptop: application.properties (defaults)
 *   - in Kubernetes : environment variables from k8s/configmap.yaml and k8s/secret.yaml
 *       APP_RESTAURANT_NAME   -> app.restaurant-name     (relaxed binding)
 *       APP_WELCOME_MESSAGE   -> app.welcome-message
 *       APP_PAYMENT_API_KEY   -> app.payment-api-key     (a SECRET - never log or return it!)
 *
 * The SAME jar/image runs everywhere; only the environment changes (12-factor app).
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(String restaurantName, String welcomeMessage, String paymentApiKey) {

    /** Shows only that a key exists, e.g. "****9f2a" - safe to display. */
    public String maskedPaymentApiKey() {
        if (paymentApiKey == null || paymentApiKey.length() < 4) {
            return "(not set)";
        }
        return "****" + paymentApiKey.substring(paymentApiKey.length() - 4);
    }
}
