package com.foodkart.menu_service.service;

import com.foodkart.menu_service.dto.MenuItemResponseDTO;
import com.foodkart.menu_service.entity.MenuItem;
import com.foodkart.menu_service.repository.MenuItemRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;

    @Override
    public MenuItemResponseDTO getMenuItemById(Long id) {

        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Menu item not found with id: " + id)
                );

        return MenuItemResponseDTO.builder()
                .id(menuItem.getId())
                .restaurantId(menuItem.getRestaurantId())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .price(menuItem.getPrice())
                .category(menuItem.getCategory())
                .available(menuItem.getAvailable())
                .build();
    }
}