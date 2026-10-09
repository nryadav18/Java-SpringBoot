package com.course.jparelationships.controller;

import com.course.jparelationships.dto.Dtos.ProductResponse;
import com.course.jparelationships.repository.ProductRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
 * ProductController - just lists the catalogue so you know which product ids to order.
 * (A controller calling a repository directly is OK for a trivial read-only list;
 *  anything with logic belongs in a service.)
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<ProductResponse> list() {
        return repository.findAll().stream().map(ProductResponse::from).toList();
    }
}
