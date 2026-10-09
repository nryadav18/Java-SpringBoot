package com.course.webflux.client;

/*
 * WebClient = the reactive (non-blocking) HTTP client.
 * We build ONE WebClient bean with a base URL from application.properties
 * and reuse it everywhere (it is thread-safe).
 */
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient catalogWebClient(WebClient.Builder builder,
                                      @Value("${app.webclient.base-url}") String baseUrl) {
        // Spring Boot gives us a pre-configured WebClient.Builder (with Jackson etc.)
        return builder.baseUrl(baseUrl).build();
    }
}
