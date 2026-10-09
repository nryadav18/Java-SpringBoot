package com.course.patterns.solid.dip;

import com.course.patterns.demo.PatternDemo;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/*
 * D - DEPENDENCY INVERSION PRINCIPLE
 * "High-level code (business rules) should depend on ABSTRACTIONS (interfaces),
 *  not on low-level details (MySQL, HTTP clients, files)."
 *
 * BAD : OrderServiceBad does "new MySqlOrderDatabase()" itself. It cannot be tested without
 *       MySQL and cannot switch to another storage without editing the service.
 * GOOD: OrderService asks for an OrderRepository in its CONSTRUCTOR. Whoever creates it decides
 *       which implementation to pass. In Spring, the container does that = Dependency Injection
 *       (topics 22-24). DIP is the PRINCIPLE, DI is the TECHNIQUE, Spring is the TOOL.
 *
 * Expected output:
 *   [bad ] Saved 'ORD-1' into MySQL (hard-wired!)
 *   [good] Saved via InMemoryOrderRepository: [ORD-1, ORD-2]
 *   [good] Same service, different storage: FakeEmailingOrderRepository got ORD-3
 */
@Component
@Order(5)
public class DipDemo implements PatternDemo {

    // ---------------------------------------------------------- BAD
    static class MySqlOrderDatabase {
        String insert(String orderId) {
            return "Saved '" + orderId + "' into MySQL (hard-wired!)";
        }
    }

    static class OrderServiceBad {
        private final MySqlOrderDatabase database = new MySqlOrderDatabase(); // tight coupling

        String place(String orderId) {
            return database.insert(orderId);
        }
    }

    // ---------------------------------------------------------- GOOD
    /** The abstraction, owned by the business side. */
    interface OrderRepository {
        void save(String orderId);

        List<String> findAll();
    }

    static class InMemoryOrderRepository implements OrderRepository {
        private final List<String> orders = new ArrayList<>();

        public void save(String orderId) { orders.add(orderId); }

        public List<String> findAll() { return List.copyOf(orders); }
    }

    static class FakeEmailingOrderRepository implements OrderRepository {
        private String last;

        public void save(String orderId) { last = orderId; }

        public List<String> findAll() { return last == null ? List.of() : List.of(last); }
    }

    /** High-level policy: knows NOTHING about MySQL, memory or email. */
    static class OrderService {
        private final OrderRepository repository;

        OrderService(OrderRepository repository) { // constructor injection
            this.repository = repository;
        }

        void place(String orderId) {
            repository.save(orderId);
        }
    }

    @Override
    public String title() {
        return "D - Dependency Inversion Principle";
    }

    @Override
    public void run() {
        System.out.println("[bad ] " + new OrderServiceBad().place("ORD-1"));

        InMemoryOrderRepository memory = new InMemoryOrderRepository();
        OrderService service = new OrderService(memory);
        service.place("ORD-1");
        service.place("ORD-2");
        System.out.println("[good] Saved via InMemoryOrderRepository: " + memory.findAll());

        FakeEmailingOrderRepository other = new FakeEmailingOrderRepository();
        new OrderService(other).place("ORD-3");
        System.out.println("[good] Same service, different storage: FakeEmailingOrderRepository got "
                + other.findAll().get(0));
    }
}
