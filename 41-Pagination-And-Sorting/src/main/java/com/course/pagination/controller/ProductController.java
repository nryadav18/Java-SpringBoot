package com.course.pagination.controller;

import com.course.pagination.dto.PageResponse;
import com.course.pagination.dto.ProductResponse;
import com.course.pagination.dto.SliceResponse;
import com.course.pagination.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/*
 * ProductController
 * -----------------
 * A "Pageable pageable" parameter is filled AUTOMATICALLY by Spring from the query string:
 *
 *     ?page=2&size=5&sort=price,desc&sort=name,asc
 *      |      |      |                 \__ second sort order (tie-breaker)
 *      |      |      \__ property,direction
 *      |      \__ rows per page (max 50 - see application.properties)
 *      \__ page index, starting at 0
 *
 * @PageableDefault sets the values used when the client sends nothing.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // GET /api/products?page=0&size=5&sort=price,desc
    @GetMapping
    public PageResponse<ProductResponse> list(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return service.list(pageable);
    }

    // GET /api/products/manual?page=1&size=5&sortBy=name&direction=asc
    @GetMapping("/manual")
    public PageResponse<ProductResponse> listManual(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "id") String sortBy,
                                                    @RequestParam(defaultValue = "asc") String direction) {
        return service.listManual(page, size, sortBy, direction);
    }

    // GET /api/products/category/Books?page=0&size=5&sort=price,asc
    @GetMapping("/category/{category}")
    public PageResponse<ProductResponse> byCategory(@PathVariable String category,
                                                    @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return service.byCategory(category, pageable);
    }

    // GET /api/products/category/Books/cheapest   (Sort only, top 5)
    @GetMapping("/category/{category}/cheapest")
    public List<ProductResponse> cheapest(@PathVariable String category) {
        return service.cheapestInCategory(category);
    }

    // GET /api/products/slice?page=0&size=20            ("load more" style, no COUNT query)
    // GET /api/products/slice?category=Sports&page=1&size=5
    @GetMapping("/slice")
    public SliceResponse<ProductResponse> slice(@RequestParam(required = false) String category,
                                                @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return service.slice(category, pageable);
    }

    // GET /api/products/search?category=Books&q=classic&minPrice=10&maxPrice=80&page=0&size=5&sort=price,desc
    @GetMapping("/search")
    public PageResponse<ProductResponse> search(@RequestParam(required = false) String category,
                                                @RequestParam(required = false) String q,
                                                @RequestParam(required = false) BigDecimal minPrice,
                                                @RequestParam(required = false) BigDecimal maxPrice,
                                                @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.ASC)
                                                Pageable pageable) {
        return service.search(category, q, minPrice, maxPrice, pageable);
    }

    // GET /api/products/low-stock?threshold=10&sort=stock,asc
    @GetMapping("/low-stock")
    public PageResponse<ProductResponse> lowStock(@RequestParam(defaultValue = "10") int threshold,
                                                  @PageableDefault(sort = "stock") Pageable pageable) {
        return service.lowStock(threshold, pageable);
    }

    // GET /api/products/paged-model?page=0&size=3   -> Spring's own PagedModel JSON shape
    @GetMapping("/paged-model")
    public Page<ProductResponse> pagedModel(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return service.pageForPagedModel(pageable);
    }
}
