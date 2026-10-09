package com.course.apiclient.service;

import com.course.apiclient.config.ExternalApiProperties;
import com.course.apiclient.exception.ExternalResourceNotFoundException;
import com.course.apiclient.exception.ExternalServiceException;
import com.course.apiclient.exception.ExternalServiceUnavailableException;
import com.course.apiclient.model.Post;
import com.course.apiclient.model.User;
import com.course.apiclient.model.UserSummary;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

/*
 * 3. WebClient - the REACTIVE (non-blocking) client. (A preview - topic 55 goes deep.)
 *
 * Big difference: methods return Mono<T> ("a box that will contain ONE value LATER")
 * or Flux<T> ("a stream of MANY values later") instead of the value itself.
 * Nothing is sent until someone SUBSCRIBES to the Mono. Spring MVC subscribes
 * for us when a controller method returns a Mono.
 *
 * Why use it? To do many calls at the same time without blocking threads,
 * e.g. Mono.zip(...) below runs two calls IN PARALLEL.
 */
@Service
public class ReactivePostService {

    private final WebClient webClient;
    private final Duration timeout;

    public ReactivePostService(WebClient jsonPlaceholderWebClient, ExternalApiProperties props) {
        this.webClient = jsonPlaceholderWebClient;
        this.timeout = props.readTimeout();
    }

    public Mono<Post> getPost(Long id) {
        return webClient.get()
                .uri("/posts/{id}", id)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        response -> Mono.error(new ExternalResourceNotFoundException("Post " + id + " not found (WebClient)")))
                .onStatus(HttpStatusCode::isError,
                        response -> Mono.error(new ExternalServiceException("Remote error", response.statusCode().value())))
                .bodyToMono(Post.class)
                .timeout(timeout)                        // give up after the read timeout
                .onErrorMap(this::isConnectionProblem,   // translate low-level errors to OUR exception
                        e -> new ExternalServiceUnavailableException("JSONPlaceholder is not reachable", e));
    }

    public Mono<UserSummary> getUserSummary(Long userId) {
        Mono<User> userMono = webClient.get().uri("/users/{id}", userId)
                .retrieve().bodyToMono(User.class);

        Mono<List<Post>> postsMono = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/posts").queryParam("userId", userId).build())
                .retrieve().bodyToFlux(Post.class)       // Flux<Post> = many posts
                .collectList();                          // -> Mono<List<Post>>

        // zip = wait for BOTH (running at the same time), then combine the results
        return Mono.zip(userMono, postsMono)
                .map(tuple -> UserSummary.of(tuple.getT1(), tuple.getT2()))
                .timeout(timeout)
                .onErrorMap(this::isConnectionProblem,
                        e -> new ExternalServiceUnavailableException("JSONPlaceholder is not reachable", e));
    }

    private boolean isConnectionProblem(Throwable e) {
        return e instanceof TimeoutException || e instanceof WebClientRequestException;
    }
}
