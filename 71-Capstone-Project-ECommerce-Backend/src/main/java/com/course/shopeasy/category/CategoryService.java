package com.course.shopeasy.category;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.course.shopeasy.common.exception.BusinessRuleException;
import com.course.shopeasy.common.exception.DuplicateResourceException;
import com.course.shopeasy.common.exception.ResourceNotFoundException;
import com.course.shopeasy.config.CacheNames;
import com.course.shopeasy.product.ProductRepository;

/**
 * Business logic for categories.
 *
 * Caching (topic 51): the full category list is small and read on almost every page of a shop
 * (menus, filters), so we cache it. Every write evicts the cache so readers never see stale data.
 * Product responses contain the category NAME, so renaming a category also clears the products cache.
 *
 * @Transactional(readOnly = true) on the class = default for all methods; write methods override it.
 */
@Service
@Transactional(readOnly = true)
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Cacheable(CacheNames.CATEGORIES)
    public List<CategoryResponse> findAll() {
        log.debug("Loading categories from the database (cache miss)");
        return categoryRepository.findAll(Sort.by("name")).stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    public CategoryResponse findById(Long id) {
        return CategoryMapper.toResponse(getEntity(id));
    }

    @Transactional
    @CacheEvict(cacheNames = CacheNames.CATEGORIES, allEntries = true)
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name().trim())) {
            throw new DuplicateResourceException("Category '" + request.name() + "' already exists");
        }
        Category saved = categoryRepository.save(CategoryMapper.toEntity(request));
        log.info("Category created: id={}, name={}", saved.getId(), saved.getName());
        return CategoryMapper.toResponse(saved);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.CATEGORIES, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.PRODUCTS, allEntries = true)
    })
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getEntity(id);
        String newName = request.name().trim();
        categoryRepository.findByNameIgnoreCase(newName)
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new DuplicateResourceException("Category '" + newName + "' already exists");
                });
        // No save() call needed: the entity is "managed" - Hibernate writes the change at commit (dirty checking, topic 38).
        category.update(newName, request.description());
        return CategoryMapper.toResponse(category);
    }

    @Transactional
    @CacheEvict(cacheNames = CacheNames.CATEGORIES, allEntries = true)
    public void delete(Long id) {
        Category category = getEntity(id);
        if (productRepository.existsByCategoryId(id)) {
            throw new BusinessRuleException("Category '" + category.getName() + "' still has products and cannot be deleted");
        }
        categoryRepository.delete(category);
        log.info("Category deleted: id={}", id);
    }

    /** Used by ProductService too - "find or throw 404" lives in one place. */
    public Category getEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }
}
