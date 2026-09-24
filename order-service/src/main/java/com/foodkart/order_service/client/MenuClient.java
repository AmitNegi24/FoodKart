package com.foodkart.order_service.client;

import com.foodkart.order_service.dto.MenuItemDTO;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "menu-service", url = "${menu.service.url:http://localhost:8083}")
public interface MenuClient {

    @GetMapping("/menu-items/food-items/{foodItemId}")
    MenuItemDTO getMenuItemById(@PathVariable("foodItemId") @NotNull(message = "Food ID is required") Long foodItemId);
}