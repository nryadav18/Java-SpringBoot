package com.course.scheduling.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/*
 * Turns ON @Scheduled methods.
 *
 * We put @EnableScheduling on its own small class with @ConditionalOnProperty so that
 * scheduling can be switched OFF with one property:
 *
 *     app.scheduling.enabled=false
 *
 * Useful in tests (jobs firing in the middle of a test are confusing) or when you run
 * several copies of the app and only ONE copy should run the jobs.
 *
 * The thread pool for scheduled jobs is configured in application.properties
 * (spring.task.scheduling.pool.size / thread-name-prefix).
 */
@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "app.scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class SchedulingConfig {
}
