/*
 * RestClientConfig.java  (Topic 61 - order-service)
 *
 * WHAT: A load-balanced RestClient (explained in Topic 60). "http://product-service"
 *       is resolved through Eureka.
 */
package com.course.gateway.order;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient productRestClient(@LoadBalanced RestClient.Builder builder) {
        return builder.baseUrl("http://product-service").build();
    }
}
