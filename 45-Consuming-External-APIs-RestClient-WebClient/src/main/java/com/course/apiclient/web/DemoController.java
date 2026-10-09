package com.course.apiclient.web;

import com.course.apiclient.client.PostHttpClient;
import com.course.apiclient.model.NewPost;
import com.course.apiclient.model.Post;
import com.course.apiclient.model.UserSummary;
import com.course.apiclient.service.LegacyPostService;
import com.course.apiclient.service.PostService;
import com.course.apiclient.service.ReactivePostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

/*
 * OUR API. Every endpoint here calls the EXTERNAL API behind the scenes.
 *
 *   Browser/curl  -->  DemoController  -->  a client  -->  JSONPlaceholder (or the mock)
 *
 * The URL tells you which client is used, so you can compare them.
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final LegacyPostService legacyPostService;     // RestTemplate
    private final PostService postService;                 // RestClient
    private final ReactivePostService reactivePostService; // WebClient
    private final PostHttpClient postHttpClient;           // @HttpExchange interface

    public DemoController(LegacyPostService legacyPostService, PostService postService,
                          ReactivePostService reactivePostService, PostHttpClient postHttpClient) {
        this.legacyPostService = legacyPostService;
        this.postService = postService;
        this.reactivePostService = reactivePostService;
        this.postHttpClient = postHttpClient;
    }

    // ---------------------------------------------------------------- 1. RestTemplate
    @GetMapping("/rest-template/posts/{id}")
    public Post restTemplatePost(@PathVariable Long id) {
        return legacyPostService.getPost(id);
    }

    @GetMapping("/rest-template/posts")
    public List<Post> restTemplatePostsByUser(@RequestParam Long userId) {
        return legacyPostService.getPostsByUser(userId);
    }

    // ---------------------------------------------------------------- 2. RestClient
    @GetMapping("/rest-client/posts/{id}")
    public Post restClientPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    @GetMapping("/rest-client/posts")
    public List<Post> restClientPostsByUser(@RequestParam Long userId) {
        return postService.getPostsByUser(userId);
    }

    @PostMapping("/rest-client/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public Post restClientCreate(@Valid @RequestBody NewPost newPost) {
        return postService.createPost(newPost);
    }

    @GetMapping("/rest-client/users/{id}/summary")
    public UserSummary restClientUserSummary(@PathVariable Long id) {
        return postService.getUserSummary(id);
    }

    // ---------------------------------------------------------------- 3. WebClient
    // Spring MVC accepts a Mono as return value: it subscribes and writes the
    // result when it arrives (without blocking a Tomcat thread while waiting).
    @GetMapping("/web-client/posts/{id}")
    public Mono<Post> webClientPost(@PathVariable Long id) {
        return reactivePostService.getPost(id);
    }

    @GetMapping("/web-client/users/{id}/summary")
    public Mono<UserSummary> webClientUserSummary(@PathVariable Long id) {
        return reactivePostService.getUserSummary(id);
    }

    // ---------------------------------------------------------------- 4. HTTP interface
    @GetMapping("/http-interface/posts/{id}")
    public Post httpInterfacePost(@PathVariable Long id) {
        return postHttpClient.getPost(id);       // looks like a normal method call!
    }

    @GetMapping("/http-interface/posts")
    public List<Post> httpInterfacePosts(@RequestParam(required = false) Long userId) {
        return postHttpClient.getPosts(userId);
    }

    // ---------------------------------------------------------------- error-handling demos
    // These always call OUR OWN mock endpoints (they exist in every profile).

    /** The mock answers after 10 s, our read timeout is 5 s  ->  503 Service Unavailable. */
    @GetMapping("/errors/timeout")
    public String timeoutDemo() {
        return postService.callAbsoluteUrl(localUrl("/mock-api/slow"));
    }

    /** The mock answers 500  ->  we answer 502 Bad Gateway. */
    @GetMapping("/errors/remote-500")
    public String remoteErrorDemo() {
        return postService.callAbsoluteUrl(localUrl("/mock-api/broken"));
    }

    // http://localhost:<our port><path>
    private static String localUrl(String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(path).toUriString();
    }
}
