/*
 * CorrelationIdFilter - makes sure every request has a business "correlation-id".
 * ---------------------------------------------------------------
 * Trace ids are technical and new for every request. Sometimes you ALSO want an id that YOU
 * control - e.g. the mobile app sends "correlation-id: checkout-42" and support staff can search
 * all logs for it. Micrometer Tracing calls such a value "BAGGAGE": a key/value that travels
 * with the trace to every downstream service (configured in application.yml:
 * management.tracing.baggage.remote-fields / correlation.fields).
 *
 * This filter:
 *   - if the caller sent a "correlation-id" header -> Brave already picked it up as baggage, do nothing;
 *   - otherwise -> create one (short random value) and put it in scope for this request.
 * It also echoes the id back in the response header so the caller can quote it.
 *
 * The filter runs INSIDE Spring Boot's tracing filter, so a span is already current here.
 */
package com.course.tracing.order;

import io.micrometer.tracing.BaggageInScope;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    static final String CORRELATION_ID = "correlation-id";

    private final Tracer tracer;

    public CorrelationIdFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String existing = tracer.getBaggage(CORRELATION_ID) != null
                ? tracer.getBaggage(CORRELATION_ID).get()
                : null;

        if (existing != null && !existing.isBlank()) {
            response.setHeader(CORRELATION_ID, existing);
            chain.doFilter(request, response);
            return;
        }

        String newId = "corr-" + UUID.randomUUID().toString().substring(0, 8);
        response.setHeader(CORRELATION_ID, newId);
        // try-with-resources: the baggage is "in scope" only while the request is processed
        try (BaggageInScope baggage = tracer.createBaggageInScope(CORRELATION_ID, newId)) {
            chain.doFilter(request, response);
        }
    }
}
