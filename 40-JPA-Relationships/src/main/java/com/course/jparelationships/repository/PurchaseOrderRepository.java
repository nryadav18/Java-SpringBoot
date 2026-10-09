package com.course.jparelationships.repository;

import com.course.jparelationships.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    // Derived query THROUGH a relationship: orders whose customer has this id.
    // SQL: ... FROM orders o WHERE o.customer_id = ?
    List<PurchaseOrder> findByCustomerId(Long customerId);

    // Even deeper: orders that contain a product with this name (joins order_products + products).
    List<PurchaseOrder> findByProductsName(String productName);
}
