package com.order.controller;


import com.order.dto.OrderDto;
import com.order.dto.OrderResponse;
import com.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/")
public class OrderController {

    @Autowired
    OrderService orderService;


    @PostMapping("create")
    public ResponseEntity<String> createOrder(@RequestBody OrderDto orderDto){
        return ResponseEntity.ok(orderService.createOrder(orderDto));
    }

    @GetMapping("orderDetail/{orderId}")
    public ResponseEntity<OrderResponse> findOrderDetail(@PathVariable(required = false) Long orderId){
        return ResponseEntity.ok(orderService.findOrderDetail(orderId));
    }


}
