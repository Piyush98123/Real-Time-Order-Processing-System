package com.order.process.service;

import com.order.process.dto.OrderDto;
import com.order.process.dto.OrderResponse;

public interface OrderService {


    String createOrder(OrderDto orderDto);

    OrderResponse findOrderDetail(Long orderId);
}
