package com.course.scheduling.user;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * POST /api/users  -> registers a user and returns IMMEDIATELY (about 20 ms),
 *                     while the welcome email (3 s) is "sent" on a background thread.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    public record RegisterRequest(String name, String email) { }

    public record UserResponse(Long id, String name, String email) { }

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        AppUser user = userService.register(request.name(), request.email());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserResponse(user.getId(), user.getName(), user.getEmail()));
    }

    @GetMapping
    public List<UserResponse> all() {
        return userService.findAll().stream()
                .map(u -> new UserResponse(u.getId(), u.getName(), u.getEmail()))
                .toList();
    }

    /** Small local error handler so the demo returns a readable message (topic 35 shows the global way). */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handle(RuntimeException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
