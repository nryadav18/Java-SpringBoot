package com.course.configprops;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Shows the THREE ways to read configuration in Spring Boot:
 *   1. @Value("${key}")            - quick, for one or two single values
 *   2. @ConfigurationProperties    - type-safe group of related values (ShopProperties)
 *   3. Environment.getProperty()   - programmatic lookup (rarely needed in app code)
 *
 * Endpoints:
 *   GET /config/welcome   -> values read with @Value
 *   GET /config/shop      -> the whole ShopProperties record as JSON
 *   GET /config/profile   -> which profile(s) are active
 *   GET /config/secret    -> proves a secret was read from an ENV variable, without showing it
 */
@RestController
@RequestMapping("/config")
public class ConfigController {

    // 1) @Value with a plain key. The app fails to start if the key is missing.
    @Value("${spring.application.name}")
    private String appName;

    // @Value with a DEFAULT after the colon: used when the key is missing everywhere.
    @Value("${shop.welcome-message:Welcome to our shop!}")
    private String welcomeMessage;

    // Spring converts the text "true"/"false" into a boolean for us.
    @Value("${features.new-checkout:false}")
    private boolean newCheckoutEnabled;

    // A secret. application.yml says: payment.api-key: ${PAYMENT_API_KEY:not-set}
    // -> the real value comes from the PAYMENT_API_KEY environment variable, never from Git.
    @Value("${payment.api-key}")
    private String paymentApiKey;

    // 2) Type-safe properties, injected through the constructor like any other bean.
    private final ShopProperties shop;

    // 3) The Environment - Spring's view of ALL property sources (files, env vars, command line ...)
    private final Environment environment;

    public ConfigController(ShopProperties shop, Environment environment) {
        this.shop = shop;
        this.environment = environment;
    }

    @GetMapping("/welcome")
    public Map<String, Object> welcome() {
        Map<String, Object> result = new LinkedHashMap<>();   // LinkedHashMap keeps the key order in JSON
        result.put("application", appName);
        result.put("message", welcomeMessage);
        result.put("newCheckoutEnabled", newCheckoutEnabled);
        return result;
    }

    @GetMapping("/shop")
    public ShopProperties shop() {
        return shop;   // a record -> JSON automatically
    }

    @GetMapping("/profile")
    public Map<String, Object> profile() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("activeProfiles", Arrays.asList(environment.getActiveProfiles()));
        result.put("serverPort", environment.getProperty("server.port"));
        result.put("loggingLevelOfOurCode", environment.getProperty("logging.level.com.course", "INFO"));
        return result;
    }

    @GetMapping("/secret")
    public Map<String, Object> secret() {
        // NEVER return a real secret from an API. We only show whether it is set,
        // and a masked hint, so you can check your environment variable worked.
        boolean isSet = !"not-set".equals(paymentApiKey);
        String masked = isSet && paymentApiKey.length() > 4
                ? "****" + paymentApiKey.substring(paymentApiKey.length() - 4)
                : "****";
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("paymentApiKeyConfigured", isSet);
        result.put("maskedValue", masked);
        return result;
    }
}
