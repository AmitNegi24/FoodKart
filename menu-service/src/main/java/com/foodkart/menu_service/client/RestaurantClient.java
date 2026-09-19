package com.foodkart.menu_service.client;

import com.foodkart.menu_service.dto.RestaurantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "restaurant-service",
        url = "${restaurant-service.url}"
)
public interface RestaurantClient {

    @GetMapping("/restaurants/{restaurantId}")
    RestaurantDTO getRestaurantById(
            @PathVariable Long restaurantId
    );
}