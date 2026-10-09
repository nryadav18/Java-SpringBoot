package com.course.shopeasy.security;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Writes 401 / 403 errors as ProblemDetail JSON - the SAME format as every other error in the API.
 *
 * Why is this needed? Security errors happen in the FILTER CHAIN, before any controller runs,
 * so @RestControllerAdvice never sees them. Spring Security instead calls:
 *   - AuthenticationEntryPoint -> "who are you?"        -> 401 Unauthorized (no / bad token)
 *   - AccessDeniedHandler      -> "you may not do that" -> 403 Forbidden   (wrong role)
 */
@Component
public class SecurityProblemHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public SecurityProblemHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // The WWW-Authenticate header tells clients which scheme to use (standard for 401 responses).
        response.setHeader("WWW-Authenticate", "Bearer");
        write(response, request, HttpStatus.UNAUTHORIZED, "Unauthorized", "unauthorized",
                "Authentication is required. Send a valid token in the 'Authorization: Bearer <token>' header.");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        write(response, request, HttpStatus.FORBIDDEN, "Forbidden", "forbidden",
                "You do not have permission to perform this action");
    }

    private void write(HttpServletResponse response, HttpServletRequest request, HttpStatus status,
                       String title, String type, String detail) throws IOException {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create("https://shopeasy.example/problems/" + type));
        pd.setInstance(URI.create(request.getRequestURI()));
        pd.setProperty("timestamp", Instant.now());

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), pd);
    }
}
