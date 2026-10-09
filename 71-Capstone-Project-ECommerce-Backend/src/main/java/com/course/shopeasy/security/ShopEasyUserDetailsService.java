package com.course.shopeasy.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.course.shopeasy.user.UserRepository;

/**
 * Tells Spring Security how to find a user by login name (topic 48).
 *
 * Used in two places:
 *   1. Login - the AuthenticationManager loads the user, then compares the BCrypt hash of the given password.
 *   2. Every request with a JWT - JwtAuthenticationFilter reloads the user, so a disabled or deleted
 *      account stops working immediately, even if its token has not expired yet.
 */
@Service
public class ShopEasyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public ShopEasyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserPrincipal loadUserByUsername(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .map(UserPrincipal::from)
                // The message is for logs only - the client always gets "Invalid email or password".
                .orElseThrow(() -> new UsernameNotFoundException("No user with email " + email));
    }
}
