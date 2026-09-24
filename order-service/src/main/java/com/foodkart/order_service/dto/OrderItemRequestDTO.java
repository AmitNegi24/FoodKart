package com.foodkart.order_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequestDTO {

    @NotNull(message = "Food ID is required")
    private Long menuItemId;
    @Min(value = 1, message = "quantity cannot be less than 1")
    private Integer quantity;

    @Override
    public String toString() {
        return "OrderItemRequestDTO{" +
                "menuItemId=" + menuItemId +
                ", quantity=" + quantity +
                '}';
    }
}