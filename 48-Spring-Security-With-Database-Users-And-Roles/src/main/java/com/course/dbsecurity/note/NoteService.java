package com.course.dbsecurity.note;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * NoteService - shows METHOD SECURITY with SpEL that looks at the method's own arguments.
 *
 * URL rules can only say "any logged-in user may call GET /api/users/{username}/notes".
 * They cannot say "...but only for HIS OWN username". That needs the DATA -> @PreAuthorize.
 */
@Service
public class NoteService {

    public record NoteRequest(String title, String content) { }

    public record NoteResponse(Long id, String title, String content, String owner) {
        static NoteResponse from(Note n) {
            return new NoteResponse(n.getId(), n.getTitle(), n.getContent(), n.getOwnerUsername());
        }
    }

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    /*
     * The owner comes from the LOGGED-IN user (passed by the controller), never from the
     * request body - otherwise alice could create notes "owned" by bob.
     * hasAuthority('NOTE_WRITE') - every USER has this permission (see Role enum).
     */
    @PreAuthorize("hasAuthority('NOTE_WRITE')")
    @Transactional
    public NoteResponse create(String ownerUsername, NoteRequest request) {
        Note saved = noteRepository.save(new Note(request.title(), request.content(), ownerUsername));
        return NoteResponse.from(saved);
    }

    /*
     * SpEL (Spring Expression Language) inside @PreAuthorize:
     *   #username            -> the value of the method parameter called "username"
     *   authentication.name  -> the username of whoever is logged in right now
     *   hasRole('ADMIN')     -> admins may read everybody's notes
     *
     * alice asks for /api/users/alice/notes -> "alice" == "alice" -> allowed
     * alice asks for /api/users/admin/notes -> not equal, not admin -> 403 Forbidden
     */
    @PreAuthorize("#username == authentication.name or hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<NoteResponse> findNotesOf(String username) {
        return noteRepository.findByOwnerUsernameOrderByIdAsc(username).stream()
                .map(NoteResponse::from)
                .toList();
    }
}
