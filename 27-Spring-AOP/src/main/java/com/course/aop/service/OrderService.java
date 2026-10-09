/*
 * A normal business service - notice there is NO logging or timing code in here.
 * Those "cross-cutting concerns" are added from the outside by aspects.
 *
 * OrderService does NOT implement an interface, so Spring makes a CGLIB proxy
 * (a generated SUBCLASS of OrderService) to apply the aspects.
 *
 * placeBulkOrders() demonstrates the SELF-INVOCATION gotcha: its internal call
 * this.placeOrder(...) does NOT go through the proxy, so no advice runs for it.
 */
package com.course.aop.service;

import com.course.aop.annotation.LogExecutionTime;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final Map<String, String> orders = new LinkedHashMap<>();
    private int nextId = 1;

    public String placeOrder(String item, int quantity) {
        String orderId = "ORD-" + nextId++;
        orders.put(orderId, quantity + " x " + item);
        System.out.println("      (business logic) saved " + orderId + ": " + quantity + " x " + item);
        return orderId;
    }

    public void cancelOrder(String orderId) {
        if (!orders.containsKey(orderId)) {
            throw new IllegalArgumentException("No such order: " + orderId);
        }
        orders.remove(orderId);
        System.out.println("      (business logic) cancelled " + orderId);
    }

    @LogExecutionTime
    public String generateMonthlyReport() throws InterruptedException {
        Thread.sleep(150);   // pretend this is a slow database query
        return "Monthly report: " + orders.size() + " open orders";
    }

    /** SELF-INVOCATION demo: calls another method of THIS class directly. */
    public List<String> placeBulkOrders(List<String> items) {
        List<String> ids = new ArrayList<>();
        for (String item : items) {
            ids.add(this.placeOrder(item, 1));   // "this" = the real object, NOT the proxy -> no advice!
        }
        return ids;
    }
}
