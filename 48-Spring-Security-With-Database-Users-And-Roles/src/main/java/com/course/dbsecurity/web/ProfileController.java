package com.course.dbsecurity.web;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.course.dbsecurity.security.SecurityUser;

/*
 * ProfileController - two ways to get the CURRENT user.
 *
 *   1. @AuthenticationPrincipal SecurityUser me
 *        Spring injects the UserDetails object our JpaUserDetailsService created.
 *        Cleanest in controllers - and we get our own extra fields (getId()).
 *
 *   2. SecurityContextHolder.getContext().getAuthentication()
 *        Works ANYWHERE in the same request thread (services, helpers...).
 *        The SecurityContext is stored in a ThreadLocal - one per request thread.
 */
@RestController
@RequestMapping("/api/me")
public class ProfileController {

    @GetMapping
    public Map<String, Object> me(@AuthenticationPrincipal SecurityUser me) {
        return Map.of(
                "id", me.getId(),
                "username", me.getUsername(),
                "authorities", authorityNames(me.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()));
    }

    @GetMapping("/context")
    public Map<String, Object> fromContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Map.of(
                "username", auth.getName(),
                "authenticated", auth.isAuthenticated(),
                "authorities", authorityNames(auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()),
                "authenticationType", auth.getClass().getSimpleName());
    }

    private static List<String> authorityNames(List<String> names) {
        return names.stream().sorted().toList();   // sorted -> stable, readable output
    }
}
