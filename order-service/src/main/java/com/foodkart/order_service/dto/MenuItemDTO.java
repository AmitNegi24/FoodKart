package com.foodkart.order_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MenuItemDTO {

    private Long id;
    private Long restaurantId;
    private String name;
    private BigDecimal price;
    private boolean available;
}