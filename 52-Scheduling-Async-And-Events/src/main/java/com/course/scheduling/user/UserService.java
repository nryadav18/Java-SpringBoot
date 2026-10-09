package com.course.scheduling.user;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * The PUBLISHER side of events.
 *
 * Registering a user should also: send a welcome email, write an audit log, give a coupon ...
 * Instead of calling EmailService, AuditService, CouponService directly (tight coupling!),
 * this service just ANNOUNCES "a user registered" and does not care who listens.
 * New reactions can be added later as new listener classes - this class never changes.
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final AppUserRepository repository;
    private final ApplicationEventPublisher eventPublisher;   // provided by Spring - just inject it

    public UserService(AppUserRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public AppUser register(String name, String email) {
        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        AppUser saved = repository.save(new AppUser(name, email));
        log.info("Saved user {} - publishing UserRegisteredEvent", saved.getId());

        // Publish INSIDE the transaction. Plain @EventListeners run right now (same thread, same transaction).
        // @TransactionalEventListeners wait until the transaction has COMMITTED.
        eventPublisher.publishEvent(
                new UserRegisteredEvent(saved.getId(), saved.getName(), saved.getEmail(), saved.getRegisteredAt()));

        // DEMO: an email containing "fail" makes the transaction roll back AFTER the event was published.
        // Watch the log: the audit listener already ran, but the welcome email is NEVER sent.
        if (email.contains("fail")) {
            throw new IllegalStateException("Simulated failure after publishing the event - rolling back");
        }

        log.info("register() finished - returning the response now (email is sent in the background)");
        return saved;
    }

    @Transactional(readOnly = true)
    public List<AppUser> findAll() {
        return repository.findAll();
    }
}
