package com.course.scheduling.jobs;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.course.scheduling.user.AppUserRepository;

/*
 * Background jobs that run BY THEMSELVES - nobody calls these methods.
 * Spring's scheduler calls them on a timer (needs @EnableScheduling, see SchedulingConfig).
 *
 * Rules for a @Scheduled method:
 *   - it returns void
 *   - it has no parameters
 *   - it lives in a Spring bean (@Component, @Service ...)
 *
 * The three styles:
 *   fixedRate  -> start every N ms, measured from the START of the previous run ("every 20 s on the clock")
 *   fixedDelay -> wait N ms after the previous run FINISHED ("rest 30 s between runs")
 *   cron       -> calendar based ("every day at 02:00", "Mon-Fri at 9:30")
 *
 * The values come from application.properties (${...}) so they can be changed without recompiling.
 */
@Component
public class ShopJobs {

    private static final Logger log = LoggerFactory.getLogger(ShopJobs.class);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final AppUserRepository userRepository;

    public ShopJobs(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * fixedRate: runs every 20 seconds (first run 5 seconds after startup).
     * Typical use: heartbeat, polling something, refreshing a small in-memory value.
     */
    @Scheduled(fixedRateString = "${app.jobs.heartbeat-rate-ms}", initialDelayString = "${app.jobs.initial-delay-ms}")
    public void heartbeat() {
        log.info("[fixedRate ] Heartbeat at {} - {} registered users", LocalTime.now().format(TIME), userRepository.count());
    }

    /**
     * fixedDelay: the job itself takes ~2 seconds; the next run starts 30 seconds AFTER it finished.
     * So the runs never overlap - good for jobs whose duration varies (e.g. sending a batch of emails).
     */
    @Scheduled(fixedDelayString = "${app.jobs.retry-delay-ms}", initialDelayString = "${app.jobs.initial-delay-ms}")
    public void retryFailedEmails() throws InterruptedException {
        log.info("[fixedDelay] Retrying failed emails... started  {}", LocalTime.now().format(TIME));
        Thread.sleep(2000);   // pretend to do some work
        log.info("[fixedDelay] Retrying failed emails... finished {}", LocalTime.now().format(TIME));
    }

    /**
     * cron: "second minute hour day-of-month month day-of-week".
     * Default in application.properties: "0 * * * * *" = at second 0 of every minute (so you can see it while learning).
     * A real nightly report would be "0 0 2 * * *" = every day at 02:00:00.
     * zone: which time zone the cron expression is read in (defaults to the server's zone).
     */
    @Scheduled(cron = "${app.jobs.daily-report-cron}", zone = "${app.jobs.zone}")
    public void dailyReport() {
        log.info("[cron      ] Daily report: {} users in total", userRepository.count());
    }
}
