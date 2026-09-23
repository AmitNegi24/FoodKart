package com.foodkart.menu_service.controller;

import com.foodkart.menu_service.dto.MenuItemRequestDTO;
import com.foodkart.menu_service.dto.MenuItemResponseDTO;
import com.foodkart.menu_service.model.FoodItemCategory;
import com.foodkart.menu_service.model.MenuCategory;
import com.foodkart.menu_service.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private static final Logger log = LoggerFactory.getLogger(MenuItemController.class);
    private final MenuItemService menuItemService;

    @GetMapping("/food-items/{foodItemId}")
    public ResponseEntity<MenuItemResponseDTO> getMenuItemByFoodItemId(
            @PathVariable Long foodItemId) {

        MenuItemResponseDTO response =
                menuItemService.getMenuItemByFoodItemId(foodItemId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurants/{restaurantId}/food-items/{foodItemId}")
    public ResponseEntity<MenuItemResponseDTO> getMenuItemByRestaurantIdAndFoodItemId(
            @PathVariable Long restaurantId, Long foodItemId) {

        MenuItemResponseDTO response = menuItemService.getMenuItemByRestaurantIdAndFoodItemId(restaurantId, foodItemId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<Page<MenuItemResponseDTO>> getAllMenuItemByRestaurantId(
            @PathVariable Long restaurantId, Pageable pageable) {

        Page<MenuItemResponseDTO> response = menuItemService.getAllMenuItemByRestaurantId(restaurantId, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurants/{restaurantId}/available")
    public ResponseEntity<Page<MenuItemResponseDTO>> getAllAvailableMenuItemByRestaurantId(
            @PathVariable Long restaurantId, Pageable pageable){

        Page<MenuItemResponseDTO> response = menuItemService.getAllAvailableMenuItemByRestaurantId(restaurantId, pageable);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/restaurants/{restaurantId}/menu-category/{menuCategory}")
    public ResponseEntity<Page<MenuItemResponseDTO>> getAllMenuItemByRestaurantIdAndMenuCategory(
            @PathVariable Long restaurantId, @PathVariable MenuCategory menuCategory, Pageable pageable){

            Page<MenuItemResponseDTO> response = menuItemService.getAllMenuItemByRestaurantIdAndMenuCategory(restaurantId, menuCategory, pageable);

            return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurants/{restaurantId}/menu-category/{menuCategory}/available")
    public ResponseEntity<Page<MenuItemResponseDTO>> getAllAvailableMenuItemByRestaurantIdAndMenuCategory(
            @PathVariable Long restaurantId, @PathVariable MenuCategory menuCategory, Pageable pageable){

        Page<MenuItemResponseDTO> response = menuItemService.getAllAvailableMenuItemByRestaurantIdAndMenuCategory(restaurantId, menuCategory, pageable);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/restaurants/{restaurantId}/food-item-category/{foodItemCategory}")
    public ResponseEntity<Page<MenuItemResponseDTO>> getAllMenuItemByRestaurantIdAndFoodItemCategory(
            @PathVariable Long restaurantId, @PathVariable FoodItemCategory foodItemCategory, Pageable pageable){

        Page<MenuItemResponseDTO> response = menuItemService.getAllMenuItemByRestaurantIdAndFoodItemCategory(restaurantId, foodItemCategory, pageable);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/restaurants/{restaurantId}/food-item-category/{foodItemCategory}/available")
    public ResponseEntity<Page<MenuItemResponseDTO>> getAllAvailableMenuItemByRestaurantIdAndFoodItemCategory(
            @PathVariable Long restaurantId, @PathVariable FoodItemCategory foodItemCategory, Pageable pageable){

        Page<MenuItemResponseDTO> response = menuItemService.getAllAvailableMenuItemByRestaurantIdAndFoodItemCategory(restaurantId, foodItemCategory, pageable);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/restaurants/{restaurantId}/menu-category/{menuCategory}/food-item-category/{foodItemCategory}/")
    public ResponseEntity<Page<MenuItemResponseDTO>> getAllMenuItemByRestaurantIdAndMenuCategoryAndFoodItemCategory(
            @PathVariable Long restaurantId, @PathVariable MenuCategory menuCategory, @PathVariable FoodItemCategory foodItemCategory, Pageable pageable){

        Page<MenuItemResponseDTO> response = menuItemService.getAllMenuItemByRestaurantIdAndMenuCategoryAndFoodItemCategory(restaurantId, menuCategory, foodItemCategory, pageable);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/restaurants/{restaurantId}/menu-category/{menuCategory}/food-item-category/{foodItemCategory}/available")
    public ResponseEntity<Page<MenuItemResponseDTO>> getAllAvailableMenuItemByRestaurantIdAndMenuCategoryAndFoodItemCategory(
            @PathVariable Long restaurantId, @PathVariable MenuCategory menuCategory, @PathVariable FoodItemCategory foodItemCategory, Pageable pageable){

        Page<MenuItemResponseDTO> response = menuItemService.getAllAvailableMenuItemByRestaurantIdAndMenuCategoryAndFoodItemCategory(restaurantId, menuCategory, foodItemCategory, pageable);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/restaurants/{restaurantId}/by-price")
    public ResponseEntity<Page<MenuItemResponseDTO>> getAllMenuItemByRestaurantIdAndPriceRange(
            @PathVariable Long restaurantId,
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            Pageable pageable) {

        Page<MenuItemResponseDTO> response =
                menuItemService.getAllMenuItemByRestaurantIdAndPriceRange(restaurantId, minPrice, maxPrice, pageable);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/create")
    public ResponseEntity<MenuItemResponseDTO> createMenuItem(
            @Valid @RequestBody MenuItemRequestDTO request) {
        log.info("Creating menu item: {}", request);
        MenuItemResponseDTO response =
                menuItemService.createMenuItem(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/update/restaurants/{restaurantId}/food-items/{foodItemId}")
    public ResponseEntity<MenuItemResponseDTO> updateMenuItem(
            @PathVariable Long restaurantId, @PathVariable Long foodItemId,
            @Valid @RequestBody  MenuItemRequestDTO request){

        MenuItemResponseDTO response =
                menuItemService.updateMenuItem(restaurantId, foodItemId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);

    }

    @DeleteMapping("/delete/restaurants/{restaurantId}/food-items/{foodItemId}")
    public ResponseEntity<Void> deleteMenuItem(
            @PathVariable Long restaurantId, @PathVariable Long foodItemId){

        menuItemService.deleteMenuItem(restaurantId, foodItemId);

        return ResponseEntity.noContent().build();
    }
}