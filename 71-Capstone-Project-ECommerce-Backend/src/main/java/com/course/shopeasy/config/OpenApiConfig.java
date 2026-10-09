package com.course.shopeasy.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * OpenAPI (Swagger) documentation settings (topic 44).
 *
 * Open http://localhost:8080/swagger-ui.html to see and TRY every endpoint.
 *
 * The @SecurityScheme below adds the green "Authorize" button to Swagger UI:
 *   1. Call POST /api/v1/auth/login and copy the "accessToken" from the response.
 *   2. Click "Authorize", paste the token (without the word "Bearer"), click "Authorize".
 *   3. Swagger UI now sends "Authorization: Bearer <token>" on every call to endpoints that are
 *      marked with @SecurityRequirement(name = "bearerAuth").
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "ShopEasy API",
                version = "v1",
                description = "E-commerce REST backend - capstone of the Java + Spring Boot course. "
                        + "Demo users: admin@shopeasy.com / Admin@123 and customer@shopeasy.com / Customer@123",
                contact = @Contact(name = "ShopEasy Team", email = "dev@shopeasy.example"),
                license = @License(name = "For learning purposes")))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Paste the accessToken returned by /api/v1/auth/login")
public class OpenApiConfig {
}
