/*
 * ApiKeyGlobalFilter.java  (Topic 61 - api-gateway)
 *
 * WHAT: A tiny "security guard at the main gate".
 *       Rule: creating an order (POST /api/orders...) needs the header
 *             X-API-KEY: <value of gateway.api-key in application.yml>
 *       Without it the gateway answers 401 Unauthorized and the request NEVER reaches
 *       order-service. This shows how a gateway handles a cross-cutting concern ONCE
 *       instead of in every service.
 *
 *       In real projects the gateway usually validates a JWT / OAuth2 token instead
 *       (Topics 49 and 50). A fixed API key is used here only to keep the idea simple.
 */
package com.course.gateway.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ApiKeyGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyGlobalFilter.class);

    private final String expectedApiKey;

    public ApiKeyGlobalFilter(@Value("${gateway.api-key}") String expectedApiKey) {
        this.expectedApiKey = expectedApiKey;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        boolean isCreateOrder = HttpMethod.POST.equals(request.getMethod())
                && request.getURI().getPath().startsWith("/api/orders");

        if (isCreateOrder) {
            String givenKey = request.getHeaders().getFirst("X-API-KEY");
            if (!expectedApiKey.equals(givenKey)) {
                log.warn("Blocked {} {} - missing or wrong X-API-KEY", request.getMethod(), request.getURI().getPath());
                // Stop here: set 401 and finish the response. chain.filter(...) is NOT called,
                // so order-service never sees this request.
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }
        return chain.filter(exchange);   // allowed -> continue to the next filter / the service
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;   // right after the logging filter
    }
}
