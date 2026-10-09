/*
 * ProductClientErrorDecoder - translates HTTP error answers into meaningful Java exceptions.
 * ---------------------------------------------------------------
 * Without it, Feign throws a generic feign.FeignException for every 4xx/5xx and the
 * caller would have to dig into status codes. With it, OrderService simply lets
 * ProductNotFoundException, OutOfStockException... bubble up to GlobalExceptionHandler.
 *
 * decode() is called by Feign ONLY for non-2xx responses.
 * (Connection refused / read timeout never reach the decoder: there is no response at all.
 *  Feign throws feign.RetryableException for those - see GlobalExceptionHandler.)
 */
package com.course.feign.order.exception;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class ProductClientErrorDecoder implements ErrorDecoder {

    private static final Logger log = LoggerFactory.getLogger(ProductClientErrorDecoder.class);

    // Feign's built-in decoder - we fall back to it for status codes we do not handle.
    private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String body = readBody(response);
        log.warn("product-service answered {} for {} -> body: {}", response.status(), methodKey, body);

        return switch (response.status()) {
            case 404 -> new ProductNotFoundException("Product not found in product-service");
            case 409 -> new OutOfStockException("Not enough stock in product-service");
            case 500, 502, 503, 504 -> new ProductServiceUnavailableException(
                    "product-service is having problems (HTTP " + response.status() + ")");
            default -> defaultDecoder.decode(methodKey, response);
        };
    }

    /** Reads the (small) error body as text. Careful: a body can be read only once! */
    private String readBody(Response response) {
        if (response.body() == null) {
            return "";
        }
        try (Reader reader = response.body().asReader(StandardCharsets.UTF_8)) {
            return Util.toString(reader);
        } catch (IOException e) {
            return "<unreadable body>";
        }
    }
}
