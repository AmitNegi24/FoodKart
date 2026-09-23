package com.foodkart.menu_service.dto;

import com.foodkart.menu_service.model.FoodItemCategory;
import com.foodkart.menu_service.model.MenuCategory;
import jakarta.validation.Valid;
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

    @NotNull(message = "Food item is required")
    @Valid
    private FoodItemRequestDTO foodItem;

}