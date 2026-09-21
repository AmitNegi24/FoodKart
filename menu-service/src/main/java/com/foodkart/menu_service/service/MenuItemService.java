package com.foodkart.menu_service.service;

import com.foodkart.menu_service.dto.MenuItemRequestDTO;
import com.foodkart.menu_service.dto.MenuItemResponseDTO;
import com.foodkart.menu_service.model.FoodItemCategory;
import com.foodkart.menu_service.model.MenuCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface MenuItemService {
    MenuItemResponseDTO getMenuItemByFoodItemId(Long foodItemId);
    MenuItemResponseDTO getMenuItemByRestaurantIdAndFoodItemId(Long restaurantId, Long foodItemId);
    Page<MenuItemResponseDTO> getAllMenuItemByRestaurantId(Long restaurantId, Pageable pageable);
    Page<MenuItemResponseDTO> getAllAvailableMenuItemByRestaurantId(Long restaurantId, Pageable pageable);
    Page<MenuItemResponseDTO> getAllMenuItemByRestaurantIdAndMenuCategory(Long restaurantId, MenuCategory menuCategory, Pageable pageable);
    Page<MenuItemResponseDTO> getAllAvailableMenuItemByRestaurantIdAndMenuCategory(Long restaurantId, MenuCategory menuCategory, Pageable pageable);
    Page<MenuItemResponseDTO> getAllMenuItemByRestaurantIdAndFoodItemCategory(Long restaurantId, FoodItemCategory FoodItemCategory, Pageable pageable);
    Page<MenuItemResponseDTO> getAllAvailableMenuItemByRestaurantIdAndFoodItemCategory(Long restaurantId, FoodItemCategory foodItemCategory, Pageable pageable);
    Page<MenuItemResponseDTO> getAllMenuItemByRestaurantIdAndMenuCategoryAndFoodItemCategory(Long restaurantId, MenuCategory menuCategory, FoodItemCategory foodItemCategory, Pageable pageable);
    Page<MenuItemResponseDTO> getAllAvailableMenuItemByRestaurantIdAndMenuCategoryAndFoodItemCategory(Long restaurantId, MenuCategory menuCategory, FoodItemCategory foodItemCategory, Pageable pageable);
    MenuItemResponseDTO createMenuItem(MenuItemRequestDTO request);
    MenuItemResponseDTO updateMenuItem(Long restaurantId, Long menuId, MenuItemRequestDTO request);
    void deleteMenuItem(Long restaurantId, Long menuId);
    Page<MenuItemResponseDTO> getAllMenuItemByRestaurantIdAndPriceRange(Long restaurantId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}
