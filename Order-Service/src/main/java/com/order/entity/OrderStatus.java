package com.order.entity;


import lombok.Getter;

@Getter
public enum OrderStatus {
    CREATED, PENDING, PROCESSING, COMPLETED, FAILED;
}
