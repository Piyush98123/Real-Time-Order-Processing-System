package com.order.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.dto.OrderDto;
import com.order.dto.OrderResponse;
import com.order.entity.Order;
import com.order.entity.OrderStatus;
import com.order.entity.Product;
import com.order.repository.OrderRepository;
import com.order.service.OrderService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


@Service
public class OrderServiceImpl implements OrderService {


    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    @Override
    @Transactional
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
        orderRepository.save(order);
        sendOrderInfo(order);
        String msg="";
        AtomicBoolean isOrderFailed= new AtomicBoolean(false);
        orderRepository.findById(order.getId()).ifPresent(ord->{
            if(ord.getStatus().equalsIgnoreCase(OrderStatus.FAILED.name())){
                isOrderFailed.set(true);
            }
        });
        if(isOrderFailed.get()){
            msg="Product is out of stock order is failed to created";
        }
        else{
            msg="Order placed successfully " +orderId;
        }
        return msg;
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

    public boolean sendOrderInfo(Order order){
        log.info(order+" sent to kafka topic");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonString = objectMapper.writeValueAsString(order);
            System.out.println("JSON Output: " + jsonString);
            this.kafkaTemplate.send("order", jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        order.setStatus(OrderStatus.PROCESSING.name());
        orderRepository.save(order);
        return true;
    }


}
