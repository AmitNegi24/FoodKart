package com.foodkart.order_service.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {

    private Long menuItemId;

    private String itemName;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal subtotal;
}