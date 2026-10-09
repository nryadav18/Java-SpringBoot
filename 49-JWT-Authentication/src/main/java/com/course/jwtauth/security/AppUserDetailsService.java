package com.course.jwtauth.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.course.jwtauth.user.AppUserRepository;

/*
 * AppUserDetailsService - loads a user from the database by username.
 *
 * Used in TWO places:
 *   1. At LOGIN: AuthenticationManager -> DaoAuthenticationProvider -> loadUserByUsername + BCrypt check
 *   2. On EVERY request with a token: JwtAuthenticationFilter loads the user named in the token
 *      (so a deleted user's old token stops working immediately).
 *
 * This time we use Spring's ready-made User builder instead of our own adapter class
 * (topic 48 showed the adapter way - both are fine).
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;

    public AppUserDetailsService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(u -> User.withUsername(u.getUsername())
                        .password(u.getPassword())
                        .roles(u.getRole().name())        // ADMIN -> "ROLE_ADMIN"
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
