package com.foodKart.restaurant_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantRequestDTO {

    @NotBlank(message = "Restaurant name is required")
    @Size(max = 100, message = "Restaurant name cannot exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phone;
}