package com.course.shopeasy.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.course.shopeasy.user.Role;
import com.course.shopeasy.user.User;

/**
 * The logged-in user as Spring Security sees it (topic 48: UserDetails).
 *
 * Why a separate class instead of making the User ENTITY implement UserDetails?
 *   - Separation of concerns: the entity is about the database, this is about security.
 *   - It is a small immutable record - safe to keep in the SecurityContext for the whole request,
 *     with no Hibernate proxies or lazy fields inside.
 *   - Controllers can ask for it with @AuthenticationPrincipal and read principal.id() directly.
 */
public record UserPrincipal(Long id, String email, String password, Role role, boolean enabled) implements UserDetails {

    public static UserPrincipal from(User user) {
        return new UserPrincipal(user.getId(), user.getEmail(), user.getPassword(), user.getRole(), user.isEnabled());
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.authority()));   // e.g. "ROLE_CUSTOMER"
    }

    /** Spring Security calls the login name "username" - for us it is the e-mail. */
    @Override
    public String getUsername() { return email; }

    @Override
    public String getPassword() { return password; }

    @Override
    public boolean isEnabled() { return enabled; }

    /** Never print the password hash in logs: override the record's default toString(). */
    @Override
    public String toString() {
        return "UserPrincipal[id=%d, email=%s, role=%s]".formatted(id, email, role);
    }
}
