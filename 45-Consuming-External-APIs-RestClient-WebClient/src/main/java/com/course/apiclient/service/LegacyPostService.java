package com.course.apiclient.service;

import com.course.apiclient.exception.ExternalResourceNotFoundException;
import com.course.apiclient.exception.ExternalServiceException;
import com.course.apiclient.exception.ExternalServiceUnavailableException;
import com.course.apiclient.model.Post;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/*
 * 1. RestTemplate - the CLASSIC way (Spring 3, 2009).
 *
 * You will find it in MANY existing projects, so you must be able to read it.
 * It is in "maintenance mode": not deprecated, but new features go to RestClient.
 * For NEW code, prefer RestClient (see PostService).
 *
 * Style: one method per HTTP verb -> getForObject, getForEntity, postForObject,
 * exchange ... and errors arrive as exceptions (HttpClientErrorException etc.).
 */
@Service
public class LegacyPostService {

    private final RestTemplate restTemplate;

    public LegacyPostService(RestTemplate jsonPlaceholderRestTemplate) {
        this.restTemplate = jsonPlaceholderRestTemplate;
    }

    public Post getPost(Long id) {
        try {
            // GET {rootUri}/posts/{id}  ->  JSON  ->  Post
            return restTemplate.getForObject("/posts/{id}", Post.class, id);
        } catch (HttpClientErrorException.NotFound e) {           // 404
            throw new ExternalResourceNotFoundException("Post " + id + " not found (RestTemplate)");
        } catch (HttpClientErrorException | HttpServerErrorException e) {   // other 4xx / 5xx
            throw new ExternalServiceException("Remote error: " + e.getStatusCode(), e.getStatusCode().value());
        } catch (ResourceAccessException e) {                     // timeout, no connection
            throw new ExternalServiceUnavailableException("Remote API not reachable", e);
        }
    }

    public List<Post> getPostsByUser(Long userId) {
        // For generic types like List<Post> we need ParameterizedTypeReference + exchange(...)
        ResponseEntity<List<Post>> response = restTemplate.exchange(
                "/posts?userId={userId}",
                HttpMethod.GET,
                null,                                              // no request body / headers
                new ParameterizedTypeReference<List<Post>>() { },
                userId);
        return response.getBody();
    }
}
