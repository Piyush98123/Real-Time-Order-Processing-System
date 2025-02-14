package com.order.process.controller;


import com.order.process.dto.OrderDto;
import com.order.process.dto.OrderResponse;
import com.order.process.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/order/")
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
