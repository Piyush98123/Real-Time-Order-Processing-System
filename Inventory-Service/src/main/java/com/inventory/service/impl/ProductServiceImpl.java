package com.inventory.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.entity.Order;
import com.inventory.entity.OrderProduct;
import com.inventory.entity.Product;
import com.inventory.repository.OrderProductRepository;
import com.inventory.repository.OrderRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.service.ProductService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderProductRepository orderProductRepository;

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
    @Transactional
    public void fetchOrderFromKafka(String data){
       try{
           ObjectMapper objectMapper = new ObjectMapper();
           Order order = objectMapper.readValue(data, Order.class);
           log.info("fetched ordered data from kafka topic order"+order.toString());
           List<OrderProduct> results = orderProductRepository.findByOrderId(order.getId());
           log.info("results ->"+results);
           List<Long> productId = new ArrayList<>();
           results.stream().forEach(orderProduct -> productId.add(orderProduct.getProductId()));
           List<Product> productList = productRepository.findAllByProductIdIn(productId); // Inventory product
           log.info("fetched inventory product for these ids: "+productList);
           AtomicBoolean isOrderFailed= new AtomicBoolean(false);
           results.stream().forEach(m->{
               productList.stream().filter(product -> product.getProductId().equals(m.getProductId())).findFirst().ifPresent(product->{
                   if(product.getAvailable()<m.getQuantity()){
                       // call order db to update the status to failed and return
                       isOrderFailed.set(true);
                   }
                   else{
                       product.setAvailable(product.getAvailable()-m.getQuantity());
                   }
               });
           });
           if(isOrderFailed.get()) order.setStatus("FAILED");
           else order.setStatus("PENDING");
           log.info("current order status "+order.getStatus());
           orderRepository.updateOrderStatus(order.getOrderId(), order.getStatus());
           if(!isOrderFailed.get()) productRepository.saveAllAndFlush(productList);
           orderRepository.flush();
           sendOrderUpdatedStatus(order);
       }
       catch (Exception e){
           e.printStackTrace();
       }
    }

    public void sendOrderUpdatedStatus(Order order){
        log.info(order+" sent to kafka topic order-status");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonString = objectMapper.writeValueAsString(order.getStatus());
            log.info("Inventory JSON Output: " + jsonString);
            Thread.sleep(2000);
            this.kafkaTemplate.send("order-status", jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
