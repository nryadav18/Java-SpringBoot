package com.course.webflux.functional;

/*
 * The "routing table" for functional endpoints.
 * Read it like a menu:  GET /fn/hello  -> handler.hello(...)
 */
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> functionalRoutes(GreetingHandler handler) {
        return route(GET("/fn/hello"), handler::hello)
                .andRoute(GET("/fn/products"), handler::products);
    }
}
