package com.course.jwtauth.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.course.jwtauth.auth.AuthDtos.AuthResponse;
import com.course.jwtauth.auth.AuthDtos.LoginRequest;
import com.course.jwtauth.auth.AuthDtos.RegisterRequest;
import com.course.jwtauth.security.JwtService;
import com.course.jwtauth.user.AppUser;
import com.course.jwtauth.user.AppUserRepository;
import com.course.jwtauth.user.Role;

/*
 * AuthService - register and login. Both end with "here is your token".
 *
 * LOGIN step by step:
 *   1. authenticationManager.authenticate(username, password)
 *        -> loads the user (AppUserDetailsService) and checks BCrypt
 *        -> wrong username/password? throws BadCredentialsException (-> 401 in ApiExceptionHandler)
 *   2. jwtService.generateToken(user) -> signed JWT
 *   3. return it. The SERVER STORES NOTHING. The token itself is the proof from now on.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, AppUserRepository userRepository,
                       UserDetailsService userDetailsService, PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameTakenException(request.username());
        }
        // Always hash, always role USER - the client never chooses its own role
        userRepository.save(new AppUser(request.username(), passwordEncoder.encode(request.password()), Role.USER));

        UserDetails user = userDetailsService.loadUserByUsername(request.username());
        return toResponse(user);   // registered = logged in (handy for the client)
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        UserDetails user = (UserDetails) authentication.getPrincipal();
        return toResponse(user);
    }

    private AuthResponse toResponse(UserDetails user) {
        String token = jwtService.generateToken(user);
        String role = user.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        return new AuthResponse(token, "Bearer", jwtService.expirationSeconds(), user.getUsername(), role);
    }
}
