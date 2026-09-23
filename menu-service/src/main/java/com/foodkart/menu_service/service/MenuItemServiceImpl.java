package com.foodkart.menu_service.service;

import com.foodkart.menu_service.client.RestaurantClient;
import com.foodkart.menu_service.dto.*;
import com.foodkart.menu_service.entity.MenuItem;
import com.foodkart.menu_service.model.FoodItemCategory;
import com.foodkart.menu_service.model.MenuCategory;
import com.foodkart.menu_service.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantClient restaurantClient;

    @Override
    public MenuItemResponseDTO createMenuItem(MenuItemRequestDTO request) {

        log.info("Creating Menu Item");
        Long restaurantId = request.getRestaurantId();

        RestaurantDTO restaurant = restaurantClient.getRestaurantById(restaurantId);

        if (!restaurant.isActive()) {
            throw new RuntimeException("Restaurant is not active");
        }

        log.info("Creating Menu Item for restaurant Id: {}", restaurantId);

        MenuItem menuItem = MenuItem.builder()
                .restaurantId(restaurantId)
                .menuCategory(request.getMenuCategory())
                .foodItemName(request.getFoodItem().getName())
                .foodItemDescription(request.getFoodItem().getDescription())
                .foodItemCategory(request.getFoodItem().getCategory())
                .foodItemPrice(request.getFoodItem().getPrice())
                .foodItemAvailable(request.getFoodItem().getAvailable())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);

        //Intention is to return foodItemId to response
        MenuItem fetchMenuItem = menuItemRepository.findByRestaurantIdAndId(savedMenuItem.getRestaurantId(), savedMenuItem.getId());

        return mapToResponse(fetchMenuItem);
    }

    @Override
    public MenuItemResponseDTO getMenuItemByFoodItemId(Long foodItemId) {

        MenuItem menuItem = menuItemRepository.findById(foodItemId)
                .orElseThrow(() ->
                        new RuntimeException("Menu item not found with id: " + foodItemId)
                );

        return mapToResponse(menuItem);
    }

    @Override
    public MenuItemResponseDTO getMenuItemByRestaurantIdAndFoodItemId(Long restaurantId, Long foodItemId){

        MenuItem menuItem = menuItemRepository.findByRestaurantIdAndId(restaurantId, foodItemId);

        if (menuItem == null) {
            log.error("Menu item not found with RestaurantId: {}, FoodItemId: {} ", restaurantId, foodItemId);
            return null;
        }

        return mapToResponse(menuItem);
    }

    @Override
    public Page<MenuItemResponseDTO> getAllMenuItemByRestaurantId(Long restaurantId, Pageable pageable){

        Page<MenuItem> menuItem = menuItemRepository.findAllByRestaurantId(restaurantId, pageable);

        return menuItem.map(this::mapToResponse);

    }

    @Override
    public Page<MenuItemResponseDTO> getAllAvailableMenuItemByRestaurantId(Long restaurantId, Pageable pageable){

        Page<MenuItem> menuItem = menuItemRepository.findAllByRestaurantIdAndFoodItemAvailableTrue(restaurantId, pageable);

        return menuItem.map(this :: mapToResponse);
    }

    @Override
    public Page<MenuItemResponseDTO> getAllMenuItemByRestaurantIdAndMenuCategory(Long restaurantId, MenuCategory menuCategory, Pageable pageable){

        Page<MenuItem> menuItem = menuItemRepository.findAllByRestaurantIdAndMenuCategory(restaurantId, menuCategory, pageable);

        return menuItem.map(this :: mapToResponse);
    }

    @Override
    public Page<MenuItemResponseDTO> getAllAvailableMenuItemByRestaurantIdAndMenuCategory(Long restaurantId,MenuCategory menuCategory, Pageable pageable){

        Page<MenuItem> menuItem = menuItemRepository.findAllByRestaurantIdAndMenuCategoryAndFoodItemAvailableTrue(restaurantId, menuCategory, pageable);

        return menuItem.map(this :: mapToResponse);
    }

    @Override
    public Page<MenuItemResponseDTO> getAllMenuItemByRestaurantIdAndFoodItemCategory(Long restaurantId, FoodItemCategory foodItemCategory, Pageable pageable){

        Page<MenuItem> menuItem = menuItemRepository.findAllByRestaurantIdAndFoodItemCategory(restaurantId, foodItemCategory, pageable);

        return menuItem.map(this :: mapToResponse);
    }
    
    @Override
    public Page<MenuItemResponseDTO> getAllAvailableMenuItemByRestaurantIdAndFoodItemCategory(Long restaurantId, FoodItemCategory foodItemCategory, Pageable pageable){

        Page<MenuItem> menuItem = menuItemRepository.findAllByRestaurantIdAndFoodItemCategoryAndFoodItemAvailableTrue(restaurantId, foodItemCategory, pageable);

        return menuItem.map(this :: mapToResponse);
    }
    
    @Override
    public Page<MenuItemResponseDTO> getAllMenuItemByRestaurantIdAndMenuCategoryAndFoodItemCategory(
            Long restaurantId, MenuCategory menuCategory, FoodItemCategory foodItemCategory, Pageable pageable){
        
        Page<MenuItem> menuItem = menuItemRepository.findAllByRestaurantIdAndMenuCategoryAndFoodItemCategory(restaurantId, menuCategory, foodItemCategory, pageable);

        return menuItem.map(this :: mapToResponse);
    }
    
    @Override
    public Page<MenuItemResponseDTO> getAllAvailableMenuItemByRestaurantIdAndMenuCategoryAndFoodItemCategory(
            Long restaurantId, MenuCategory menuCategory, FoodItemCategory foodItemCategory, Pageable pageable){
        
        Page<MenuItem> menuItem = menuItemRepository.findAllByRestaurantIdAndMenuCategoryAndFoodItemCategoryAndFoodItemAvailableTrue(restaurantId, menuCategory, foodItemCategory, pageable);

        return menuItem.map(this :: mapToResponse);
    }

    @Override
    public Page<MenuItemResponseDTO> getAllMenuItemByRestaurantIdAndPriceRange(Long restaurantId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {

        Page<MenuItem> menuItems = menuItemRepository
                .findAllByRestaurantIdAndFoodItemPriceBetween(restaurantId, minPrice, maxPrice, pageable);

        return menuItems.map(this::mapToResponse);
    }

    @Override
    public MenuItemResponseDTO updateMenuItem(Long restaurantId, Long foodItemId, MenuItemRequestDTO request){

        if((restaurantId==0 || foodItemId == 0)||(restaurantId==-1 || foodItemId == -1)){
            throw new RuntimeException("RestaurantId or MenuId not Found");
        }

        MenuItem menuItem = menuItemRepository.findByRestaurantIdAndId(restaurantId, foodItemId);

        menuItem.setMenuCategory(request.getMenuCategory());
        menuItem.setFoodItemName(request.getFoodItem().getName());
        menuItem.setFoodItemDescription(request.getFoodItem().getDescription());
        menuItem.setFoodItemPrice(request.getFoodItem().getPrice());
        menuItem.setFoodItemCategory(request.getFoodItem().getCategory());
        menuItem.setFoodItemAvailable(request.getFoodItem().getAvailable());
        menuItem.setUpdatedAt(LocalDateTime.now());

        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);

        return mapToResponse(updatedMenuItem);
    }

    @Override
    @Transactional
    public void deleteMenuItem(Long restaurantId, Long menuId) {

        log.info(
                "Deleting menu item with id: {}, for Restaurant Id: {}",
                menuId,
                restaurantId
        );

        if (!menuItemRepository.existsByRestaurantIdAndId(restaurantId, menuId)) {
            log.warn(
                    "MenuItem not found with id: {} for Restaurant Id: {}",
                    menuId,
                    restaurantId
            );

            throw new RuntimeException(
                    "No menu item found with id " + menuId +
                            " for restaurant id " + restaurantId
            );
        }

        menuItemRepository.deleteByRestaurantIdAndId(
                restaurantId,
                menuId
        );

        log.info(
                "Menu item deleted successfully with Menu Id: {}, for Restaurant Id: {}",
                menuId,
                restaurantId
        );
    }

    private MenuItemResponseDTO mapToResponse(MenuItem menuItem){

        FoodItemRequestDTO foodItemDTO = FoodItemRequestDTO.builder()
                .name(menuItem.getFoodItemName())
                .description(menuItem.getFoodItemDescription())
                .price(menuItem.getFoodItemPrice())
                .category(menuItem.getFoodItemCategory())
                .available(menuItem.getFoodItemAvailable())
                .createdAt(menuItem.getCreatedAt())
                .updatedAt(menuItem.getUpdatedAt())
                .build();

        return MenuItemResponseDTO.builder()
                .foodItemId(menuItem.getId())
                .restaurantId(menuItem.getRestaurantId())
                .menuCategory(menuItem.getMenuCategory())
                .foodItem(foodItemDTO)
                .build();
    }
}