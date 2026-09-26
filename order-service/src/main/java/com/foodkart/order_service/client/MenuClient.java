package com.foodkart.order_service.client;

import com.foodkart.order_service.dto.MenuItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "menu-service",
        url = "${menu.service.url:http://localhost:8082}",
        configuration = FeignClientConfig.class
)
public interface MenuClient {

    @GetMapping("/menu-items/food-items/{foodItemId}")
    MenuItemDTO getMenuItemById(
            @PathVariable("foodItemId") Long foodItemId
    );
}