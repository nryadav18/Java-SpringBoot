package com.course.validation.service;

import com.course.validation.dto.RegisterUserRequest;
import com.course.validation.dto.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/*
 * UserService - very small in-memory service (layers from topic 33).
 *
 * IMPORTANT idea: by the time a request reaches this service, the controller
 * has ALREADY validated the FORMAT of the input (@Valid). So the service can
 * focus on BUSINESS rules that need data, e.g. "email already registered".
 *   - Format rules   (not blank, valid email, min age)  -> Bean Validation on the DTO
 *   - Business rules (email unique, enough stock)       -> service code
 */
@Service
public class UserService {

    private final Map<Long, UserResponse> users = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong();

    public UserResponse register(RegisterUserRequest request) {
        // A real app would hash the password (topic 48) and check for duplicate emails.
        long id = ids.incrementAndGet();
        UserResponse user = new UserResponse(id, request.name(), request.email(),
                request.address().city());
        users.put(id, user);
        return user;
    }

    public Optional<UserResponse> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<UserResponse> searchByName(String namePart) {
        return users.values().stream()
                .filter(u -> u.name().toLowerCase().contains(namePart.toLowerCase()))
                .toList();
    }
}
