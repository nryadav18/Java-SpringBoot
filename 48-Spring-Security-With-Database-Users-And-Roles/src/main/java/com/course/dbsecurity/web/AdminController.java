package com.course.dbsecurity.web;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.course.dbsecurity.user.UserDtos.UserResponse;
import com.course.dbsecurity.user.UserService;

/*
 * AdminController
 *   /api/admin/**    -> URL rule hasRole("ADMIN") in SecurityConfig
 *   deleteUser(...)  -> ALSO @PreAuthorize("hasAuthority('USER_DELETE')") on UserService
 *
 *   /api/reports/**  -> URL rule hasAuthority("REPORT_READ") (a permission, not a role)
 */
@RestController
@RequestMapping("/api")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/users")
    public List<UserResponse> allUsers() {
        return userService.findAll();
    }

    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();   // 204
    }

    @GetMapping("/reports/summary")
    public Map<String, Object> summary() {
        List<UserResponse> users = userService.findAll();
        return Map.of("totalUsers", users.size());
    }
}
