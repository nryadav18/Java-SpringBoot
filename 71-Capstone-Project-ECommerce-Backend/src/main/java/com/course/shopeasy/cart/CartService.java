package com.course.shopeasy.cart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.course.shopeasy.common.exception.BusinessRuleException;
import com.course.shopeasy.common.exception.InsufficientStockException;
import com.course.shopeasy.common.exception.ResourceNotFoundException;
import com.course.shopeasy.product.Product;
import com.course.shopeasy.product.ProductService;
import com.course.shopeasy.user.UserRepository;

/**
 * Business logic for the shopping cart. Every method works on the cart of ONE user, identified by the
 * id from the JWT - a customer can never touch somebody else's cart.
 *
 * Stock is checked here too, to give early feedback ("only 3 left"). But the cart does NOT reserve stock;
 * the final, authoritative check happens at checkout in OrderService (with row locks).
 */
@Service
@Transactional
public class CartService {

    /** Same limit as @Max(99) on the request: protects against absurd quantities when adding repeatedly. */
    static final int MAX_QUANTITY_PER_LINE = 99;

    private static final Logger log = LoggerFactory.getLogger(CartService.class);

    private final CartRepository cartRepository;
    private final ProductService productService;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, ProductService productService, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(CartMapper::toResponse)
                .orElseGet(CartResponse::empty);   // no cart yet = an empty cart; no need to create a row
    }

    public CartResponse addItem(Long userId, AddCartItemRequest request) {
        Product product = productService.getActiveEntity(request.productId());
        Cart cart = getOrCreateCart(userId);

        int alreadyInCart = cart.findItem(product.getId()).map(CartItem::getQuantity).orElse(0);
        int newQuantity = alreadyInCart + request.quantity();
        checkQuantity(product, newQuantity);

        cart.addProduct(product, request.quantity());
        log.debug("User {} added {} x product {} to cart", userId, request.quantity(), product.getId());
        return CartMapper.toResponse(cart);
    }

    public CartResponse updateItem(Long userId, Long productId, UpdateCartItemRequest request) {
        Cart cart = getExistingCart(userId);
        CartItem item = cart.findItem(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item for product", productId));
        checkQuantity(item.getProduct(), request.quantity());
        item.changeQuantity(request.quantity());
        return CartMapper.toResponse(cart);
    }

    public CartResponse removeItem(Long userId, Long productId) {
        Cart cart = getExistingCart(userId);
        if (!cart.removeProduct(productId)) {
            throw new ResourceNotFoundException("Cart item for product", productId);
        }
        return CartMapper.toResponse(cart);
    }

    public void clear(Long userId) {
        cartRepository.findByUserId(userId).ifPresent(Cart::clear);
    }

    // ------------------------------------------------------------------ helpers

    private void checkQuantity(Product product, int quantity) {
        if (quantity > MAX_QUANTITY_PER_LINE) {
            throw new BusinessRuleException("You can buy at most " + MAX_QUANTITY_PER_LINE + " units of one product");
        }
        if (!product.hasStock(quantity)) {
            throw new InsufficientStockException(product.getId(), product.getName(), quantity, product.getStockQuantity());
        }
    }

    private Cart getExistingCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart of user", userId));
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                // getReferenceById: a lazy proxy - we only need the user's id for the foreign key, no SELECT.
                .orElseGet(() -> cartRepository.save(new Cart(userRepository.getReferenceById(userId))));
    }
}
