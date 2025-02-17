package com.inventory.repository;

import com.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductId(Long productId);

    List<Product> findAllByProductIdIn(List<Long> productId);

    @Query(value = "select product_id, quantity from product where oid=:oid", nativeQuery = true)
    List<Object[]> findAllByOid(Long oid);
}
