package com.course.jpabasics.service;

import com.course.jpabasics.dto.ProductRequest;
import com.course.jpabasics.dto.ProductResponse;
import com.course.jpabasics.entity.Product;
import com.course.jpabasics.exception.DuplicateResourceException;
import com.course.jpabasics.exception.ResourceNotFoundException;
import com.course.jpabasics.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * ProductService - business logic layer.
 * --------------------------------------
 * - talks to the repository (never the controller directly to the database),
 * - converts entities <-> DTOs,
 * - throws meaningful exceptions.
 *
 * @Transactional (full story in topic 42): every public method runs inside ONE database transaction.
 * readOnly = true for reads is a small performance hint for Hibernate.
 */
@Service
@Transactional(readOnly = true)   // default for all methods in this class ...
public class ProductService {

    private final ProductRepository repository;

    // Constructor injection (topic 24) - Spring passes the auto-generated repository proxy here.
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<ProductResponse> findAll() {
        return repository.findAll().stream()        // SELECT * FROM products
                .map(ProductResponse::from)
                .toList();
    }

    public ProductResponse findById(Long id) {
        return ProductResponse.from(getProductOrThrow(id));
    }

    public long count() {
        return repository.count();                  // SELECT COUNT(*) FROM products
    }

    @Transactional                                  // ... but writing methods need a read-write transaction
    public ProductResponse create(ProductRequest request) {
        if (repository.existsBySku(request.sku())) {
            throw new DuplicateResourceException("A product with SKU '" + request.sku() + "' already exists");
        }
        Product product = new Product(request.name(), request.description(), request.sku(),
                request.price(), request.stock());   // TRANSIENT: just a Java object, JPA does not know it yet
        Product saved = repository.save(product);    // INSERT -> now MANAGED and has an id
        return ProductResponse.from(saved);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getProductOrThrow(id);     // MANAGED entity loaded inside this transaction

        if (!product.getSku().equals(request.sku()) && repository.existsBySku(request.sku())) {
            throw new DuplicateResourceException("A product with SKU '" + request.sku() + "' already exists");
        }
        product.setName(request.name());
        product.setDescription(request.description());
        product.setSku(request.sku());
        product.setPrice(request.price());
        product.setStock(request.stock());

        // We do NOT even need repository.save(product) here!
        // The entity is MANAGED, so at commit Hibernate compares it with the original copy
        // ("dirty checking") and sends an UPDATE automatically. We call saveAndFlush only so the
        // @UpdateTimestamp value is already filled in the response we return.
        return ProductResponse.from(repository.saveAndFlush(product));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {            // SELECT COUNT(*) ... WHERE id = ?
            throw new ResourceNotFoundException("Product", id);
        }
        repository.deleteById(id);                   // DELETE FROM products WHERE id = ?
    }

    private Product getProductOrThrow(Long id) {
        // findById returns Optional<Product> (topic 14) - empty when no row has that id.
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }
}
