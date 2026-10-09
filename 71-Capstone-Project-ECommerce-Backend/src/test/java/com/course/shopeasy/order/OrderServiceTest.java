package com.course.shopeasy.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.test.util.ReflectionTestUtils;

import com.course.shopeasy.cart.Cart;
import com.course.shopeasy.cart.CartRepository;
import com.course.shopeasy.category.Category;
import com.course.shopeasy.common.exception.BusinessRuleException;
import com.course.shopeasy.common.exception.InsufficientStockException;
import com.course.shopeasy.common.exception.ResourceNotFoundException;
import com.course.shopeasy.config.CacheNames;
import com.course.shopeasy.product.Product;
import com.course.shopeasy.product.ProductRepository;
import com.course.shopeasy.security.UserPrincipal;
import com.course.shopeasy.user.Role;
import com.course.shopeasy.user.User;

/**
 * Pure UNIT test of OrderService (topic 46): no Spring context, no database - just Mockito mocks.
 * Runs in milliseconds and checks the business rules of checkout and cancellation.
 *
 * Pattern in every test: Arrange (given) -> Act (when) -> Assert (then).
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final Long USER_ID = 7L;

    @Mock private OrderRepository orderRepository;
    @Mock private CartRepository cartRepository;
    @Mock private ProductRepository productRepository;
    @Mock private CacheManager cacheManager;

    @InjectMocks
    private OrderService orderService;

    private User customer;
    private Product phone;     // price 100.00, stock 10
    private Product watch;     // price  50.00, stock 2
    private Cart cart;
    private ConcurrentMapCache productCache;

    @BeforeEach
    void setUp() {
        customer = new User("Test Customer", "test@shopeasy.com", "hash", Role.CUSTOMER);
        ReflectionTestUtils.setField(customer, "id", USER_ID);   // ids are normally set by the database

        Category electronics = new Category("Electronics", null);
        ReflectionTestUtils.setField(electronics, "id", 1L);

        phone = product(1L, "Phone", "100.00", 10, electronics);
        watch = product(2L, "Watch", "50.00", 2, electronics);

        cart = new Cart(customer);
        productCache = new ConcurrentMapCache(CacheNames.PRODUCTS);
    }

    @Test
    @DisplayName("placeOrder reduces stock, snapshots prices, empties the cart and evicts cached products")
    void placeOrder_happyPath() {
        // given: 3 phones + 1 watch in the cart, and phone #1 is currently cached
        cart.addProduct(phone, 3);
        cart.addProduct(watch, 1);
        productCache.put(1L, "stale cached phone");
        givenCartWithProducts(List.of(1L, 2L));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cacheManager.getCache(CacheNames.PRODUCTS)).thenReturn(productCache);

        // when
        OrderResponse response = orderService.placeOrder(USER_ID, new PlaceOrderRequest("  12 Main Road, Pune  "));

        // then
        assertThat(phone.getStockQuantity()).isEqualTo(7);
        assertThat(watch.getStockQuantity()).isEqualTo(1);
        assertThat(response.totalAmount()).isEqualByComparingTo("350.00");   // 3*100 + 1*50
        assertThat(response.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.shippingAddress()).isEqualTo("12 Main Road, Pune");
        assertThat(response.items()).hasSize(2);
        assertThat(cart.isEmpty()).isTrue();
        assertThat(productCache.get(1L)).isNull();
        verify(productRepository).findAllByIdForUpdate(List.of(1L, 2L));     // rows were locked
    }

    @Test
    @DisplayName("placeOrder fails with InsufficientStockException and saves nothing when stock is too low")
    void placeOrder_insufficientStock() {
        cart.addProduct(watch, 5);   // only 2 in stock
        givenCartWithProducts(List.of(2L));

        assertThatThrownBy(() -> orderService.placeOrder(USER_ID, new PlaceOrderRequest("Somewhere")))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("requested 5, available 2");

        assertThat(watch.getStockQuantity()).isEqualTo(2);
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("placeOrder with an empty cart is rejected before anything is locked")
    void placeOrder_emptyCart() {
        when(cartRepository.findProductIdsInCart(USER_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> orderService.placeOrder(USER_ID, new PlaceOrderRequest("Somewhere")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("cart is empty");

        verify(productRepository, never()).findAllByIdForUpdate(anyCollection());
    }

    @Test
    @DisplayName("cancelMyOrder returns the items to stock")
    void cancel_restoresStock() {
        Order order = existingOrder(customer, phone, 4);   // phone stock is now 6
        when(orderRepository.findDetailedById(100L)).thenReturn(Optional.of(order));
        when(cacheManager.getCache(CacheNames.PRODUCTS)).thenReturn(productCache);

        OrderResponse response = orderService.cancelMyOrder(100L, principal(USER_ID, Role.CUSTOMER));

        assertThat(response.status()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(phone.getStockQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("a customer cannot see or cancel another customer's order (404, not 403)")
    void cancel_otherCustomersOrder() {
        Order order = existingOrder(customer, phone, 1);
        when(orderRepository.findDetailedById(100L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelMyOrder(100L, principal(999L, Role.CUSTOMER)))
                .isInstanceOf(ResourceNotFoundException.class);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("a shipped order cannot be cancelled")
    void cancel_shippedOrder() {
        Order order = existingOrder(customer, phone, 1);
        order.changeStatus(OrderStatus.PAID);
        order.changeStatus(OrderStatus.SHIPPED);
        when(orderRepository.findDetailedById(100L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelMyOrder(100L, principal(USER_ID, Role.CUSTOMER)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("SHIPPED");
    }

    // ------------------------------------------------------------------ test helpers

    private void givenCartWithProducts(List<Long> productIds) {
        when(cartRepository.findProductIdsInCart(USER_ID)).thenReturn(productIds);
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
    }

    private static Product product(Long id, String name, String price, int stock, Category category) {
        Product p = new Product("SKU-" + id, name, null, new BigDecimal(price), stock, null, category);
        ReflectionTestUtils.setField(p, "id", id);
        return p;
    }

    private static Order existingOrder(User user, Product product, int quantity) {
        product.decreaseStock(quantity);
        Order order = new Order(user, "Somewhere");
        order.addLine(product, quantity);
        ReflectionTestUtils.setField(order, "id", 100L);
        return order;
    }

    private static UserPrincipal principal(Long id, Role role) {
        return new UserPrincipal(id, "someone@shopeasy.com", "hash", role, true);
    }
}
