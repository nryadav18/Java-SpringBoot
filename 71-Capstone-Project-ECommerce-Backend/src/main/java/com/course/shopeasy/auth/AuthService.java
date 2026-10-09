package com.course.shopeasy.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.course.shopeasy.common.exception.DuplicateResourceException;
import com.course.shopeasy.security.JwtService;
import com.course.shopeasy.security.UserPrincipal;
import com.course.shopeasy.user.Role;
import com.course.shopeasy.user.User;
import com.course.shopeasy.user.UserRepository;
import com.course.shopeasy.user.UserResponse;

/**
 * Registration and login (topics 48-49).
 *
 *   register: validate unique e-mail -> hash password with BCrypt -> save as CUSTOMER -> return a token
 *   login:    AuthenticationManager checks e-mail + password -> return a token
 *
 * A wrong password makes authenticate(...) throw BadCredentialsException, which GlobalExceptionHandler
 * turns into 401 "Invalid email or password".
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("An account with email " + email + " already exists");
        }
        User user = new User(request.fullName().trim(), email, passwordEncoder.encode(request.password()), Role.CUSTOMER);
        User saved = userRepository.save(user);
        log.info("New customer registered: id={}", saved.getId());
        return tokenFor(UserPrincipal.from(saved), UserResponse.from(saved));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizeEmail(request.email()), request.password()));

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(principal.id()).orElseThrow();
        log.debug("User {} logged in", principal.id());
        return tokenFor(principal, UserResponse.from(user));
    }

    private AuthResponse tokenFor(UserPrincipal principal, UserResponse user) {
        String token = jwtService.generateToken(principal);
        return AuthResponse.bearer(token, jwtService.getExpiration().toSeconds(), user);
    }

    /** "  Asha@Example.COM " and "asha@example.com" must be the same account. */
    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
