package com.course.jparelationships;

import com.course.jparelationships.dto.Dtos.AddressDto;
import com.course.jparelationships.dto.Dtos.CreateCustomerRequest;
import com.course.jparelationships.dto.Dtos.CreateOrderRequest;
import com.course.jparelationships.dto.Dtos.CustomerDetails;
import com.course.jparelationships.dto.Dtos.OrderResponse;
import com.course.jparelationships.entity.Customer;
import com.course.jparelationships.repository.CustomerRepository;
import com.course.jparelationships.repository.ProductRepository;
import com.course.jparelationships.repository.PurchaseOrderRepository;
import com.course.jparelationships.service.CustomerService;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/*
 * RelationshipsTest - proves the important relationship behaviours.
 * ------------------------------------------------------------------
 * @SpringBootTest starts the whole application (incl. DataSeeder) WITHOUT a web server.
 * The test methods are NOT transactional, so - just like a controller - every repository
 * call ends its own transaction. That lets us see LazyInitializationException for real.
 *
 * Tests that change data clean up after themselves so they don't affect each other.
 * Run:  mvn test
 */
@SpringBootTest
class RelationshipsTest {

    @Autowired CustomerService service;
    @Autowired CustomerRepository customerRepository;
    @Autowired PurchaseOrderRepository orderRepository;
    @Autowired ProductRepository productRepository;

    @Test
    @DisplayName("Cascade: the seeder's single saveAll() stored customers, addresses, orders and join rows")
    void cascadeSavedTheWholeGraph() {
        CustomerDetails asha = service.getDetails(1L);

        assertThat(asha.address().city()).isEqualTo("Bengaluru");
        assertThat(asha.orders()).hasSize(2);
        assertThat(asha.orders().get(0).products()).extracting("name").contains("Laptop", "Backpack");
    }

    @Test
    @DisplayName("Touching a LAZY collection outside a transaction -> LazyInitializationException")
    void lazyCollectionOutsideTransactionFails() {
        Customer detached = customerRepository.findById(1L).orElseThrow();   // transaction already closed

        assertThatThrownBy(() -> detached.getOrders().size())
                .isInstanceOf(LazyInitializationException.class);
    }

    @Test
    @DisplayName("@EntityGraph loads the relationships up front -> no exception later")
    void entityGraphPreloadsRelationships() {
        Customer customer = customerRepository.findWithDetailsById(1L).orElseThrow();

        assertThat(customer.getOrders()).hasSize(2);                        // works: already loaded
        assertThat(customer.getOrders().iterator().next().getProducts()).isNotEmpty();
        assertThat(customer.getAddress().getCity()).isEqualTo("Bengaluru");
    }

    @Test
    @DisplayName("N+1: findAll() needs 1 + N + N statements, JOIN FETCH / @EntityGraph need only 1")
    void nPlusOneVersusFetchJoin() {
        assertThat(service.demoNPlusOne().sqlStatements()).isEqualTo(7);   // 1 customers + 3 addresses + 3 order lists
        assertThat(service.demoJoinFetch().sqlStatements()).isEqualTo(1);
        assertThat(service.demoEntityGraph().sqlStatements()).isEqualTo(1);
    }

    @Test
    @DisplayName("orphanRemoval: removing an order from the list DELETEs it, products survive")
    void orphanRemovalDeletesOrder() {
        CustomerDetails dina = service.create(new CreateCustomerRequest("Dina", "dina@example.com",
                new AddressDto("1 Main St", "Pune", "411001", "India")));
        OrderResponse order = service.placeOrder(dina.id(), new CreateOrderRequest(List.of(1L, 4L)));
        assertThat(order.total()).isEqualByComparingTo("918.99");             // Laptop 899.00 + Charger 19.99
        long productsBefore = productRepository.count();

        service.removeOrder(dina.id(), order.id());

        assertThat(orderRepository.findById(order.id())).isEmpty();          // order row deleted
        assertThat(productRepository.count()).isEqualTo(productsBefore);      // catalogue untouched

        service.deleteCustomer(dina.id());                                    // clean up
    }

    @Test
    @DisplayName("Cascade REMOVE: deleting a customer deletes its address and orders, not the products")
    void deletingCustomerCascades() {
        CustomerDetails eli = service.create(new CreateCustomerRequest("Eli", "eli@example.com",
                new AddressDto("2 High St", "Leeds", "LS1", "UK")));
        OrderResponse order = service.placeOrder(eli.id(), new CreateOrderRequest(List.of(2L)));
        long productsBefore = productRepository.count();

        service.deleteCustomer(eli.id());

        assertThat(customerRepository.existsById(eli.id())).isFalse();
        assertThat(orderRepository.existsById(order.id())).isFalse();
        assertThat(productRepository.count()).isEqualTo(productsBefore);
    }

    @Test
    @DisplayName("Derived queries can walk through relationships")
    void derivedQueriesThroughRelationships() {
        assertThat(orderRepository.findByCustomerId(2L)).hasSize(1);
        assertThat(orderRepository.findByProductsName("Charger")).hasSize(2);   // ORD-1002 and ORD-1003
    }
}
