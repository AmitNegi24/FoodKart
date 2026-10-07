package com.foodkart.order_service.client;

import com.foodkart.order_service.dto.MenuItemDTO;
import org.springframework.stereotype.Component;

@Component
public class MenuClientFallBack implements MenuClient {

    @Override
    public MenuItemDTO getMenuItemById(Long id) {

        throw new RuntimeException(
                "Menu Service is currently unavailable. Please try again later."
        );
    }
}