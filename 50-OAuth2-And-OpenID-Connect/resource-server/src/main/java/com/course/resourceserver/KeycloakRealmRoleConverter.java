package com.course.resourceserver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

/*
 * KeycloakRealmRoleConverter - turns a JWT into Spring Security authorities.
 *
 * Spring's DEFAULT only reads the "scope" claim:
 *     "scope": "openid profile email"   ->  SCOPE_openid, SCOPE_profile, SCOPE_email
 *
 * Keycloak puts the user's ROLES somewhere else, which Spring does not know about:
 *     "realm_access": { "roles": ["user", "admin"] }
 *
 * This converter keeps the scopes AND adds the roles:
 *     ROLE_USER, ROLE_ADMIN       -> so hasRole("ADMIN") works
 *
 * Other providers use other claim names (Auth0: a custom namespace claim, Azure AD: "roles").
 * Only this small class would change.
 */
public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>(scopesConverter.convert(jwt));

        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> roles) {
            for (Object role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase()));
            }
        }
        return authorities;
    }
}
