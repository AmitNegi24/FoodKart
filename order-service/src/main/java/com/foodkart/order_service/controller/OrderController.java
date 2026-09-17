package com.foodkart.order_service.controller;

import com.foodkart.order_service.dto.OrderRequestDTO;
import com.foodkart.order_service.dto.OrderResponseDTO;
import com.foodkart.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @RequestBody OrderRequestDTO orderRequestDTO) {

        OrderResponseDTO savedOrder = orderService.createOrder(orderRequestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedOrder);
    }
}