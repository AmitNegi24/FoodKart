package com.foodkart.menu_service.dto;

import com.foodkart.menu_service.model.FoodItemCategory;
import com.foodkart.menu_service.model.MenuCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemRequestDTO {

    @NotNull
    private Long restaurantId;

    @NotNull(message = "Menu category is required")
    private MenuCategory menuCategory;

    @NotBlank
    @Size(max = 100)
    private String foodItemName;

    @Size(max = 500)
    private String foodItemDescription;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal foodItemPrice;

    @NotNull(message = "Food item category is required")
    private FoodItemCategory foodItemCategory;

    @NotNull
    private Boolean foodItemAvailable;

}