package com.order.dto;

import com.order.entity.Product;
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

   public Long getOrderId() {
      return orderId;
   }

   public void setOrderId(Long orderId) {
      this.orderId = orderId;
   }

   public Long getUserId() {
      return userId;
   }

   public void setUserId(Long userId) {
      this.userId = userId;
   }

   public Double getTotalAmount() {
      return totalAmount;
   }

   public void setTotalAmount(Double totalAmount) {
      this.totalAmount = totalAmount;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public LocalDateTime getLocalDateTime() {
      return localDateTime;
   }

   public void setLocalDateTime(LocalDateTime localDateTime) {
      this.localDateTime = localDateTime;
   }

   public List<Product> getItems() {
      return items;
   }

   public void setItems(List<Product> items) {
      this.items = items;
   }
}
