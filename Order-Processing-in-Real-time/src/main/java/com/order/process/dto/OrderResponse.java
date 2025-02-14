package com.order.process.dto;

import com.order.process.entity.Product;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse implements Serializable {

   private Long orderId;
   private Long userId;
   private Double totalAmount;
   private String status;
   private LocalDateTime localDateTime;
   List<Product> items;
}
