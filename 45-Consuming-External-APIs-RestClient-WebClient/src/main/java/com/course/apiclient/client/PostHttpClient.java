package com.course.apiclient.client;

import com.course.apiclient.model.NewPost;
import com.course.apiclient.model.Post;
import com.course.apiclient.model.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

/*
 * DECLARATIVE HTTP INTERFACE (Spring 6+).
 *
 * We only DESCRIBE the remote API with annotations - there is no implementation class!
 * At startup, HttpServiceProxyFactory (see HttpClientsConfig) creates a proxy object
 * that turns each method call into an HTTP request.
 *
 *   postHttpClient.getPost(1)   ->   GET {baseUrl}/posts/1   ->   JSON -> Post
 *
 * It looks a lot like a Spring MVC controller, but "the other way round":
 * a controller RECEIVES requests, this interface SENDS them.
 */
@HttpExchange   // common settings for all methods could go here, e.g. @HttpExchange("/api")
public interface PostHttpClient {

    @GetExchange("/posts/{id}")
    Post getPost(@PathVariable Long id);

    // GET /posts?userId=1   (required = false -> GET /posts when userId is null)
    @GetExchange("/posts")
    List<Post> getPosts(@RequestParam(required = false) Long userId);

    @PostExchange("/posts")
    Post createPost(@RequestBody NewPost newPost);

    @GetExchange("/users/{id}")
    User getUser(@PathVariable Long id);
}
