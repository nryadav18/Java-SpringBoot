package com.course.requesthandling;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * GLOBAL CORS configuration (Cross-Origin Resource Sharing).
 *
 * The problem: a React/Angular app running at http://localhost:5173 calls our API at
 * http://localhost:8080. Different port = different "origin", so the BROWSER blocks the
 * response unless our server says "I allow that origin" with CORS headers.
 *
 * (curl, Postman and server-to-server calls are NOT browsers - CORS does not apply to them.)
 *
 * Global config here = one place for all /api/** endpoints.
 * @CrossOrigin on a controller/method = a local exception (see ProductController.headersDemo).
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:3000")   // our front-end dev servers
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                .allowedHeaders("*")
                .exposedHeaders("Location", "X-Total-Count")   // let browser JS READ these response headers
                .maxAge(3600);                                 // browser may cache the "preflight" answer for 1 hour
    }
}
