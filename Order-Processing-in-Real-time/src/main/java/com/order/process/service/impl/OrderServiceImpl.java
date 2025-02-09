package com.order.process.service.impl;

import com.order.process.dto.OrderDto;
import com.order.process.entity.Order;
import com.order.process.entity.OrderStatus;
import com.order.process.repository.OrderRepository;
import com.order.process.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;


@Slf4j
@Service
public class OrderServiceImpl implements OrderService {


    @Autowired
    OrderRepository orderRepository;


    @Override
    public String createOrder(OrderDto orderDto) {
        // take productId from orderDto and call inventory service to find stock
        Order order = new Order();
        long orderId = Instant.now().toEpochMilli() % 1000000;
        order.setOrderId(orderId);
        order.setItems(orderDto.getProductDto());
        order.setStatus(OrderStatus.CREATED.name());
        order.setTotalAmount(0.0); // need to calculate from inventory service
        order.setUserId(orderDto.getUserId());
        order.setLocalDateTime(LocalDateTime.now());
        orderRepository.save(order);
        return "Order placed successfully "+orderId;
    }
}
