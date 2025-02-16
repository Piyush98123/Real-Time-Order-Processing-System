package com.order.service.impl;

import com.order.dto.OrderDto;
import com.order.dto.OrderResponse;
import com.order.entity.Order;
import com.order.entity.OrderStatus;
import com.order.repository.OrderRepository;
import com.order.service.OrderService;
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


}
