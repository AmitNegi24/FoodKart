package com.foodkart.menu_service.dto;

import com.foodkart.menu_service.model.FoodItemCategory;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemDTO {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private FoodItemCategory category;
        private Boolean available;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
}
