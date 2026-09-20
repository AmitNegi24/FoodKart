package com.foodkart.menu_service.controller;

import com.foodkart.menu_service.dto.MenuItemRequestDTO;
import com.foodkart.menu_service.dto.MenuItemResponseDTO;
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

    @GetMapping("/{menuId}")
    public ResponseEntity<MenuItemResponseDTO> getMenuItemById(
            @PathVariable Long menuId) {

        MenuItemResponseDTO response =
                menuItemService.getMenuItemByMenuId(menuId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<Page<MenuItemResponseDTO>> getAllMenuItemByRestaurantId(
            @PathVariable Long restaurantId, Pageable pageable) {

        Page<MenuItemResponseDTO> response = menuItemService.getAllMenuItemByRestaurantId(restaurantId, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurants/{restaurantId}/{category}")
    public ResponseEntity<Page<MenuItemResponseDTO>> getAllMenuItemByRestaurantIdAndCategory(
            @PathVariable Long restaurantId, @PathVariable String category, Pageable pageable){

        Page<MenuItemResponseDTO> response = menuItemService.getAllMenuItemByRestaurantIdAndCategory(restaurantId, category, pageable);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/restaurants/{restaurantId}/{category}/available")
    public ResponseEntity<Page<MenuItemResponseDTO>> getAllAvailableMenuItemByRestaurantIdAndCategory(
            @PathVariable Long restaurantId, @PathVariable String category, Pageable pageable){

        Page<MenuItemResponseDTO> response = menuItemService.getAllAvailableMenuItemByRestaurantIdAndCategory(restaurantId, category, pageable);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/restaurants/{restaurantId}/available")
    public ResponseEntity<Page<MenuItemResponseDTO>> getAvailableMenuItemByRestaurantId(
            @PathVariable Long restaurantId, Pageable pageable){

        Page<MenuItemResponseDTO> response = menuItemService.getAvailableMenuItemByRestaurantId(restaurantId, pageable);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/restaurants/{restaurantId}/by-price")
    public ResponseEntity<Page<MenuItemResponseDTO>> getMenuItemsByPriceRange(
            @PathVariable Long restaurantId,
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            Pageable pageable) {

        Page<MenuItemResponseDTO> response =
                menuItemService.getMenuItemsByRestaurantAndPriceRange(restaurantId, minPrice, maxPrice, pageable);

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

    @PutMapping("/update/{restaurantId}/{menuId}")
    public ResponseEntity<MenuItemResponseDTO> updateMenuItem(
            @PathVariable Long restaurantId, @PathVariable Long menuId,
            @Valid @RequestBody  MenuItemRequestDTO request){

        MenuItemResponseDTO response =
                menuItemService.updateMenuItem(restaurantId, menuId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);

    }

    @DeleteMapping("/delete/{restaurantId}/{menuId}")
    public ResponseEntity<Void> deleteMenuItem(
            @PathVariable Long restaurantId, @PathVariable Long menuId){

        menuItemService.deleteMenuItem(restaurantId, menuId);

        return ResponseEntity.noContent().build();
    }
}