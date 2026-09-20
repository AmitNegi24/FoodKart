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

    private Long id;
    private Long restaurantId;
    private MenuCategory menuCategory;
    private String foodItemName;
    private String foodItemDescription;
    private BigDecimal foodItemPrice;
    private FoodItemCategory foodItemCategory;
    private Boolean foodItemAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}