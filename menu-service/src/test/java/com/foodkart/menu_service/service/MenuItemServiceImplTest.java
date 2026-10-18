package com.foodkart.menu_service.service;

import com.foodkart.menu_service.client.RestaurantClient;
import com.foodkart.menu_service.dto.FoodItemRequestDTO;
import com.foodkart.menu_service.dto.MenuItemRequestDTO;
import com.foodkart.menu_service.dto.MenuItemResponseDTO;
import com.foodkart.menu_service.dto.RestaurantDTO;
import com.foodkart.menu_service.entity.MenuItem;
import com.foodkart.menu_service.exception.MenuItemNotFoundException;
import com.foodkart.menu_service.model.FoodItemCategory;
import com.foodkart.menu_service.model.MenuCategory;
import com.foodkart.menu_service.repository.MenuItemRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceImplTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private RestaurantClient restaurantClient;

    @InjectMocks
    private MenuItemServiceImpl menuItemService;

    private MenuItem menuItem;
    private MenuItemRequestDTO requestDTO;
    private RestaurantDTO restaurantDTO;

    @BeforeEach
    void setUp() {
        menuItem = MenuItem.builder()
                .id(1L)
                .restaurantId(1L)
                .menuCategory(MenuCategory.STARTER)
                .foodItemName("Pancakes")
                .foodItemDescription("Delicious pancakes")
                .foodItemPrice(new BigDecimal("9.99"))
                .foodItemCategory(FoodItemCategory.VEG)
                .foodItemAvailable(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        FoodItemRequestDTO foodItem = FoodItemRequestDTO.builder()
                .name("Pancakes")
                .description("Delicious pancakes")
                .price(new BigDecimal("9.99"))
                .category(FoodItemCategory.VEG)
                .available(true)
                .build();

        requestDTO = MenuItemRequestDTO.builder()
                .restaurantId(1L)
                .menuCategory(MenuCategory.STARTER)
                .foodItem(foodItem)
                .build();

        restaurantDTO = new RestaurantDTO();
        restaurantDTO.setId(1L);
        restaurantDTO.setName("Test Restaurant");
        restaurantDTO.setActive(true);
    }

    @Test
    void createMenuItem_success() {
        when(restaurantClient.getRestaurantById(1L)).thenReturn(restaurantDTO);
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(menuItem);
        when(menuItemRepository.findByRestaurantIdAndId(1L, 1L)).thenReturn(menuItem);

        MenuItemResponseDTO result = menuItemService.createMenuItem(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getFoodItemId());
        assertEquals(1L, result.getRestaurantId());
        verify(restaurantClient, times(1)).getRestaurantById(1L);
        verify(menuItemRepository, times(1)).save(any(MenuItem.class));
    }

    @Test
    void createMenuItem_restaurantNotActive() {
        restaurantDTO.setActive(false);
        when(restaurantClient.getRestaurantById(1L)).thenReturn(restaurantDTO);

        assertThrows(RuntimeException.class, () -> menuItemService.createMenuItem(requestDTO));
        verify(menuItemRepository, never()).save(any(MenuItem.class));
    }

    @Test
    void getMenuItemByFoodItemId_success() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));

        MenuItemResponseDTO result = menuItemService.getMenuItemByFoodItemId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getFoodItemId());
        verify(menuItemRepository, times(1)).findById(1L);
    }

    @Test
    void getMenuItemByFoodItemId_notFound() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MenuItemNotFoundException.class, () -> menuItemService.getMenuItemByFoodItemId(1L));
    }

    @Test
    void getMenuItemByRestaurantIdAndFoodItemId_success() {
        when(menuItemRepository.findByRestaurantIdAndId(1L, 1L)).thenReturn(menuItem);

        MenuItemResponseDTO result = menuItemService.getMenuItemByRestaurantIdAndFoodItemId(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getFoodItemId());
    }

    @Test
    void getMenuItemByRestaurantIdAndFoodItemId_notFound() {
        when(menuItemRepository.findByRestaurantIdAndId(1L, 1L)).thenReturn(null);

        MenuItemResponseDTO result = menuItemService.getMenuItemByRestaurantIdAndFoodItemId(1L, 1L);

        assertNull(result);
    }

    @Test
    void getAllMenuItemByRestaurantId_success() {
        Page<MenuItem> page = new PageImpl<>(java.util.List.of(menuItem));
        when(menuItemRepository.findAllByRestaurantId(eq(1L), any(Pageable.class))).thenReturn(page);

        Page<MenuItemResponseDTO> result = menuItemService.getAllMenuItemByRestaurantId(1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllAvailableMenuItemByRestaurantId_success() {
        Page<MenuItem> page = new PageImpl<>(java.util.List.of(menuItem));
        when(menuItemRepository.findAllByRestaurantIdAndFoodItemAvailableTrue(eq(1L), any(Pageable.class))).thenReturn(page);

        Page<MenuItemResponseDTO> result = menuItemService.getAllAvailableMenuItemByRestaurantId(1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllMenuItemByRestaurantIdAndMenuCategory_success() {
        Page<MenuItem> page = new PageImpl<>(java.util.List.of(menuItem));
        when(menuItemRepository.findAllByRestaurantIdAndMenuCategory(eq(1L), eq(MenuCategory.STARTER), any(Pageable.class))).thenReturn(page);

        Page<MenuItemResponseDTO> result = menuItemService.getAllMenuItemByRestaurantIdAndMenuCategory(1L, MenuCategory.STARTER, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllAvailableMenuItemByRestaurantIdAndMenuCategory_success() {
        Page<MenuItem> page = new PageImpl<>(java.util.List.of(menuItem));
        when(menuItemRepository.findAllByRestaurantIdAndMenuCategoryAndFoodItemAvailableTrue(eq(1L), eq(MenuCategory.STARTER), any(Pageable.class))).thenReturn(page);

        Page<MenuItemResponseDTO> result = menuItemService.getAllAvailableMenuItemByRestaurantIdAndMenuCategory(1L, MenuCategory.STARTER, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllMenuItemByRestaurantIdAndFoodItemCategory_success() {
        Page<MenuItem> page = new PageImpl<>(java.util.List.of(menuItem));
        when(menuItemRepository.findAllByRestaurantIdAndFoodItemCategory(eq(1L), eq(FoodItemCategory.VEG), any(Pageable.class))).thenReturn(page);

        Page<MenuItemResponseDTO> result = menuItemService.getAllMenuItemByRestaurantIdAndFoodItemCategory(1L, FoodItemCategory.VEG, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllAvailableMenuItemByRestaurantIdAndFoodItemCategory_success() {
        Page<MenuItem> page = new PageImpl<>(java.util.List.of(menuItem));
        when(menuItemRepository.findAllByRestaurantIdAndFoodItemCategoryAndFoodItemAvailableTrue(eq(1L), eq(FoodItemCategory.VEG), any(Pageable.class))).thenReturn(page);

        Page<MenuItemResponseDTO> result = menuItemService.getAllAvailableMenuItemByRestaurantIdAndFoodItemCategory(1L, FoodItemCategory.VEG, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllMenuItemByRestaurantIdAndMenuCategoryAndFoodItemCategory_success() {
        Page<MenuItem> page = new PageImpl<>(java.util.List.of(menuItem));
        when(menuItemRepository.findAllByRestaurantIdAndMenuCategoryAndFoodItemCategory(eq(1L), eq(MenuCategory.STARTER), eq(FoodItemCategory.VEG), any(Pageable.class))).thenReturn(page);

        Page<MenuItemResponseDTO> result = menuItemService.getAllMenuItemByRestaurantIdAndMenuCategoryAndFoodItemCategory(1L, MenuCategory.STARTER, FoodItemCategory.VEG, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllAvailableMenuItemByRestaurantIdAndMenuCategoryAndFoodItemCategory_success() {
        Page<MenuItem> page = new PageImpl<>(java.util.List.of(menuItem));
        when(menuItemRepository.findAllByRestaurantIdAndMenuCategoryAndFoodItemCategoryAndFoodItemAvailableTrue(eq(1L), eq(MenuCategory.STARTER), eq(FoodItemCategory.VEG), any(Pageable.class))).thenReturn(page);

        Page<MenuItemResponseDTO> result = menuItemService.getAllAvailableMenuItemByRestaurantIdAndMenuCategoryAndFoodItemCategory(1L, MenuCategory.STARTER, FoodItemCategory.VEG, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllMenuItemByRestaurantIdAndPriceRange_success() {
        Page<MenuItem> page = new PageImpl<>(java.util.List.of(menuItem));
        when(menuItemRepository.findAllByRestaurantIdAndFoodItemPriceBetween(eq(1L), eq(new BigDecimal("5.00")), eq(new BigDecimal("15.00")), any(Pageable.class))).thenReturn(page);

        Page<MenuItemResponseDTO> result = menuItemService.getAllMenuItemByRestaurantIdAndPriceRange(1L, new BigDecimal("5.00"), new BigDecimal("15.00"), PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateMenuItem_success() {
        when(menuItemRepository.findByRestaurantIdAndId(1L, 1L)).thenReturn(menuItem);
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(menuItem);

        MenuItemResponseDTO result = menuItemService.updateMenuItem(1L, 1L, requestDTO);

        assertNotNull(result);
        verify(menuItemRepository, times(1)).save(any(MenuItem.class));
    }

    @Test
    void updateMenuItem_invalidIds() {
        assertThrows(RuntimeException.class, () -> menuItemService.updateMenuItem(0L, 1L, requestDTO));
        assertThrows(RuntimeException.class, () -> menuItemService.updateMenuItem(1L, 0L, requestDTO));
        assertThrows(RuntimeException.class, () -> menuItemService.updateMenuItem(-1L, 1L, requestDTO));
        assertThrows(RuntimeException.class, () -> menuItemService.updateMenuItem(1L, -1L, requestDTO));
    }

    @Test
    void deleteMenuItem_success() {
        when(menuItemRepository.existsByRestaurantIdAndId(1L, 1L)).thenReturn(true);
        doNothing().when(menuItemRepository).deleteByRestaurantIdAndId(1L, 1L);

        menuItemService.deleteMenuItem(1L, 1L);

        verify(menuItemRepository, times(1)).deleteByRestaurantIdAndId(1L, 1L);
    }

    @Test
    void deleteMenuItem_notFound() {
        when(menuItemRepository.existsByRestaurantIdAndId(1L, 1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> menuItemService.deleteMenuItem(1L, 1L));
        verify(menuItemRepository, never()).deleteByRestaurantIdAndId(anyLong(), anyLong());
    }
}
