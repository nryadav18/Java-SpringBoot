package com.course.k8sdemo.ops;

import java.util.Map;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * ReadinessController.java - a TEACHING tool to watch Kubernetes readiness in action.
 *
 *   POST /ops/readiness/off  -> the app says "don't send me traffic" (REFUSING_TRAFFIC)
 *                               /actuator/health/readiness becomes 503 OUT_OF_SERVICE,
 *                               Kubernetes removes this pod from the Service (no restart!)
 *   POST /ops/readiness/on   -> ACCEPTING_TRAFFIC again, the pod gets traffic again
 *   GET  /ops/readiness      -> current state
 *
 * Real apps do this automatically, e.g. Spring Boot sets REFUSING_TRAFFIC during shutdown.
 * In production you would protect or remove such endpoints (Spring Security, topic 47).
 */
@RestController
@RequestMapping("/ops/readiness")
public class ReadinessController {

    private final ApplicationEventPublisher publisher;
    private final ApplicationAvailability availability;

    public ReadinessController(ApplicationEventPublisher publisher, ApplicationAvailability availability) {
        this.publisher = publisher;
        this.availability = availability;
    }

    @GetMapping
    public Map<String, Object> current() {
        return Map.of("readiness", availability.getReadinessState());
    }

    @PostMapping("/off")
    public Map<String, Object> off() {
        AvailabilityChangeEvent.publish(publisher, this, ReadinessState.REFUSING_TRAFFIC);
        return current();
    }

    @PostMapping("/on")
    public Map<String, Object> on() {
        AvailabilityChangeEvent.publish(publisher, this, ReadinessState.ACCEPTING_TRAFFIC);
        return current();
    }
}
