package com.course.shopeasy.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.course.shopeasy.common.exception.ResourceNotFoundException;
import com.course.shopeasy.security.UserPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * "Who am I?" endpoint. Useful for a front-end right after login to show "Hello, Asha".
 *
 * @AuthenticationPrincipal injects the logged-in user that our JwtAuthenticationFilter put into the
 * SecurityContext (topic 48). The id comes from the verified token - a client can NOT fake it.
 * User has no lazy associations, so a plain repository call (itself transactional) is enough here;
 * features with real business logic (cart, order) go through a @Service.
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "The currently logged-in user")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    @Operation(summary = "Get the profile of the logged-in user")
    public UserResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        return userRepository.findById(principal.id())
                .map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("User", principal.id()));
    }
}
