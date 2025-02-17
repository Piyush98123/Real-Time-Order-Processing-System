package com.inventory.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.entity.Order;
import com.inventory.entity.Product;
import com.inventory.repository.ProductRepository;
import com.inventory.service.ProductService;
import jakarta.persistence.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public String addProduct(List<Product> productList) {
        productRepository.saveAll(productList);
        // push the event to kafka
        kafkaTemplate.send("inventory","new product inserted");
        return "product inserted in inventory";
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
        kafkaTemplate.send("inventory","product stock updated");
        return "Product stocks updated";
    }

    @KafkaListener(topics = "order", groupId = "OrderProduct")
    public void fetchOrder(String data){
       try{
           ObjectMapper objectMapper = new ObjectMapper();
           Order order = objectMapper.readValue(data, Order.class);
           log.info("fetched ordered data from kafka topic order");
           List<Object[]> results = productRepository.findAllByOid(order.getOid());
           Map<Long,Integer> map = results.stream()
                   .collect(Collectors.toMap(
                           row -> ((Number) row[0]).longValue(),  // Convert to Long
                           row -> ((Number) row[1]).intValue()    // Convert to Integer
                   ));
           List<Long> productId = new ArrayList<>(map.keySet());
           List<Product> productList = productRepository.findAllByProductIdIn(productId);
           map.entrySet().stream().forEach(m->{
               productList.stream().filter(product -> product.getProductId().equals(m.getKey())).findFirst().ifPresent(prod->{
                   if(prod.getAvailable()<m.getValue()){
                       // call order db to update the status to failed and return
                   }
                   else{
                       prod.setAvailable(prod.getAvailable()-m.getValue());
                   }
               });
           });


       }
       catch (Exception e){
           e.printStackTrace();
       }
    }
}
