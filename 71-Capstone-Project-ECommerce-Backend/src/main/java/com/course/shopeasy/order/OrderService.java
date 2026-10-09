package com.course.shopeasy.order;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.course.shopeasy.cart.Cart;
import com.course.shopeasy.cart.CartItem;
import com.course.shopeasy.cart.CartRepository;
import com.course.shopeasy.common.exception.BusinessRuleException;
import com.course.shopeasy.common.exception.ResourceNotFoundException;
import com.course.shopeasy.common.web.PageResponse;
import com.course.shopeasy.config.CacheNames;
import com.course.shopeasy.product.Product;
import com.course.shopeasy.product.ProductRepository;
import com.course.shopeasy.security.UserPrincipal;

/**
 * The most important service of the shop: turning a cart into an order (topic 42: transactions).
 *
 * placeOrder() is ONE @Transactional unit of work - "all or nothing":
 * <pre>
 *   1. read the product ids in the cart
 *   2. LOCK those product rows (SELECT ... FOR UPDATE)      -> no two checkouts can sell the same last item
 *   3. for every cart line: check stock, reduce stock, copy the line into the order
 *   4. save the order, empty the cart
 *   5. COMMIT  (or ROLLBACK everything if any step threw an exception)
 * </pre>
 * If the 3rd line of the cart is out of stock, the stock already reduced for lines 1 and 2 is rolled back
 * automatically, because InsufficientStockException is a RuntimeException.
 */
@Service
@Transactional(readOnly = true)
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CacheManager cacheManager;

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository,
                        ProductRepository productRepository, CacheManager cacheManager) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cacheManager = cacheManager;
    }

    // =================================================================== customer actions

    @Transactional
    public OrderResponse placeOrder(Long userId, PlaceOrderRequest request) {
        // 1. Which products are in the cart?
        List<Long> productIds = cartRepository.findProductIdsInCart(userId);
        if (productIds.isEmpty()) {
            throw new BusinessRuleException("Your cart is empty - add products before placing an order");
        }

        // 2. Lock them BEFORE reading the stock. Rows are locked until this transaction ends.
        productRepository.findAllByIdForUpdate(productIds);

        // 3. Load the cart; its products are the freshly locked instances (same persistence context).
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessRuleException("Your cart is empty"));

        Order order = new Order(cart.getUser(), request.shippingAddress().trim());
        for (CartItem line : cart.getItems()) {
            Product product = line.getProduct();
            if (!product.isActive()) {
                throw new BusinessRuleException("'" + product.getName() + "' is no longer available. Please remove it from your cart.");
            }
            product.decreaseStock(line.getQuantity());   // throws InsufficientStockException -> rollback
            order.addLine(product, line.getQuantity());
        }

        // 4. Persist the order (cascade saves the lines) and empty the cart.
        Order saved = orderRepository.save(order);
        cart.clear();

        // Stock changed -> the cached ProductResponses of these products are now stale.
        evictProductsFromCache(productIds);

        log.info("Order {} placed by user {}: {} line(s), total {}",
                saved.getId(), userId, saved.getItems().size(), saved.getTotalAmount());
        return OrderMapper.toResponse(saved);
    }

    public PageResponse<OrderResponse> findMyOrders(Long userId, Pageable pageable) {
        return PageResponse.from(orderRepository.findByUserId(userId, pageable).map(OrderMapper::toResponse));
    }

    /**
     * Customers may see only their own orders; admins may see all.
     * For somebody else's order we answer 404, not 403 - we do not even confirm that the order exists.
     */
    public OrderResponse findOrder(Long orderId, UserPrincipal requester) {
        return OrderMapper.toResponse(getVisibleOrder(orderId, requester));
    }

    /** A customer cancels their own order. Allowed only while it is PENDING or PAID. Stock is returned. */
    @Transactional
    public OrderResponse cancelMyOrder(Long orderId, UserPrincipal requester) {
        Order order = getVisibleOrder(orderId, requester);
        changeStatus(order, OrderStatus.CANCELLED);
        log.info("Order {} cancelled by user {}", orderId, requester.id());
        return OrderMapper.toResponse(order);
    }

    // =================================================================== admin actions

    public PageResponse<OrderResponse> findAll(OrderStatus status, Pageable pageable) {
        var page = (status == null)
                ? orderRepository.findAll(pageable)
                : orderRepository.findByStatus(status, pageable);
        return PageResponse.from(page.map(OrderMapper::toResponse));
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findDetailedById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        changeStatus(order, newStatus);
        log.info("Order {} moved to {} by admin", orderId, newStatus);
        return OrderMapper.toResponse(order);
    }

    // =================================================================== helpers

    private Order getVisibleOrder(Long orderId, UserPrincipal requester) {
        return orderRepository.findDetailedById(orderId)
                .filter(order -> requester.isAdmin() || order.belongsTo(requester.id()))
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    }

    /** Central place for status changes; cancelling puts the items back in stock. */
    private void changeStatus(Order order, OrderStatus newStatus) {
        order.changeStatus(newStatus);   // throws BusinessRuleException for an illegal move
        if (newStatus == OrderStatus.CANCELLED) {
            List<Long> productIds = order.getItems().stream().map(i -> i.getProduct().getId()).distinct().toList();
            productRepository.findAllByIdForUpdate(productIds);           // lock before changing stock
            order.getItems().forEach(i -> i.getProduct().increaseStock(i.getQuantity()));
            evictProductsFromCache(productIds);
        }
    }

    /**
     * Evicts only the products that changed. Because the CacheManager is wrapped in a
     * TransactionAwareCacheManagerProxy (see CacheConfig), the eviction really happens AFTER COMMIT.
     */
    private void evictProductsFromCache(Collection<Long> productIds) {
        Cache cache = cacheManager.getCache(CacheNames.PRODUCTS);
        if (cache != null) {
            productIds.forEach(cache::evict);
        }
    }
}
