package com.course.dbsecurity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;

import com.course.dbsecurity.note.NoteService;
import com.course.dbsecurity.user.UserService;

/*
 * NoteServiceMethodSecurityTest - method security works even WITHOUT HTTP.
 * We call the service bean directly; the @PreAuthorize proxy still checks the rules.
 */
@SpringBootTest
class NoteServiceMethodSecurityTest {

    @Autowired
    NoteService noteService;

    @Test
    @WithMockUser(username = "alice")
    void ownerIsAllowed() {
        assertThat(noteService.findNotesOf("alice")).isNotEmpty();   // seeded "Welcome" note
    }

    @Test
    @WithMockUser(username = "alice")
    void otherUserIsDenied() {
        assertThatThrownBy(() -> noteService.findNotesOf("admin"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Autowired
    UserService userService;

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void userCannotDeleteUsers() {
        // alice has ROLE_USER but not the USER_DELETE permission
        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void nobodyLoggedIn() {
        // No user at all in the SecurityContext (no @WithMockUser) -> "who are you?"
        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
    }
}
