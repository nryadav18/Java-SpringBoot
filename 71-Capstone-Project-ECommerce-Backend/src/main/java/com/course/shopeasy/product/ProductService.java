package com.course.shopeasy.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.course.shopeasy.category.Category;
import com.course.shopeasy.category.CategoryService;
import com.course.shopeasy.common.exception.BusinessRuleException;
import com.course.shopeasy.common.exception.DuplicateResourceException;
import com.course.shopeasy.common.exception.ResourceNotFoundException;
import com.course.shopeasy.common.web.PageResponse;
import com.course.shopeasy.config.CacheNames;

/**
 * Business logic for the product catalog.
 *
 * Read side:  paginated + filtered search (topics 39, 41) and a cached "get by id" (topic 51).
 * Write side: create / update / soft delete - ADMIN only (checked in SecurityConfig and the controller).
 *
 * Cache strategy (cache-aside):
 *   findById  -> @Cacheable: first call hits the DB and stores the DTO, next calls come from memory.
 *   update / delete -> @CacheEvict for that id, so the next read loads fresh data.
 *   OrderService also evicts products, because placing an order changes the stock.
 * We do NOT cache search pages: too many combinations of filters/pages, and stock changes often.
 */
@Service
@Transactional(readOnly = true)
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    public PageResponse<ProductResponse> search(ProductSearchCriteria criteria, Pageable pageable) {
        return PageResponse.from(
                productRepository.findAll(ProductSpecifications.from(criteria), pageable)
                        .map(ProductMapper::toResponse));
    }

    @Cacheable(cacheNames = CacheNames.PRODUCTS, key = "#id")
    public ProductResponse findById(Long id) {
        log.debug("Loading product {} from the database (cache miss)", id);
        return ProductMapper.toResponse(getActiveEntity(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySkuIgnoreCase(request.sku())) {
            throw new DuplicateResourceException("A product with SKU " + request.sku() + " already exists");
        }
        Category category = categoryService.getEntity(request.categoryId());
        Product saved = productRepository.save(ProductMapper.toEntity(request, category));
        log.info("Product created: id={}, sku={}", saved.getId(), saved.getSku());
        return ProductMapper.toResponse(saved);
    }

    /**
     * Full update (PUT). The SKU is the business identity and cannot be changed - if it differs we refuse.
     */
    @Transactional
    @CacheEvict(cacheNames = CacheNames.PRODUCTS, key = "#id")
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getActiveEntity(id);
        if (!product.getSku().equalsIgnoreCase(request.sku())) {
            throw new BusinessRuleException(
                    "SKU cannot be changed (current: " + product.getSku() + ")");
        }
        Category category = categoryService.getEntity(request.categoryId());
        product.update(request.name().trim(), request.description(), request.price(),
                request.stockQuantity(), request.imageUrl(), category);
        // flush now so the @Version increment (and any DB constraint error) happens inside this method
        productRepository.flush();
        log.info("Product updated: id={}", id);
        return ProductMapper.toResponse(product);
    }

    /** Soft delete: the product disappears from the shop, but old orders that reference it stay valid. */
    @Transactional
    @CacheEvict(cacheNames = CacheNames.PRODUCTS, key = "#id")
    public void delete(Long id) {
        getActiveEntity(id).deactivate();
        log.info("Product deactivated: id={}", id);
    }

    /** Used by CartService: only active products can be bought. */
    public Product getActiveEntity(Long id) {
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }
}
