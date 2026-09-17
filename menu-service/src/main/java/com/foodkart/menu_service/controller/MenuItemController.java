package com.foodkart.menu_service.controller;

import com.foodkart.menu_service.dto.MenuItemResponseDTO;
import com.foodkart.menu_service.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponseDTO> getMenuItem(
            @PathVariable Long id) {

        MenuItemResponseDTO response =
                menuItemService.getMenuItemById(id);

        return ResponseEntity.ok(response);
    }
}