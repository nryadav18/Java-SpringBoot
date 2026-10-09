/*
 * PaymentClientConfig - creates the RestClient used to call payment-service.
 * ---------------------------------------------------------------
 * THE most important line in this topic:
 *     we build the RestClient from the RestClient.Builder that Spring Boot INJECTS.
 *
 * Spring Boot pre-configures that builder with an ObservationRegistry. Thanks to it, every
 * call made with this RestClient:
 *   - creates a CLIENT span ("http post"),
 *   - adds the "traceparent" header (W3C Trace Context) so payment-service joins the trace,
 *   - adds our "correlation-id" baggage header.
 *
 * If you write RestClient.create() or new RestTemplate() yourself, NONE of that happens and
 * the trace is broken into two unrelated traces. (Same rule for RestTemplateBuilder and
 * WebClient.Builder. OpenFeign is instrumented automatically when micrometer is present.)
 */
package com.course.tracing.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PaymentClientConfig {

    @Bean
    public RestClient paymentRestClient(RestClient.Builder builder,
                                        @Value("${payment-service.base-url}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }
}
