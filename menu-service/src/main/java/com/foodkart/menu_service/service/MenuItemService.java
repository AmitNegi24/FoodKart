package com.foodkart.menu_service.service;

import com.foodkart.menu_service.dto.MenuItemResponseDTO;

public interface MenuItemService {
    MenuItemResponseDTO getMenuItemById(Long id);
}
