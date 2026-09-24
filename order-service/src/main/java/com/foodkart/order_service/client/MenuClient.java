package com.foodkart.order_service.client;

import com.foodkart.order_service.dto.MenuItemDTO;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "menu-service", url = "${menu.service.url:http://localhost:8083}")
public interface MenuClient {

    @GetMapping("/menu-items/{id}")
    MenuItemDTO getMenuItemById(@PathVariable("id") @NotNull(message = "Food ID is required") Long menuItemId);
}