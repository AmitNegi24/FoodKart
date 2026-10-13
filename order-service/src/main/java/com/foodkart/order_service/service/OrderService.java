
package com.foodkart.order_service.service;

import com.foodkart.order_service.dto.OrderRequestDTO;
import com.foodkart.order_service.dto.OrderResponseDTO;
import com.foodkart.order_service.entity.OrderStatus;

public interface OrderService {

    OrderResponseDTO createOrder(
            OrderRequestDTO orderRequestDTO,
            String correlationId
    );

    OrderResponseDTO getOrderById(Long id);

    OrderResponseDTO updateOrderStatus(
            Long orderId,
            OrderStatus newStatus
    );
}
