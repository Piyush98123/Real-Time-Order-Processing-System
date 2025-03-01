package com.inventory.repository;

import com.inventory.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

    @Query("SELECT op FROM OrderProduct op WHERE op.orderId = :orderId")
    List<OrderProduct> findByOrderId(Long orderId);
}
