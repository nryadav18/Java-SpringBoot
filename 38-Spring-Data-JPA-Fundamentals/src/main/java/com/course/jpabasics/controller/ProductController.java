package com.course.jpabasics.controller;

import com.course.jpabasics.dto.ProductRequest;
import com.course.jpabasics.dto.ProductResponse;
import com.course.jpabasics.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

/*
 * ProductController - the web layer. It only deals with HTTP: URLs, status codes, JSON.
 * All the real work is delegated to ProductService.
 *
 *   GET    /api/products          -> list all
 *   GET    /api/products/count    -> how many
 *   GET    /api/products/{id}     -> one product (404 if missing)
 *   POST   /api/products          -> create (201 + Location header)
 *   PUT    /api/products/{id}     -> replace / update
 *   DELETE /api/products/{id}     -> delete (204)
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/count")
    public Map<String, Long> count() {
        return Map.of("count", service.count());
    }

    @GetMapping("/{id}")
    public ProductResponse getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);   // 201 Created
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();               // 204 No Content
    }
}
