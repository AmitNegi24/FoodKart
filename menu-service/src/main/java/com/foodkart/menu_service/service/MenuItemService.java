package com.foodkart.menu_service.service;

import com.foodkart.menu_service.dto.MenuItemRequestDTO;
import com.foodkart.menu_service.dto.MenuItemResponseDTO;
import com.foodkart.menu_service.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface MenuItemService {
    MenuItemResponseDTO getMenuItemByMenuId(Long menuId);
//    Page<MenuItemResponseDTO> getMenuItemByRestaurantIdAndId(Long restaurantId, Long Id);
    Page<MenuItemResponseDTO> getAllMenuItemByRestaurantId(Long restaurantId, Pageable pageable);
    Page<MenuItemResponseDTO> getAllMenuItemByRestaurantIdAndCategory(Long restaurantId,String category, Pageable pageable);
    Page<MenuItemResponseDTO> getAllAvailableMenuItemByRestaurantIdAndCategory(Long restaurantId,String category, Pageable pageable);
    MenuItemResponseDTO createMenuItemWithRestaurantId(MenuItemRequestDTO request, Long restaurantId);
    MenuItemResponseDTO updateMenuItem(Long restaurantId, Long menuId, MenuItemRequestDTO request);
    void deleteMenuItem(Long restaurantId, Long menuId);
    Page<MenuItemResponseDTO> getAvailableMenuItemByRestaurantId(Long restaurantId, Pageable pageable);
    Page<MenuItemResponseDTO> getMenuItemsByRestaurantAndPriceRange(Long restaurantId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}
