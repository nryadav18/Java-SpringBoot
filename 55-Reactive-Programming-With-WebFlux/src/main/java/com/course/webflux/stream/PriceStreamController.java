package com.course.webflux.stream;

/*
 * Server-Sent Events (SSE): the server keeps the HTTP connection OPEN and
 * pushes a new event whenever it has one. Perfect for live prices, scores,
 * notifications, progress bars...
 *
 * The magic is produces = TEXT_EVENT_STREAM_VALUE ("text/event-stream").
 * Try:  curl -N http://localhost:8080/api/stream/prices
 *       (-N = do not buffer, print each event as it arrives)
 */
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/stream")
public class PriceStreamController {

    @GetMapping(value = "/prices", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<PriceTick> prices() {
        return Flux.interval(Duration.ofSeconds(1))      // 0, 1, 2, ... one per second
                .map(seq -> new PriceTick(seq, "Laptop", randomPrice(), LocalTime.now().withNano(0)))
                .take(10);                                // stop after 10 events
    }

    private BigDecimal randomPrice() {
        double value = 880 + ThreadLocalRandom.current().nextDouble(40);
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}
