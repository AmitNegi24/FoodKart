package com.foodkart.menu_service.dto;

import com.foodkart.menu_service.model.FoodItemCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemRequestDTO {

        @NotBlank(message = "Food item name is required")
        @Size(max = 100, message = "Food item name cannot exceed 100 characters")
        private String name;

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        private String description;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        private BigDecimal price;

        @NotNull(message = "Food item category is required")
        private FoodItemCategory category;

        @NotNull(message = "Availability is required")
        private Boolean available;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
}