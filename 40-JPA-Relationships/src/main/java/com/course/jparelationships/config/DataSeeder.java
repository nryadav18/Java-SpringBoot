package com.course.jparelationships.config;

import com.course.jparelationships.entity.Address;
import com.course.jparelationships.entity.Customer;
import com.course.jparelationships.entity.Product;
import com.course.jparelationships.entity.PurchaseOrder;
import com.course.jparelationships.repository.CustomerRepository;
import com.course.jparelationships.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/*
 * DataSeeder - builds a small object graph in JAVA and saves it with very few save() calls,
 * thanks to cascading. Look at the console: Hibernate inserts addresses, customers, orders
 * and join-table rows in the right order for us.
 *
 *   Asha  (Bengaluru)  -> ORD-1001 [Laptop, Backpack], ORD-1002 [Charger]
 *   Ben   (London)     -> ORD-1003 [Phone, Headphones, Charger]
 *   Chen  (Singapore)  -> no orders yet
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public DataSeeder(CustomerRepository customerRepository, ProductRepository productRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (customerRepository.count() > 0) {
            return;
        }
        // Products must exist on their own (no cascade from orders to products).
        Product laptop = new Product("Laptop", new BigDecimal("899.00"));
        Product headphones = new Product("Headphones", new BigDecimal("59.99"));
        Product phone = new Product("Phone", new BigDecimal("499.00"));
        Product charger = new Product("Charger", new BigDecimal("19.99"));
        Product backpack = new Product("Backpack", new BigDecimal("39.50"));
        productRepository.saveAll(List.of(laptop, headphones, phone, charger, backpack));

        Customer asha = new Customer("Asha Rao", "asha@example.com",
                new Address("12 MG Road", "Bengaluru", "560001", "India"));
        PurchaseOrder o1 = new PurchaseOrder("ORD-1001");
        o1.addProduct(laptop);
        o1.addProduct(backpack);
        PurchaseOrder o2 = new PurchaseOrder("ORD-1002");
        o2.addProduct(charger);
        asha.addOrder(o1);
        asha.addOrder(o2);

        Customer ben = new Customer("Ben Carter", "ben@example.com",
                new Address("221B Baker Street", "London", "NW1 6XE", "UK"));
        PurchaseOrder o3 = new PurchaseOrder("ORD-1003");
        o3.addProduct(phone);
        o3.addProduct(headphones);
        o3.addProduct(charger);
        ben.addOrder(o3);

        Customer chen = new Customer("Chen Wei", "chen@example.com",
                new Address("8 Marina Blvd", "Singapore", "018981", "Singapore"));

        // ONE saveAll call -> addresses, customers, orders and order_products rows are all inserted (cascade).
        customerRepository.saveAll(List.of(asha, ben, chen));
        log.info("Seeded {} products and {} customers", productRepository.count(), customerRepository.count());
    }
}
