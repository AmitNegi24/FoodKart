package com.foodkart.order_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuItemDTO {

    private Long restaurantId;
    private Long foodItemId;
    private String menuCategory;
    private FoodItemDTO foodItem;
}