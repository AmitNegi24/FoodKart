package com.foodkart.order_service.client;

import com.foodkart.order_service.dto.MenuItemDTO;
import jakarta.validation.constraints.NotNull;


public interface MenuClient {

    MenuItemDTO getMenuItemById(@NotNull(message = "Food ID is required") Long menuItemId);
}