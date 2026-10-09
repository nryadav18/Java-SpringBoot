package com.course.apiclient.mock;

import com.course.apiclient.model.NewPost;
import com.course.apiclient.model.Post;
import com.course.apiclient.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * A tiny FAKE copy of JSONPlaceholder that lives inside our own app,
 * under http://localhost:8080/mock-api/...
 *
 * Why? So the lesson works OFFLINE (no internet, company firewall, API down ...).
 * Start the app with the "offline" profile and every client calls this mock
 * instead of the real https://jsonplaceholder.typicode.com.
 *
 * It also has two "bad" endpoints to practise error handling:
 *   /mock-api/slow    -> answers after 10 seconds (to trigger our 5 s timeout)
 *   /mock-api/broken  -> always answers 500
 *
 * In a real project you would NOT put a mock in production code; you would use
 * WireMock or MockRestServiceServer in TESTS (see the lesson / topic 46).
 */
@RestController
@RequestMapping("/mock-api")
public class MockJsonPlaceholderController {

    private static final Map<Long, User> USERS = Map.of(
            1L, new User(1L, "Leanne Graham", "Bret", "Sincere@april.biz"),
            2L, new User(2L, "Ervin Howell", "Antonette", "Shanna@melissa.tv"),
            3L, new User(3L, "Clementine Bauch", "Samantha", "Nathan@yesenia.net"));

    private static final List<Post> POSTS = new ArrayList<>();

    static {
        long id = 1;
        for (long userId = 1; userId <= 3; userId++) {
            for (int i = 1; i <= 3; i++) {
                POSTS.add(new Post(userId, id, "Mock post " + id + " by user " + userId,
                        "This is the body of mock post " + id + "."));
                id++;
            }
        }
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> post(@PathVariable Long id) {
        return POSTS.stream().filter(p -> p.id().equals(id)).findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());   // 404 with an empty body, like the real API
    }

    @GetMapping("/posts")
    public List<Post> posts(@RequestParam(required = false) Long userId) {
        return POSTS.stream().filter(p -> userId == null || p.userId().equals(userId)).toList();
    }

    @PostMapping("/posts")
    public ResponseEntity<Post> create(@RequestBody NewPost newPost) {
        // The real JSONPlaceholder also returns id 101 and does NOT really save anything.
        Post created = new Post(newPost.userId(), 101L, newPost.title(), newPost.body());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> user(@PathVariable Long id) {
        User user = USERS.get(id);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

    @GetMapping("/slow")
    public String slow() throws InterruptedException {
        Thread.sleep(10_000);   // 10 seconds - longer than our 5 s read timeout
        return "finally!";
    }

    @GetMapping("/broken")
    public ResponseEntity<String> broken() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\":\"database exploded\"}");
    }
}
