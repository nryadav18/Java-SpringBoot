/*
 * RestClientConfig.java  (Topic 60 - order-service)
 *
 * WHAT: Creates a RestClient that understands SERVICE NAMES instead of host:port.
 *
 * HOW:  @LoadBalanced on the RestClient.Builder bean tells Spring Cloud:
 *       "add an interceptor to every client built from this builder".
 *       Before each request the interceptor:
 *         1. reads the host part of the URL   ->  "product-service"
 *         2. asks the Eureka registry for all live instances of that name
 *         3. picks ONE using round-robin (8081, 8091, 8081, 8091, ...)
 *         4. rewrites the URL to the real address, e.g. http://localhost:8091/api/products/1
 *       This is called CLIENT-SIDE load balancing (the caller chooses the instance).
 */
package com.course.discovery.order;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    @LoadBalanced                       // <- the magic word
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient productRestClient(@LoadBalanced RestClient.Builder builder) {
        // NOTE: "product-service" is NOT a real host name. Only a @LoadBalanced
        // client can resolve it (through Eureka). A normal client would fail with
        // "UnknownHostException: product-service".
        return builder.baseUrl("http://product-service").build();
    }
}
