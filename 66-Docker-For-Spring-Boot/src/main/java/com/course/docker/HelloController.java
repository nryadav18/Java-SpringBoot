/*
 * HelloController
 *   GET /api/hello  -> greeting + host name (inside Docker the host name is the container id!)
 *   GET /api/info   -> runtime details: Java version, user, memory, active profiles, config values
 */
package com.course.docker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelloController {

    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    private final AppProperties props;
    private final Environment environment;

    public HelloController(AppProperties props, Environment environment) {
        this.props = props;
        this.environment = environment;
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        // Logs go to the console (stdout). Docker collects them -> "docker logs <container>".
        log.info("Hello endpoint called");
        return Map.of(
                "message", props.greeting(),
                "environment", props.environmentName(),
                "host", hostName());
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        Runtime rt = Runtime.getRuntime();
        Map<String, Object> info = new LinkedHashMap<>();   // keeps insertion order in the JSON
        info.put("host", hostName());
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("javaVendor", System.getProperty("java.vendor"));
        info.put("osName", System.getProperty("os.name"));
        info.put("runningAsUser", System.getProperty("user.name"));       // should NOT be root in Docker
        info.put("availableProcessors", rt.availableProcessors());
        info.put("maxHeapMb", rt.maxMemory() / (1024 * 1024));           // affected by -XX:MaxRAMPercentage
        info.put("activeProfiles", Arrays.asList(environment.getActiveProfiles()));
        info.put("greeting", props.greeting());
        info.put("environmentName", props.environmentName());
        return info;
    }

    private String hostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }
}
