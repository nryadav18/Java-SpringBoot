/*
 * ProductClientConfig - extra beans used ONLY by ProductClient.
 * ---------------------------------------------------------------
 * IMPORTANT: this class is deliberately NOT annotated with @Configuration.
 * If it were, component scanning would register these beans GLOBALLY and they would
 * apply to EVERY Feign client in the application. Referenced only from
 * @FeignClient(configuration = ProductClientConfig.class) they live in a small
 * child context that belongs to "product-service" alone.
 *
 * Beans you can customise here: ErrorDecoder, RequestInterceptor, Logger.Level,
 * Retryer, Request.Options (timeouts), Encoder/Decoder...
 * (Timeouts and log level are set in application.yml instead - easier to change.)
 */
package com.course.feign.order.config;

import com.course.feign.order.exception.ProductClientErrorDecoder;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class ProductClientConfig {

    /** Turns 404 / 409 / 5xx answers from product-service into OUR exceptions. */
    @Bean
    public ErrorDecoder productErrorDecoder() {
        return new ProductClientErrorDecoder();
    }

    /**
     * A RequestInterceptor runs before EVERY request this client sends.
     * Typical real uses: add an Authorization header (pass a JWT along), a correlation id,
     * an API key... Here we add a simple header so product-service knows who is calling.
     */
    @Bean
    public RequestInterceptor callerHeaderInterceptor() {
        return template -> template.header("X-Caller-Service", "order-service");
    }
}
