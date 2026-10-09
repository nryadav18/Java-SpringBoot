package com.course.apiclient.service;

import com.course.apiclient.exception.ExternalServiceException;
import com.course.apiclient.exception.ExternalServiceUnavailableException;
import com.course.apiclient.model.NewPost;
import com.course.apiclient.model.Post;
import com.course.apiclient.model.User;
import com.course.apiclient.model.UserSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;

/*
 * 2. RestClient - the MODERN, RECOMMENDED way for normal (blocking) code. (Spring 6.1+)
 *
 * A "fluent" API: you chain small steps that read like a sentence:
 *
 *   restClient.get()                  // which HTTP method
 *             .uri("/posts/{id}", id) // which URL (relative to the base URL)
 *             .retrieve()             // send it and look at the response
 *             .body(Post.class);      // convert the JSON body into a Post
 *
 * It is "blocking": the current thread WAITS for the answer - exactly like
 * normal Java code, so it fits perfectly in Spring MVC apps.
 */
@Service
public class PostService {

    private static final Logger log = LoggerFactory.getLogger(PostService.class);

    private final RestClient restClient;

    public PostService(RestClient jsonPlaceholderRestClient) {
        this.restClient = jsonPlaceholderRestClient;
    }

    public Post getPost(Long id) {
        log.info("RestClient: GET /posts/{}", id);
        try {
            return restClient.get()
                    .uri("/posts/{id}", id)
                    .retrieve()
                    // 404 etc. are handled by the defaultStatusHandler in HttpClientsConfig
                    .body(Post.class);
        } catch (ResourceAccessException e) {
            // Thrown when there is NO response at all: timeout, connection refused, no internet.
            throw new ExternalServiceUnavailableException("JSONPlaceholder is not reachable: " + e.getMessage(), e);
        }
    }

    public List<Post> getPostsByUser(Long userId) {
        // uriBuilder adds ?userId=... safely (it encodes special characters for us)
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/posts").queryParam("userId", userId).build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<Post>>() { });   // List<Post>, not just List
    }

    public Post createPost(NewPost newPost) {
        ResponseEntity<Post> response = restClient.post()
                .uri("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .body(newPost)                       // Java object -> JSON request body
                .retrieve()
                // A per-call handler: overrides the default one, only for this request.
                .onStatus(HttpStatusCode::is4xxClientError, (request, response1) -> {
                    throw new ExternalServiceException("Remote API rejected our new post", response1.getStatusCode().value());
                })
                .toEntity(Post.class);               // when you also need status code / headers
        log.info("RestClient: created post, remote status {}", response.getStatusCode());
        return response.getBody();
    }

    /*
     * Used by the error-handling demos. An ABSOLUTE url ("http://...") ignores the
     * base URL, so we can call our own mock endpoints in every profile.
     * The timeouts and the defaultStatusHandler of the RestClient still apply.
     */
    public String callAbsoluteUrl(String absoluteUrl) {
        try {
            return restClient.get().uri(absoluteUrl).retrieve().body(String.class);
        } catch (ResourceAccessException e) {
            throw new ExternalServiceUnavailableException("No answer in time from " + absoluteUrl, e);
        }
    }

    public UserSummary getUserSummary(Long userId) {
        // Two calls, one after the other (sequential). Compare with ReactivePostService
        // which runs them IN PARALLEL with WebClient.
        User user = restClient.get().uri("/users/{id}", userId).retrieve().body(User.class);
        List<Post> posts = getPostsByUser(userId);
        return UserSummary.of(user, posts);
    }
}
