package com.course.scheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 52 - Scheduling, Async and Events
 * ------------------------------------------------------------
 * A small "user registration" app that shows three ideas:
 *
 *  1) SCHEDULING  - jobs that run by themselves on a timer        (jobs/ShopJobs.java)
 *  2) ASYNC       - work that runs on another thread              (report/ReportService.java)
 *  3) EVENTS      - "user registered" -> listeners react          (user/UserService.java + notification/*)
 *
 * How to run:   mvn spring-boot:run
 * Then try the requests in requests.http and WATCH THE LOG - look at the thread names
 * in square brackets: [main], [nio-8080-exec-1], [sched-1], [async-1] ...
 */
@SpringBootApplication
public class SchedulingAsyncEventsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulingAsyncEventsApplication.class, args);
    }
}
