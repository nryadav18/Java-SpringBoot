package com.course.jparelationships.service;

import com.course.jparelationships.dto.Dtos.CreateCustomerRequest;
import com.course.jparelationships.dto.Dtos.CreateOrderRequest;
import com.course.jparelationships.dto.Dtos.CustomerDetails;
import com.course.jparelationships.dto.Dtos.CustomerSummary;
import com.course.jparelationships.dto.Dtos.OrderResponse;
import com.course.jparelationships.dto.Dtos.QueryStats;
import com.course.jparelationships.entity.Customer;
import com.course.jparelationships.entity.Product;
import com.course.jparelationships.entity.PurchaseOrder;
import com.course.jparelationships.exception.ResourceNotFoundException;
import com.course.jparelationships.repository.CustomerRepository;
import com.course.jparelationships.repository.ProductRepository;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.LazyInitializationException;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/*
 * CustomerService - all relationship work happens HERE, inside transactions,
 * so lazy relationships can be loaded safely before we convert entities to DTOs.
 */
@Service
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final Statistics statistics;   // Hibernate's SQL statement counter (for the demos)

    public CustomerService(CustomerRepository customerRepository,
                           ProductRepository productRepository,
                           EntityManagerFactory entityManagerFactory) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
    }

    // =====================================================================
    // Normal API methods
    // =====================================================================

    public List<CustomerSummary> listCustomers() {
        // Uses the @EntityGraph query -> ONE SQL statement, no N+1.
        return customerRepository.findAllWithAddressAndOrders().stream().map(CustomerSummary::from).toList();
    }

    public CustomerDetails getDetails(Long id) {
        return CustomerDetails.from(loadWithDetails(id));
    }

    /** Returns the ENTITY (with Jackson annotations) - only for the /raw demo endpoint. */
    public Customer getRawEntity(Long id) {
        return loadWithDetails(id);   // everything the JSON needs is loaded by the entity graph
    }

    @Transactional
    public CustomerDetails create(CreateCustomerRequest request) {
        if (customerRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("Email " + request.email() + " is already registered");
        }
        Customer customer = new Customer(request.name(), request.email(), request.address().toEntity());
        // ONE save() call - cascade = ALL also INSERTs the new Address for us.
        return CustomerDetails.from(customerRepository.save(customer));
    }

    @Transactional
    public OrderResponse placeOrder(Long customerId, CreateOrderRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer " + customerId + " not found"));

        List<Product> products = productRepository.findAllById(new HashSet<>(request.productIds()));
        if (products.size() != new HashSet<>(request.productIds()).size()) {
            throw new IllegalArgumentException("One or more product ids do not exist: " + request.productIds());
        }

        PurchaseOrder order = new PurchaseOrder("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        products.forEach(order::addProduct);   // fills the join table order_products
        customer.addOrder(order);              // sets BOTH sides (order.customer + customer.orders)

        // No orderRepository.save(order) needed: customer is MANAGED and its "orders" list has
        // cascade = ALL, so the new order is INSERTed at commit. We flush now to get the generated id.
        customerRepository.flush();
        return OrderResponse.from(order);
    }

    @Transactional
    public void removeOrder(Long customerId, Long orderId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer " + customerId + " not found"));
        PurchaseOrder order = customer.getOrders().stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Order " + orderId + " not found for customer " + customerId));

        // Just remove it from the list - orphanRemoval = true turns that into a DELETE.
        customer.removeOrder(order);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer " + id + " not found"));
        // cascade = ALL: deletes the customer's orders (+ their join-table rows) and the address.
        // Products are NOT deleted - there is no cascade from orders to products.
        customerRepository.delete(customer);
    }

    private Customer loadWithDetails(Long id) {
        return customerRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer " + id + " not found"));
    }

    // =====================================================================
    // Demo methods - SEE the N+1 problem and the fixes (counts real SQL statements)
    // =====================================================================

    public QueryStats demoNPlusOne() {
        return measure("findAll() + lazy loading (N+1 problem)", customerRepository::findAll);
    }

    public QueryStats demoJoinFetch() {
        return measure("JPQL LEFT JOIN FETCH", customerRepository::findAllWithOrders);
    }

    public QueryStats demoEntityGraph() {
        return measure("@EntityGraph(attributePaths = {address, orders})", customerRepository::findAllWithAddressAndOrders);
    }

    private QueryStats measure(String approach, Supplier<List<Customer>> loader) {
        statistics.clear();
        List<Customer> customers = loader.get();
        // CustomerSummary.from touches c.getAddress().getCity() and c.getOrders().size().
        // If they were not fetched yet, EACH call fires another SELECT.
        List<CustomerSummary> data = customers.stream().map(CustomerSummary::from).toList();
        return new QueryStats(approach, customers.size(), statistics.getPrepareStatementCount(), data);
    }

    /**
     * Shows LazyInitializationException on purpose.
     * NOT_SUPPORTED = run this method WITHOUT a transaction. findById() opens and closes its own
     * short transaction, so the returned customer is DETACHED. Touching its lazy orders then fails.
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Map<String, String> demoLazyInitializationException(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer " + id + " not found"));
        try {
            int count = customer.getOrders().size();   // lazy collection, but no open session -> boom
            return Map.of("result", "No exception?! orders=" + count);
        } catch (LazyInitializationException ex) {
            return Map.of("exception", ex.getClass().getSimpleName(), "message", ex.getMessage());
        }
    }
}
