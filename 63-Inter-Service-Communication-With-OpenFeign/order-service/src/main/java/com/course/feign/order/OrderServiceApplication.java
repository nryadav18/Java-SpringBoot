/*
 * Topic 63 - OpenFeign | order-service
 * ---------------------------------------------------------------
 * The "consumer" side. To place an order it must ask product-service:
 *   "does product 1 exist, what does it cost, and please reduce its stock".
 * Instead of writing RestClient code by hand, we DECLARE an interface (ProductClient)
 * and OpenFeign generates the HTTP client at startup.
 *
 * @EnableFeignClients = "Spring, please scan this package (and sub-packages) for
 *                         @FeignClient interfaces and create a proxy bean for each".
 *
 * Run (product-service must already be running on 8081):
 *   cd order-service  ->  mvn spring-boot:run      (http://localhost:8082)
 */
package com.course.feign.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
