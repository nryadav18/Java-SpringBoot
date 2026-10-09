package com.course.pagination.service;

import com.course.pagination.dto.PageResponse;
import com.course.pagination.dto.ProductResponse;
import com.course.pagination.dto.SliceResponse;
import com.course.pagination.entity.Product;
import com.course.pagination.repository.ProductRepository;
import com.course.pagination.repository.ProductSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
 * ProductService - every method returns ONE PAGE, never the whole table.
 */
@Service
@Transactional(readOnly = true)
public class ProductService {

    /** Only these fields may be used for sorting - never trust the client blindly. */
    public static final Set<String> SORTABLE_FIELDS = Set.of("id", "name", "category", "price", "stock", "createdAt");
    public static final int MAX_PAGE_SIZE = 50;

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    // ---------------------------------------------------------------------
    // 1. Pageable created by Spring MVC from ?page=&size=&sort=
    // ---------------------------------------------------------------------
    public PageResponse<ProductResponse> list(Pageable pageable) {
        validateSort(pageable.getSort());
        Page<Product> page = repository.findAll(pageable);       // SELECT ... OFFSET ? FETCH FIRST ? + SELECT COUNT(*)
        return PageResponse.from(page.map(ProductResponse::from)); // Page.map converts the content, keeps the metadata
    }

    // ---------------------------------------------------------------------
    // 2. Building the PageRequest ourselves (useful when parameters come from elsewhere)
    // ---------------------------------------------------------------------
    public PageResponse<ProductResponse> listManual(int page, int size, String sortBy, String direction) {
        if (page < 0) throw new IllegalArgumentException("page must be 0 or greater");
        if (size < 1 || size > MAX_PAGE_SIZE) throw new IllegalArgumentException("size must be between 1 and " + MAX_PAGE_SIZE);

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy)   // "asc"/"desc" -> ASC/DESC
                .and(Sort.by("id"));                                         // tie-breaker -> stable order
        validateSort(sort);

        Pageable pageable = PageRequest.of(page, size, sort);
        return PageResponse.from(repository.findAll(pageable).map(ProductResponse::from));
    }

    // ---------------------------------------------------------------------
    // 3. Paging a derived query
    // ---------------------------------------------------------------------
    public PageResponse<ProductResponse> byCategory(String category, Pageable pageable) {
        validateSort(pageable.getSort());
        return PageResponse.from(repository.findByCategoryIgnoreCase(category, pageable).map(ProductResponse::from));
    }

    // ---------------------------------------------------------------------
    // 4. Slice: no COUNT query, just "hasNext"
    // ---------------------------------------------------------------------
    public SliceResponse<ProductResponse> slice(String category, Pageable pageable) {
        validateSort(pageable.getSort());
        var slice = (category == null)
                ? repository.findAllBy(pageable)
                : repository.findSliceByCategoryIgnoreCase(category, pageable);
        return SliceResponse.from(slice.map(ProductResponse::from));
    }

    // ---------------------------------------------------------------------
    // 5. Search filters + paging + sorting together
    // ---------------------------------------------------------------------
    public PageResponse<ProductResponse> search(String category, String q, BigDecimal minPrice, BigDecimal maxPrice,
                                                Pageable pageable) {
        validateSort(pageable.getSort());
        List<Specification<Product>> specs = new ArrayList<>();
        if (category != null && !category.isBlank()) specs.add(ProductSpecifications.categoryIs(category));
        if (q != null && !q.isBlank()) specs.add(ProductSpecifications.nameContains(q));
        if (minPrice != null) specs.add(ProductSpecifications.priceAtLeast(minPrice));
        if (maxPrice != null) specs.add(ProductSpecifications.priceAtMost(maxPrice));

        Page<Product> page = repository.findAll(Specification.allOf(specs), pageable);
        return PageResponse.from(page.map(ProductResponse::from));
    }

    // ---------------------------------------------------------------------
    // 6. Sorting without paging
    // ---------------------------------------------------------------------
    public List<ProductResponse> cheapestInCategory(String category) {
        return repository.findTop5ByCategoryIgnoreCase(category, Sort.by("price").ascending().and(Sort.by("id")))
                .stream().map(ProductResponse::from).toList();
    }

    // ---------------------------------------------------------------------
    // 7. @Query + Pageable
    // ---------------------------------------------------------------------
    public PageResponse<ProductResponse> lowStock(int threshold, Pageable pageable) {
        validateSort(pageable.getSort());
        return PageResponse.from(repository.findLowStock(threshold, pageable).map(ProductResponse::from));
    }

    // ---------------------------------------------------------------------
    // 8. Returning Spring's Page directly (serialized as PagedModel because of
    //    spring.data.web.pageable.serialization-mode=via-dto)
    // ---------------------------------------------------------------------
    public Page<ProductResponse> pageForPagedModel(Pageable pageable) {
        validateSort(pageable.getSort());
        return repository.findAll(pageable).map(ProductResponse::from);
    }

    // ---------------------------------------------------------------------
    private void validateSort(Sort sort) {
        for (Sort.Order order : sort) {
            if (!SORTABLE_FIELDS.contains(order.getProperty())) {
                throw new IllegalArgumentException("Cannot sort by '" + order.getProperty()
                        + "'. Allowed: " + SORTABLE_FIELDS);
            }
        }
    }
}
