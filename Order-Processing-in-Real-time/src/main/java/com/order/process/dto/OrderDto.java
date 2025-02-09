package com.order.process.dto;

import com.order.process.entity.Product;
import lombok.Data;
import java.util.List;

@Data
public class OrderDto {
    private Long userId;
    private List<Product> productDto;

}
