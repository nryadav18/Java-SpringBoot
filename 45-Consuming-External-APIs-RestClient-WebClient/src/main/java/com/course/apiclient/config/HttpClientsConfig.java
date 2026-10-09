package com.course.apiclient.config;

import com.course.apiclient.client.PostHttpClient;
import com.course.apiclient.exception.ExternalResourceNotFoundException;
import com.course.apiclient.exception.ExternalServiceException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.http.HttpClient;

/*
 * ONE place where all HTTP clients are configured:
 *   - base URL from properties (never hard-coded in services)
 *   - timeouts (ALWAYS set them - see the lesson)
 *   - default headers
 *   - default error handling
 *
 * Spring Boot auto-configures RestTemplateBuilder, RestClient.Builder and
 * WebClient.Builder (already set up with Jackson etc.). We take those builders
 * and add our own settings, then expose ready-to-use client beans.
 */
@Configuration
public class HttpClientsConfig {

    /*
     * The low-level "engine" that really opens TCP connections.
     * We use the HTTP client built into the JDK (java.net.http.HttpClient, Java 11+).
     *   connect timeout = max time to OPEN the connection
     *   read timeout    = max time to WAIT for the response
     */
    @Bean
    public ClientHttpRequestFactory externalApiRequestFactory(ExternalApiProperties props) {
        HttpClient jdkHttpClient = HttpClient.newBuilder()
                .connectTimeout(props.connectTimeout())
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(jdkHttpClient);
        factory.setReadTimeout(props.readTimeout());
        return factory;
    }

    // ---------------------------------------------------------------- 1. RestTemplate (legacy)
    @Bean
    public RestTemplate jsonPlaceholderRestTemplate(RestTemplateBuilder builder,
                                                    ExternalApiProperties props) {
        return builder
                .rootUri(props.baseUrl())             // "/posts/1" becomes {baseUrl}/posts/1
                .connectTimeout(props.connectTimeout())
                .readTimeout(props.readTimeout())
                .build();
    }

    // ---------------------------------------------------------------- 2. RestClient (recommended)
    @Bean
    public RestClient jsonPlaceholderRestClient(RestClient.Builder builder,
                                                ExternalApiProperties props,
                                                ClientHttpRequestFactory externalApiRequestFactory) {
        return builder
                .baseUrl(props.baseUrl())
                .requestFactory(externalApiRequestFactory)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "course-api-client/1.0")
                // Default error handling for EVERY call made with this client.
                // (A single call can still override it with .onStatus(...).)
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    if (response.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
                        throw new ExternalResourceNotFoundException(
                                "Remote resource not found: " + request.getURI());
                    }
                    throw new ExternalServiceException(
                            "Remote API returned " + response.getStatusCode().value() + " for " + request.getURI(),
                            response.getStatusCode().value());
                })
                .build();
    }

    // ---------------------------------------------------------------- 3. WebClient (reactive)
    @Bean
    public WebClient jsonPlaceholderWebClient(WebClient.Builder builder, ExternalApiProperties props) {
        // Timeouts for WebClient are added per call with .timeout(...) in ReactivePostService.
        // (They can also be set on the underlying Reactor Netty HttpClient.)
        return builder
                .baseUrl(props.baseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    // ---------------------------------------------------------------- 4. HTTP interface proxy
    @Bean
    public PostHttpClient postHttpClient(RestClient jsonPlaceholderRestClient) {
        // Re-use the RestClient above: same base URL, timeouts and error handling.
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(jsonPlaceholderRestClient))
                .build();
        return factory.createClient(PostHttpClient.class);
    }
}
