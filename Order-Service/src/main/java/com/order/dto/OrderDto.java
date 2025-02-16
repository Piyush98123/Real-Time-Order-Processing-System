package com.order.dto;

import com.order.entity.Product;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto {
    private Long userId;
    private List<Product> productDto;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Product> getProductDto() {
        return productDto;
    }

    public void setProductDto(List<Product> productDto) {
        this.productDto = productDto;
    }
}
