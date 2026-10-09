/*
 * LoggingGlobalFilter.java  (Topic 61 - api-gateway)
 *
 * WHAT: A GLOBAL filter = runs for EVERY request on EVERY route.
 *       It does two cross-cutting jobs (jobs every service would otherwise repeat):
 *         1. Gives every request a correlation id (X-Correlation-Id) - a "parcel tracking number"
 *            that is passed on to the microservice and returned to the client.
 *         2. Logs   method, path, status and how long the whole trip took.
 *
 * HOW A GATEWAY FILTER WORKS (reactive style):
 *       filter(exchange, chain)
 *         - "exchange" holds the request AND the response.
 *         - code BEFORE chain.filter(...)  runs on the way IN  (before calling the service)
 *         - code inside .then(...)          runs on the way OUT (after the service answered)
 *       Mono<Void> = "a promise that this work will finish later, with no value".
 */
package com.course.gateway.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoggingGlobalFilter.class);
    public static final String CORRELATION_HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long start = System.currentTimeMillis();

        // ---- ON THE WAY IN ----
        String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        final String id = correlationId;

        // Requests are immutable, so we "mutate" = build a modified copy with the extra header.
        ServerHttpRequest requestWithId = exchange.getRequest().mutate()
                .header(CORRELATION_HEADER, id)
                .build();
        ServerWebExchange newExchange = exchange.mutate().request(requestWithId).build();

        // Also send the id back to the client (useful when a user reports a problem).
        newExchange.getResponse().getHeaders().add(CORRELATION_HEADER, id);

        log.info("--> {} {} (correlationId={})",
                requestWithId.getMethod(), requestWithId.getURI().getPath(), id);

        // ---- pass the request on, and ON THE WAY OUT log the result ----
        return chain.filter(newExchange).then(Mono.fromRunnable(() -> {
            long tookMs = System.currentTimeMillis() - start;
            log.info("<-- {} {} status={} took={}ms",
                    requestWithId.getMethod(), requestWithId.getURI().getPath(),
                    newExchange.getResponse().getStatusCode(), tookMs);
        }));
    }

    /** Lower number = runs earlier. We want logging to wrap everything else. */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
