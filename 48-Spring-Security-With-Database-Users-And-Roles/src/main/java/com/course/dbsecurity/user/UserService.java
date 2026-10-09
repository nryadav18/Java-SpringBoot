package com.course.dbsecurity.user;

import java.util.List;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.course.dbsecurity.user.UserDtos.RegisterRequest;
import com.course.dbsecurity.user.UserDtos.UserResponse;

/*
 * UserService - registration and user administration.
 *
 * Key security ideas in this class:
 *   - register(): the password is HASHED with BCrypt BEFORE it touches the database.
 *   - register(): new users ALWAYS get role USER. Never let the client choose its own role!
 *   - deleteUser(): protected by @PreAuthorize - even if some controller forgets a URL rule,
 *     the service itself refuses non-admins. (Defence in depth.)
 */
@Service
public class UserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameTakenException(request.username());
        }
        String hash = passwordEncoder.encode(request.password());   // "alice123" -> "$2a$10$..."
        AppUser saved = userRepository.save(new AppUser(request.username(), hash, Set.of(Role.USER)));
        return UserResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    /*
     * Method security: checked by a Spring AOP proxy (topic 27) BEFORE the method runs.
     * hasAuthority('USER_DELETE') - only ADMIN has this permission (see Role enum).
     * Not allowed -> AccessDeniedException -> HTTP 403.
     */
    @PreAuthorize("hasAuthority('USER_DELETE')")
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + id + " not found");
        }
        userRepository.deleteById(id);
    }
}
