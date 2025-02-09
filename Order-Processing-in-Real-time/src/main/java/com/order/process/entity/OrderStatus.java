package com.order.process.entity;


import lombok.Getter;

@Getter
public enum OrderStatus {
    CREATED, PENDING, PROCESSING, COMPLETED, FAILED;
}
