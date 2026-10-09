package com.course.dbsecurity.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.course.dbsecurity.user.AppUserRepository;

/*
 * JpaUserDetailsService - tells Spring Security HOW to find a user by username.
 *
 * At login, Spring's DaoAuthenticationProvider does:
 *   1. userDetails = loadUserByUsername("alice")         <- THIS method (database lookup)
 *   2. passwordEncoder.matches(typedPassword, userDetails.getPassword())
 *   3. checks isEnabled() etc.
 *   4. success -> an Authentication with userDetails.getAuthorities() is stored
 *      in the SecurityContext for this request.
 *
 * Because this class is a @Service bean implementing UserDetailsService, Spring Boot
 * picks it up automatically - no extra wiring needed.
 */
@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;

    public JpaUserDetailsService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(SecurityUser::new)   // wrap our entity in the adapter
                // The message is NOT shown to the caller (they just get 401) - good: we don't
                // want to reveal which usernames exist.
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
