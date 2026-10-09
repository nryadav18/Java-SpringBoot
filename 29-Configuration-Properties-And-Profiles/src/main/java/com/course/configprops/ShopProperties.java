package com.course.configprops;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

/*
 * TYPE-SAFE configuration: every key under "shop:" in application.yml is copied
 * into this record when the application starts.
 *
 *   shop.name                     -> name
 *   shop.discount-percent         -> discountPercent     (kebab-case in YAML, camelCase in Java)
 *   shop.session-timeout          -> sessionTimeout      ("30m" is converted to a Duration!)
 *   shop.supported-payment-methods-> supportedPaymentMethods (a YAML list -> List<String>)
 *   shop.contact.email            -> contact().email()   (nested record)
 *
 * @Validated + the jakarta.validation annotations mean: if the config is WRONG
 * (e.g. discount-percent: 80), the app REFUSES TO START with a clear message.
 * Failing fast at startup is much better than failing at 2 a.m. in production.
 *
 * Why a record? It is immutable (no setters), short, and perfect for "read-only settings".
 */
@Validated
@ConfigurationProperties(prefix = "shop")
public record ShopProperties(

        @NotBlank String name,

        @NotBlank @DefaultValue("INR") String currency,

        @Min(0) @Max(50) @DefaultValue("0") int discountPercent,

        @Positive @DefaultValue("10") int maxItemsPerOrder,

        @DefaultValue("30m") Duration sessionTimeout,

        @NotEmpty List<String> supportedPaymentMethods,

        @Valid Contact contact
) {

    /** Nested group "shop.contact.*". @Valid above makes Spring validate inside it too. */
    public record Contact(@Email @NotBlank String email, String phone) {
    }
}
