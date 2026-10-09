package com.course.taskapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/*
 * Global OpenAPI settings.
 *
 * springdoc builds the documentation automatically from our controllers.
 * Here we only add the "cover page" (title, version, contact ...) and split
 * the API into GROUPS. Each group appears in the drop-down at the top right
 * of Swagger UI ("Select a definition").
 */
@Configuration
public class OpenApiConfig {

    // The "cover page" of the documentation.
    @Bean
    public OpenAPI taskApiOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task API")
                        .version("1.0.0")
                        .description("""
                                A small task-management API used in topic 44 of the course.
                                It shows REST naming, status codes, pagination, filtering,
                                versioning and a standard error format (ProblemDetail).""")
                        .contact(new Contact().name("Course Team").email("team@example.com"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(new Server().url("http://localhost:8080").description("Local machine")));
    }

    // Group 1: everything under /api/v1/**
    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/api/v1/**")
                .build();
    }

    // Group 2: the newer v2 endpoints
    @Bean
    public GroupedOpenApi v2Api() {
        return GroupedOpenApi.builder()
                .group("v2")
                .pathsToMatch("/api/v2/**")
                .build();
    }

    // Group 3: the header-versioning demo
    @Bean
    public GroupedOpenApi versioningDemoApi() {
        return GroupedOpenApi.builder()
                .group("versioning-demo")
                .pathsToMatch("/api/tasks/**")
                .build();
    }
}
