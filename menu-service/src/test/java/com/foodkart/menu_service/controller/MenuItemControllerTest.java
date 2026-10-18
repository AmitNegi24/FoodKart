package com.foodkart.menu_service.controller;

import com.foodkart.menu_service.dto.MenuItemRequestDTO;
import com.foodkart.menu_service.dto.MenuItemResponseDTO;
import com.foodkart.menu_service.model.FoodItemCategory;
import com.foodkart.menu_service.model.MenuCategory;
import com.foodkart.menu_service.service.MenuItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuItemControllerTest {

    @Mock
    private MenuItemService menuItemService;

    @InjectMocks
    private MenuItemController menuItemController;

    private MenuItemResponseDTO responseDTO;
    private MenuItemRequestDTO requestDTO;
    private Page<MenuItemResponseDTO> page;

    @BeforeEach
    void setUp() {
        responseDTO = MenuItemResponseDTO.builder()
                .foodItemId(1L)
                .restaurantId(1L)
                .menuCategory(MenuCategory.STARTER)
                .build();

        requestDTO = MenuItemRequestDTO.builder()
                .restaurantId(1L)
                .menuCategory(MenuCategory.STARTER)
                .build();

        page = new PageImpl<>(List.of(responseDTO));
    }

    @Test
    void getMenuItemByFoodItemId_success() {
        when(menuItemService.getMenuItemByFoodItemId(1L)).thenReturn(responseDTO);

        ResponseEntity<MenuItemResponseDTO> result = menuItemController.getMenuItemByFoodItemId(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseDTO, result.getBody());
        verify(menuItemService, times(1)).getMenuItemByFoodItemId(1L);
    }

    @Test
    void getMenuItemByRestaurantIdAndFoodItemId_success() {
        when(menuItemService.getMenuItemByRestaurantIdAndFoodItemId(1L, 1L)).thenReturn(responseDTO);

        ResponseEntity<MenuItemResponseDTO> result = menuItemController.getMenuItemByRestaurantIdAndFoodItemId(1L, 1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseDTO, result.getBody());
    }

    @Test
    void getAllMenuItemByRestaurantId_success() {
        when(menuItemService.getAllMenuItemByRestaurantId(eq(1L), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<MenuItemResponseDTO>> result = menuItemController.getAllMenuItemByRestaurantId(1L, PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getTotalElements());
    }

    @Test
    void getAllAvailableMenuItemByRestaurantId_success() {
        when(menuItemService.getAllAvailableMenuItemByRestaurantId(eq(1L), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<MenuItemResponseDTO>> result = menuItemController.getAllAvailableMenuItemByRestaurantId(1L, PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getTotalElements());
    }

    @Test
    void getAllMenuItemByRestaurantIdAndMenuCategory_success() {
        when(menuItemService.getAllMenuItemByRestaurantIdAndMenuCategory(eq(1L), eq(MenuCategory.STARTER), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<MenuItemResponseDTO>> result = menuItemController.getAllMenuItemByRestaurantIdAndMenuCategory(1L, MenuCategory.STARTER, PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getAllAvailableMenuItemByRestaurantIdAndMenuCategory_success() {
        when(menuItemService.getAllAvailableMenuItemByRestaurantIdAndMenuCategory(eq(1L), eq(MenuCategory.STARTER), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<MenuItemResponseDTO>> result = menuItemController.getAllAvailableMenuItemByRestaurantIdAndMenuCategory(1L, MenuCategory.STARTER, PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getAllMenuItemByRestaurantIdAndFoodItemCategory_success() {
        when(menuItemService.getAllMenuItemByRestaurantIdAndFoodItemCategory(eq(1L), eq(FoodItemCategory.VEG), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<MenuItemResponseDTO>> result = menuItemController.getAllMenuItemByRestaurantIdAndFoodItemCategory(1L, FoodItemCategory.VEG, PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getAllAvailableMenuItemByRestaurantIdAndFoodItemCategory_success() {
        when(menuItemService.getAllAvailableMenuItemByRestaurantIdAndFoodItemCategory(eq(1L), eq(FoodItemCategory.VEG), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<MenuItemResponseDTO>> result = menuItemController.getAllAvailableMenuItemByRestaurantIdAndFoodItemCategory(1L, FoodItemCategory.VEG, PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getAllMenuItemByRestaurantIdAndMenuCategoryAndFoodItemCategory_success() {
        when(menuItemService.getAllMenuItemByRestaurantIdAndMenuCategoryAndFoodItemCategory(eq(1L), eq(MenuCategory.STARTER), eq(FoodItemCategory.VEG), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<MenuItemResponseDTO>> result = menuItemController.getAllMenuItemByRestaurantIdAndMenuCategoryAndFoodItemCategory(1L, MenuCategory.STARTER, FoodItemCategory.VEG, PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getAllAvailableMenuItemByRestaurantIdAndMenuCategoryAndFoodItemCategory_success() {
        when(menuItemService.getAllAvailableMenuItemByRestaurantIdAndMenuCategoryAndFoodItemCategory(eq(1L), eq(MenuCategory.STARTER), eq(FoodItemCategory.VEG), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<MenuItemResponseDTO>> result = menuItemController.getAllAvailableMenuItemByRestaurantIdAndMenuCategoryAndFoodItemCategory(1L, MenuCategory.STARTER, FoodItemCategory.VEG, PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getAllMenuItemByRestaurantIdAndPriceRange_success() {
        when(menuItemService.getAllMenuItemByRestaurantIdAndPriceRange(eq(1L), eq(new BigDecimal("5.00")), eq(new BigDecimal("15.00")), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<MenuItemResponseDTO>> result = menuItemController.getAllMenuItemByRestaurantIdAndPriceRange(1L, new BigDecimal("5.00"), new BigDecimal("15.00"), PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void createMenuItem_success() {
        when(menuItemService.createMenuItem(requestDTO)).thenReturn(responseDTO);

        ResponseEntity<MenuItemResponseDTO> result = menuItemController.createMenuItem(requestDTO);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(responseDTO, result.getBody());
        verify(menuItemService, times(1)).createMenuItem(requestDTO);
    }

    @Test
    void updateMenuItem_success() {
        when(menuItemService.updateMenuItem(1L, 1L, requestDTO)).thenReturn(responseDTO);

        ResponseEntity<MenuItemResponseDTO> result = menuItemController.updateMenuItem(1L, 1L, requestDTO);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseDTO, result.getBody());
        verify(menuItemService, times(1)).updateMenuItem(1L, 1L, requestDTO);
    }

    @Test
    void deleteMenuItem_success() {
        doNothing().when(menuItemService).deleteMenuItem(1L, 1L);

        ResponseEntity<String> result = menuItemController.deleteMenuItem(1L, 1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Deleted successfully", result.getBody());
        verify(menuItemService, times(1)).deleteMenuItem(1L, 1L);
    }
}
