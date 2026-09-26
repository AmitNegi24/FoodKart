package com.foodkart.order_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FoodItemDTO {

    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private Boolean available;
}
