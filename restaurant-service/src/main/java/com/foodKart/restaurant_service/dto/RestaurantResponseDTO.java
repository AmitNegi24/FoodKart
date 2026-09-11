package com.foodKart.restaurant_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String phone;
    private boolean active;
    private LocalDateTime createdAt;
}