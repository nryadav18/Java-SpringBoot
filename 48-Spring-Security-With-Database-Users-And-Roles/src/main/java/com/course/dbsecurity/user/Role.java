package com.course.dbsecurity.user;

import java.util.Set;

/*
 * Role - WHAT KIND of user someone is.
 *
 * Each role also carries a few fine-grained PERMISSIONS (authorities).
 * Spring Security turns this into a list of GrantedAuthority strings:
 *
 *   USER  -> "ROLE_USER",                    "NOTE_WRITE"
 *   ADMIN -> "ROLE_ADMIN", "REPORT_READ",    "USER_DELETE"
 *
 * hasRole("ADMIN")            checks for the authority "ROLE_ADMIN" (prefix added for you)
 * hasAuthority("REPORT_READ") checks for the exact string "REPORT_READ"
 */
public enum Role {

    USER(Set.of("NOTE_WRITE")),
    ADMIN(Set.of("REPORT_READ", "USER_DELETE"));

    private final Set<String> permissions;

    Role(Set<String> permissions) {
        this.permissions = permissions;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    /** The authority string that hasRole(...) looks for, e.g. "ROLE_ADMIN". */
    public String asAuthority() {
        return "ROLE_" + name();
    }
}
