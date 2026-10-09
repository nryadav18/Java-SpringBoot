package com.course.validation.controller;

import com.course.validation.dto.RegisterUserRequest;
import com.course.validation.dto.UserResponse;
import com.course.validation.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
 * UserController - shows the TWO ways validation is triggered in Spring MVC.
 * ---------------------------------------------------------------------------
 * 1) @Valid @RequestBody RegisterUserRequest
 *      -> Spring validates the whole object after Jackson creates it.
 *      -> On failure: MethodArgumentNotValidException -> HTTP 400.
 *
 * 2) Constraints directly on simple parameters: @PathVariable @Min(1) Long id
 *      -> Since Spring Framework 6.1 (Boot 3.2+) Spring MVC has BUILT-IN
 *         "method validation": it sees the constraint annotation and checks it.
 *      -> On failure: HandlerMethodValidationException -> HTTP 400.
 *      -> No @Validated on the class is needed for this (see the lesson for
 *         the older @Validated approach and its "500 instead of 400" gotcha).
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        // If we reach this line, the request is GUARANTEED to be valid.
        UserResponse created = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable @Min(value = 1, message = "Id must be 1 or more") Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<UserResponse> search(
            @RequestParam
            @NotBlank(message = "Search text is required")
            @Size(min = 2, max = 30, message = "Search text must be {min}-{max} characters") String name) {
        return userService.searchByName(name);
    }
}
