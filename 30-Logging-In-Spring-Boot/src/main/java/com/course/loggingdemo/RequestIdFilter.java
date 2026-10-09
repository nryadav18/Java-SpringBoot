package com.course.loggingdemo;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * MDC (Mapped Diagnostic Context) demo.
 *
 * Problem: 50 users call the API at the same time. Their log lines are mixed together.
 *          Which lines belong to which request?
 * Solution: give every request a short "requestId" and put it into the MDC.
 *           The log pattern (logback-spring.xml) prints %X{requestId} on EVERY line
 *           logged while handling that request - even lines from OrderService,
 *           which knows nothing about HTTP.
 *
 * MDC is stored per THREAD. Tomcat re-uses threads, so we MUST clear it in "finally",
 * otherwise the next request on this thread would show the old id.
 *
 * A Filter runs BEFORE the controller for every request (Spring Boot registers it
 * automatically because it is a @Component).
 */
@Component
public class RequestIdFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Request-Id";
    public static final String MDC_KEY = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Re-use the caller's id if they sent one (useful across microservices), else create one.
        String requestId = request.getHeader(HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString().substring(0, 8);
        }

        MDC.put(MDC_KEY, requestId);
        response.setHeader(HEADER, requestId);   // the client can quote this id in a support ticket
        try {
            filterChain.doFilter(request, response);   // continue to the controller
        } finally {
            MDC.remove(MDC_KEY);                       // ALWAYS clean up
        }
    }
}
