package com.course.apiclient.service;

import com.course.apiclient.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/*
 * How to TEST code that calls an external API - without calling it!
 *
 * MockRestServiceServer plugs into the RestClient and answers requests with
 * the fake responses we prepare. No network, fast and repeatable.
 * (Testing is covered in depth in topic 46.)
 *
 * Run: mvn test
 */
class PostServiceTest {

    private MockRestServiceServer server;
    private PostService postService;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://fake-api.test");
        server = MockRestServiceServer.bindTo(builder).build();   // must be bound BEFORE build()
        postService = new PostService(builder.build());
    }

    @Test
    void getPostConvertsJsonIntoPostRecord() {
        server.expect(requestTo("https://fake-api.test/posts/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"userId": 1, "id": 1, "title": "Hello", "body": "World", "extra": "ignored"}
                        """, MediaType.APPLICATION_JSON));

        Post post = postService.getPost(1L);

        assertThat(post.title()).isEqualTo("Hello");
        assertThat(post.userId()).isEqualTo(1L);
        server.verify();   // every expected request really happened
    }

    @Test
    void getPostsByUserAddsQueryParameter() {
        server.expect(requestTo("https://fake-api.test/posts?userId=2"))
                .andRespond(withSuccess("""
                        [{"userId": 2, "id": 4, "title": "A", "body": "a"},
                         {"userId": 2, "id": 5, "title": "B", "body": "b"}]
                        """, MediaType.APPLICATION_JSON));

        List<Post> posts = postService.getPostsByUser(2L);

        assertThat(posts).extracting(Post::id).containsExactly(4L, 5L);
        server.verify();
    }
}
