package com.foodkart.order_service.entity;

public enum OutboxStatus {
    PROCESSED,
    PENDING,
    PUBLISHED,
    FAILED
}