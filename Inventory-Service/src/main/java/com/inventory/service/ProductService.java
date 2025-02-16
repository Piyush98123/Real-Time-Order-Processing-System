package com.inventory.service;

import com.inventory.entity.Product;

import java.util.List;

public interface ProductService {
    String addProduct(List<Product> productList);

    Product getDetail(Long productId);

    String updateProduct(List<Product> productList);
}
