package com.course.dbsecurity.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.course.dbsecurity.note.NoteService;
import com.course.dbsecurity.note.NoteService.NoteRequest;
import com.course.dbsecurity.note.NoteService.NoteResponse;
import com.course.dbsecurity.security.SecurityUser;

/*
 * NoteController - the controller stays SIMPLE. All "who may see what" decisions
 * live in NoteService (@PreAuthorize). The controller only passes the current user along.
 *
 *   curl -u alice:alice123 -X POST http://localhost:8080/api/notes \
 *        -H "Content-Type: application/json" -d '{"title":"Groceries","content":"Milk, eggs"}'
 *   curl -u alice:alice123 http://localhost:8080/api/users/alice/notes   -> 200 (her own)
 *   curl -u alice:alice123 http://localhost:8080/api/users/admin/notes   -> 403 (not hers)
 *   curl -u admin:admin123 http://localhost:8080/api/users/alice/notes   -> 200 (admin)
 */
@RestController
@RequestMapping("/api")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping("/notes")
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse create(@AuthenticationPrincipal SecurityUser me, @RequestBody NoteRequest request) {
        return noteService.create(me.getUsername(), request);
    }

    @GetMapping("/users/{username}/notes")
    public List<NoteResponse> notesOf(@PathVariable String username) {
        return noteService.findNotesOf(username);
    }
}
