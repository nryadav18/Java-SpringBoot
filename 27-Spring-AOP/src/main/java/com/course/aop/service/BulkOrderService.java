/*
 * The FIX for self-invocation: move the "outer" method into ANOTHER bean.
 *
 * BulkOrderService receives the OrderService PROXY by injection, so every call to
 * orderService.placeOrder(...) goes through the proxy and the aspects run.
 * (Same reason why @Transactional on a method called from the same class doesn't work - Topic 42.)
 */
package com.course.aop.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BulkOrderService {

    private final OrderService orderService;   // this is the PROXY, not the raw object

    public BulkOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public List<String> placeBulkOrders(List<String> items) {
        return items.stream()
                .map(item -> orderService.placeOrder(item, 1))   // through the proxy -> advice runs
                .toList();
    }
}
