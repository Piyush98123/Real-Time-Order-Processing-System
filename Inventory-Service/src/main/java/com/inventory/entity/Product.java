package com.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "inventory_product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;
    private Long productId;
    private String productName;
    private String category;
    private Integer available;

    @Override
    public String toString() {
        return "Product{" +
                "pid=" + pid +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", category='" + category + '\'' +
                ", available=" + available +
                '}';
    }

    public Long getId() {
        return pid;
    }

    public void setId(Long id) {
        this.pid = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }
}

