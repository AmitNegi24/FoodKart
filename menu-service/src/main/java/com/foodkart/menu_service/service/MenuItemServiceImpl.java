package com.foodkart.menu_service.service;

import com.foodkart.menu_service.dto.MenuItemRequestDTO;
import com.foodkart.menu_service.dto.MenuItemResponseDTO;
import com.foodkart.menu_service.entity.MenuItem;
import com.foodkart.menu_service.repository.MenuItemRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;

    @Override
    public MenuItemResponseDTO createMenuItemWithRestaurantId(MenuItemRequestDTO request, Long restaurantId){

        if(restaurantId == -1 || restaurantId == 0){
            throw new RuntimeException("Please provide a valid Restaurant Id!");
        }

        if(request.getName().isBlank() || request.getDescription().isBlank()){
            throw new RuntimeException("Menu can't be created without a name or description!");
        }

        log.info("Creating Menu Item for restaurant Id : {}", restaurantId);

        MenuItem menuItem = MenuItem.builder()
                .restaurantId(restaurantId)
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .available(request.getAvailable())
                .build();

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);

        if(savedMenuItem == null){
            throw new RuntimeException("Failed to create Menu Item");
        }

        return mapToResponse(savedMenuItem);
    }

    @Override
    public MenuItemResponseDTO getMenuItemByMenuId(Long menuId) {

        MenuItem menuItem = menuItemRepository.findById(menuId)
                .orElseThrow(() ->
                        new RuntimeException("Menu item not found with id: " + menuId)
                );

        return mapToResponse(menuItem);
    }

//    @Override
//    public Page<MenuItemResponseDTO> getMenuItemByRestaurantId(Long restaurantId, Long menuId){
//        Pageable<MenuItem> menuItem = menuItemRepository.findByRestaurantIdAndMenuId(restaurantId, menuId);
//
//        if(menuItem==null) {
//            throw new RuntimeException("Menu item or Restaurant doesn't exists: " + menuId);
//        }
//
//        return menuItem.map(this::mapToResponse);
//
//    }

    @Override
    public Page<MenuItemResponseDTO> getAllMenuItemByRestaurantId(Long restaurantId, Pageable pageable){

        Page<MenuItem> menuItem = menuItemRepository.findAllByRestaurantId(restaurantId, pageable);

        return menuItem.map(this::mapToResponse);

    }

    @Override
    public Page<MenuItemResponseDTO> getAvailableMenuItemByRestaurantId(Long restaurantId, Pageable pageable){

        Page<MenuItem> menuItem = menuItemRepository.findAllMenusByRestaurantIdAndAvailableTrue(restaurantId, pageable);

        return menuItem.map(this :: mapToResponse);
    }

    @Override
    public Page<MenuItemResponseDTO> getAllMenuItemByRestaurantIdAndCategory(Long restaurantId,String category, Pageable pageable){

        Page<MenuItem> menuItem = menuItemRepository.findByRestaurantIdAndCategory(restaurantId, category, pageable);

        return menuItem.map(this :: mapToResponse);
    }

    @Override
    public Page<MenuItemResponseDTO> getAllAvailableMenuItemByRestaurantIdAndCategory(Long restaurantId,String category, Pageable pageable){

        Page<MenuItem> menuItem = menuItemRepository.findByRestaurantIdAndCategoryAndAvailableTrue(restaurantId, category, pageable);

        return menuItem.map(this :: mapToResponse);
    }

    @Override
    public Page<MenuItemResponseDTO> getMenuItemsByRestaurantAndPriceRange(Long restaurantId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {

        Page<MenuItem> menuItems = menuItemRepository
                .findByRestaurantIdAndPriceBetween(restaurantId, minPrice, maxPrice, pageable);

        return menuItems.map(this::mapToResponse);
    }

    @Override
    public MenuItemResponseDTO updateMenuItem(Long restaurantId, Long menuId, MenuItemRequestDTO request){

        if((restaurantId==0 || menuId == 0)||(restaurantId==-1 || menuId == -1)){
            throw new RuntimeException("RestaurantId or MenuId not Found");
        }

        MenuItem menuItem = menuItemRepository.findByRestaurantIdAndId(restaurantId, menuId);

        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setCategory(request.getCategory());
        menuItem.setAvailable(request.getAvailable());
        menuItem.setUpdatedAt(LocalDateTime.now());

        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);

        if(updatedMenuItem.equals(null)){
            throw new RuntimeException("Failed to save Menu Item!");
        }

        return mapToResponse(updatedMenuItem);
    }

    @Override
    public void deleteMenuItem(Long restaurantId, Long menuId){

        log.info("Deleting restaurant with id: {}, for Restaurant Id: {}", menuId, restaurantId);

        if (!menuItemRepository.existsById(menuId)) {
            log.warn("Restaurant not found with id while deleting:" +menuId);
            throw new RuntimeException(
                    "Restaurant not found with id: " + menuId);
        }

        menuItemRepository.deleteByRestaurantIdAndId(restaurantId, menuId);

        log.info("Restaurant deleted successfully with Menu Id: {}, for Restaurant Id: {}", menuId, restaurantId);

    }

    private MenuItemResponseDTO mapToResponse(MenuItem menuItem){

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