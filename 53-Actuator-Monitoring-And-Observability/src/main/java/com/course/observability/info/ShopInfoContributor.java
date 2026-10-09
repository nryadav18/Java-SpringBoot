package com.course.observability.info;

import java.util.Map;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import com.course.observability.order.OrderService;

/*
 * A CUSTOM INFO CONTRIBUTOR: adds our own section to /actuator/info.
 *
 * /actuator/info is put together from several "contributors":
 *   - "app"   : from info.* properties in application.properties (env contributor)
 *   - "build" : from META-INF/build-info.properties (spring-boot-maven-plugin build-info goal)
 *   - "java", "os" : JVM and operating system details (switched on in application.properties)
 *   - "shop"  : from THIS class
 */
@Component
public class ShopInfoContributor implements InfoContributor {

    private final OrderService orderService;

    public ShopInfoContributor(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("shop", Map.of(
                "region", "EU-West",
                "supportEmail", "support@shop.example",
                "ordersSinceStartup", orderService.countAll(),
                "pendingOrders", orderService.countPending()));
    }
}
