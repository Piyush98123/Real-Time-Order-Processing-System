package com.inventory.service.impl;

import com.inventory.entity.Product;
import com.inventory.repository.ProductRepository;
import com.inventory.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public String addProduct(List<Product> productList) {
        productRepository.saveAll(productList);
        // push the event to kafka

        return "Inventory updated";
    }

    @Override
    public Product getDetail(Long productId) {
        return productRepository.findByProductId(productId).orElseThrow(() -> new RuntimeException( "No product found"));
    }

    @Override
    public String updateProduct(List<Product> productList) {
        List<Long> productId = productList.stream().map(Product::getProductId).collect(Collectors.toList());
        List<Product> existingProduct = productRepository.findAllByProductIdIn(productId);
        existingProduct.forEach(product -> {
            productList.stream().filter(p->p.getProductId().equals(product.getProductId())).findFirst().ifPresent(updatedProduct ->{
                product.setAvailable(updatedProduct.getAvailable()+product.getAvailable());
            });
        });
        productRepository.saveAll(existingProduct);
        // push update event to kafka

        return "Product stocks updated";
    }
}
