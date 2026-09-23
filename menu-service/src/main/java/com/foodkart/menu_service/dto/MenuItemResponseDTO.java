package com.foodkart.menu_service.dto;

import com.foodkart.menu_service.model.FoodItemCategory;
import com.foodkart.menu_service.model.MenuCategory;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponseDTO {

    private Long restaurantId;
    private MenuCategory menuCategory;
    private FoodItemDTO foodItem;

}