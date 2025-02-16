package com.order.service;

import com.order.dto.OrderDto;
import com.order.dto.OrderResponse;

public interface OrderService {


    String createOrder(OrderDto orderDto);

    OrderResponse findOrderDetail(Long orderId);
}
