package com.course.shopeasy.common.web;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Gives every HTTP request an id and puts it into the logging MDC (topic 30).
 *
 * MDC = "Mapped Diagnostic Context": a per-thread map that the log pattern can print (%X{requestId}).
 * Every log line written while handling this request now carries the same id, so when a customer says
 * "my order failed", support can search the logs for that one request. The id is also returned in the
 * X-Request-Id response header (and a caller may send its own id in the same header).
 *
 * It runs FIRST (highest precedence), before Spring Security, so even 401 responses are traceable.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Request-Id";
    private static final String MDC_KEY = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestId = request.getHeader(HEADER);
        if (requestId == null || requestId.isBlank() || requestId.length() > 64) {
            requestId = UUID.randomUUID().toString().substring(0, 8);   // short id is enough to search logs
        }
        MDC.put(MDC_KEY, requestId);
        response.setHeader(HEADER, requestId);
        try {
            chain.doFilter(request, response);
        } finally {
            // Threads are reused by Tomcat's pool - ALWAYS clean up, or the next request inherits this id.
            MDC.remove(MDC_KEY);
        }
    }
}
