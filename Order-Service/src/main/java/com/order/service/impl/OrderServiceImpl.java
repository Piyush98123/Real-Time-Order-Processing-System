package com.order.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.dto.OrderDto;
import com.order.dto.OrderResponse;
import com.order.entity.Order;
import com.order.entity.OrderStatus;
import com.order.entity.Product;
import com.order.repository.OrderRepository;
import com.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class OrderServiceImpl implements OrderService {


    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    static String msg="Time out ";


    @Override
    public String createOrder(OrderDto orderDto) {
        log.info("calling inventory service");
        // take productId from orderDto and call inventory service to find stock
        Order order = new Order();
        long orderId = Instant.now().toEpochMilli() % 1000000;
        order.setOrderId(orderId);
        List<Product> items = new ArrayList<>();
        order.setStatus(OrderStatus.CREATED.name());
        order.setTotalAmount(0.0); // need to calculate from inventory service
        order.setUserId(orderDto.getUserId());
        order.setLocalDateTime(LocalDateTime.now());
        orderDto.getProductDto().forEach(prod -> {
            Product product = new Product();
            product.setProductId(prod.getProductId());
            product.setProductName(prod.getProductName());
            product.setCategory(prod.getCategory());
            product.setQuantity(prod.getQuantity());
            product.setOrder(order);
            items.add(product);
        });
        order.setItems(items);
        orderRepository.saveAndFlush(order);
        sendOrderInfo(order);
        log.info("Order placed successfully. Waiting for inventory check.");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return msg+orderId;

    }

    @Override
    public OrderResponse findOrderDetail(Long orderId) {
        OrderResponse order = new OrderResponse();
        orderRepository.findById(orderId).ifPresent(order1 -> {
            order.setItems(order1.getItems());
            order.setLocalDateTime(order1.getLocalDateTime());
            order.setStatus(order1.getStatus());
            order.setTotalAmount(order.getTotalAmount());
            order.setOrderId(order1.getOrderId());
        });
        return order;

    }

    public void sendOrderInfo(Order order){
        log.info(order+" sent to kafka topic order");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonString = objectMapper.writeValueAsString(order);
            log.info("JSON Output: " + jsonString);
            Thread.sleep(2000);
            this.kafkaTemplate.send("order", jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "order-status", groupId = "order_inventory_status")
    public void pollOrderStatus(String data) {
        String status="";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
             status = objectMapper.readValue(data, String.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("consuming topic order-status "+status);
        if (status.trim().endsWith("\"FAILED\"")) {
            msg= "Product is out of stock. Order failed! ";
            }
        else {
                msg= "Order placed successfully! Order ID: ";
            }
        log.info(msg);
    }

}
