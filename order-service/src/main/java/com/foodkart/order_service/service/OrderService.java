package com.foodkart.order_service.service;

import com.foodkart.order_service.dto.OrderRequestDTO;
import com.foodkart.order_service.dto.OrderResponseDTO;

public interface OrderService {

    OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO, String correlationId);
    OrderResponseDTO getOrderById(Long id);
}