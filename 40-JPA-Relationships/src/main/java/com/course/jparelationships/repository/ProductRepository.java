package com.course.jparelationships.repository;

import com.course.jparelationships.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
