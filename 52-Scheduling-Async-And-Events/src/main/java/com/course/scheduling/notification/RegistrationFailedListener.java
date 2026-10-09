package com.course.scheduling.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.course.scheduling.user.UserRegisteredEvent;

/*
 * LISTENER #3 - react when the registration transaction was ROLLED BACK.
 *
 * Phases of @TransactionalEventListener:
 *   BEFORE_COMMIT       - just before commit (still inside the transaction)
 *   AFTER_COMMIT        - default; only if the commit succeeded
 *   AFTER_ROLLBACK      - only if the transaction rolled back
 *   AFTER_COMPLETION    - after commit OR rollback
 *
 * Try it: register with an email that contains "fail" (see requests.http).
 */
@Component
public class RegistrationFailedListener {

    private static final Logger log = LoggerFactory.getLogger(RegistrationFailedListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onRollback(UserRegisteredEvent event) {
        log.warn("Registration of {} was ROLLED BACK - no welcome email will be sent", event.email());
    }
}
