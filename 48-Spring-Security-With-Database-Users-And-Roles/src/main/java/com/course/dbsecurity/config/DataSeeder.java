package com.course.dbsecurity.config;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.course.dbsecurity.note.Note;
import com.course.dbsecurity.note.NoteRepository;
import com.course.dbsecurity.user.AppUser;
import com.course.dbsecurity.user.AppUserRepository;
import com.course.dbsecurity.user.Role;

/*
 * DataSeeder - creates the first users when the app starts.
 *
 * Chicken-and-egg problem: only an ADMIN may manage users... but who creates the
 * first admin? Answer: the application itself, at startup, IF it does not exist yet.
 *
 * In production read the admin password from an environment variable
 * (e.g. ${ADMIN_PASSWORD}) instead of hard-coding it - here it is a demo.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final AppUserRepository users;
    private final NoteRepository notes;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(AppUserRepository users, NoteRepository notes, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.notes = notes;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!users.existsByUsername("admin")) {   // "only if missing" -> safe to run on every start
            users.save(new AppUser("admin", passwordEncoder.encode("admin123"), Set.of(Role.ADMIN, Role.USER)));
            log.info("Seeded user 'admin' (roles ADMIN, USER)");
        }
        if (!users.existsByUsername("alice")) {
            users.save(new AppUser("alice", passwordEncoder.encode("alice123"), Set.of(Role.USER)));
            notes.save(new Note("Welcome", "This is alice's first private note.", "alice"));
            log.info("Seeded user 'alice' (role USER) with one note");
        }
    }
}
