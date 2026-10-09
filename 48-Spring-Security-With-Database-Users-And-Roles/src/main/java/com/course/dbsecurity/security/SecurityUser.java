package com.course.dbsecurity.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.course.dbsecurity.user.AppUser;
import com.course.dbsecurity.user.Role;

/*
 * SecurityUser - an ADAPTER between our AppUser entity and Spring Security.
 *
 * Spring Security does not know our AppUser class. It only understands the
 * UserDetails interface: "give me a username, a password hash, a list of authorities,
 * and tell me whether the account is usable".
 *
 * So we wrap AppUser in this small class. (Adapter pattern - see topic 70.)
 *
 * Why not make AppUser implement UserDetails directly? You can (many tutorials do),
 * but keeping them separate keeps the JPA entity clean and free of security code.
 */
public class SecurityUser implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final boolean enabled;
    private final Set<GrantedAuthority> authorities;

    public SecurityUser(AppUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = user.isEnabled();
        this.authorities = toAuthorities(user.getRoles());
    }

    /*
     * Role ADMIN  ->  "ROLE_ADMIN" + its permissions "REPORT_READ", "USER_DELETE"
     * This is what hasRole(...) and hasAuthority(...) will look at later.
     */
    private static Set<GrantedAuthority> toAuthorities(Set<Role> roles) {
        Set<GrantedAuthority> result = new HashSet<>();
        for (Role role : roles) {
            result.add(new SimpleGrantedAuthority(role.asAuthority()));
            for (String permission : role.getPermissions()) {
                result.add(new SimpleGrantedAuthority(permission));
            }
        }
        return result;
    }

    /** Extra field that the plain UserDetails interface does not have - handy in controllers. */
    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;          // the BCrypt hash - Spring compares it with PasswordEncoder.matches(..)
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return enabled;           // false -> login fails with "User is disabled"
    }

    // isAccountNonExpired(), isAccountNonLocked(), isCredentialsNonExpired()
    // have default implementations returning true in Spring Security 6.3+, so we skip them.
}
