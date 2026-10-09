/*
 * InventoryClient - the plain HTTP call to inventory-service (RestClient, topic 45).
 * ---------------------------------------------------------------
 * Notice: NO resilience code here. This class only knows how to make the call.
 * The protection (circuit breaker, retry...) is added one level up in StockService
 * with annotations. Keeping them apart makes both easier to read and test.
 *
 * Possible failures and the exception RestClient throws:
 *   inventory-service not running      -> ResourceAccessException (connection refused)
 *   too slow (> read timeout)          -> ResourceAccessException (read timed out)
 *   answers 500                        -> HttpServerErrorException
 *   answers 404 / 400                  -> HttpClientErrorException  (OUR mistake - do not retry!)
 */
package com.course.resilience.store.client;

import com.course.resilience.store.model.StockInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Component
public class InventoryClient {

    private static final Logger log = LoggerFactory.getLogger(InventoryClient.class);

    private final RestClient restClient;

    public InventoryClient(RestClient.Builder builder,
                           @Value("${inventory.base-url}") String baseUrl) {
        // ALWAYS set timeouts on HTTP clients. Without a read timeout a hanging
        // downstream can block our thread forever.
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(1));
        factory.setReadTimeout(Duration.ofSeconds(10)); // generous on purpose: TimeLimiter demo cuts at 2 s

        this.restClient = builder
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    public StockInfo fetchStock(String sku) {
        log.info("--> HTTP GET inventory-service /api/inventory/{}", sku);
        StockInfo info = restClient.get()
                .uri("/api/inventory/{sku}", sku)
                .retrieve()
                .body(StockInfo.class);
        log.info("<-- inventory-service answered {}", info);
        return info;
    }
}
