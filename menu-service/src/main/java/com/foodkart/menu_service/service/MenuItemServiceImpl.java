package com.foodkart.menu_service.service;

import com.foodkart.menu_service.client.RestaurantClient;
import com.foodkart.menu_service.dto.*;
import com.foodkart.menu_service.entity.FoodItemPopularity;
import com.foodkart.menu_service.entity.MenuItem;
import com.foodkart.menu_service.exception.MenuItemNotFoundException;
import com.foodkart.menu_service.model.FoodItemCategory;
import com.foodkart.menu_service.model.MenuCategory;
import com.foodkart.menu_service.repository.FoodItemPopularityRepository;
import com.foodkart.menu_service.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.kafka.annotation.KafkaListener;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final FoodItemPopularityRepository foodItemPopularityRepository;
    private final RestaurantClient restaurantClient;
    private final ObjectMapper objectMapper;

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
    @Cacheable(value = "menuItems", key = "#foodItemId")
    public MenuItemResponseDTO getMenuItemByFoodItemId(Long foodItemId) {
        log.info("CACHE MISS: loading menu item {} from DATABASE", foodItemId);
        MenuItem menuItem = menuItemRepository.findById(foodItemId)
                .orElseThrow(() ->
                        new MenuItemNotFoundException("Menu item not found with id: " + foodItemId)
                );

        return mapToResponse(menuItem);
    }


    @Override
    public Page<MenuItemResponseDTO> getPopularFoodItems(Pageable pageable) {

        // 1. Fetch popularity records in descending order
        Page<FoodItemPopularity> popularityPage =
                foodItemPopularityRepository
                        .findByFoodItemIdGreaterThanAndTotalOrdersGreaterThanOrderByTotalOrdersDesc(
                                0L,
                                0L,
                                pageable
                        );

        // 2. Extract food item IDs
        List<Long> foodItemIds = popularityPage.getContent()
                .stream()
                .map(FoodItemPopularity::getFoodItemId)
                .toList();

        if (foodItemIds.isEmpty()) {
            return new PageImpl<>(
                    List.of(),
                    pageable,
                    popularityPage.getTotalElements()
            );
        }

        // 3. Fetch actual menu item details
        List<MenuItem> menuItems =
                menuItemRepository.findAllByIdIn(foodItemIds);

        // 4. Convert menu items into a Map for quick lookup
        Map<Long, MenuItem> menuItemMap = menuItems.stream()
                .collect(Collectors.toMap(
                        MenuItem::getId,
                        Function.identity()
                ));

        // 5. Preserve popularity order while creating response DTOs
        List<MenuItemResponseDTO> response = foodItemIds.stream()
                .map(menuItemMap::get)
                .filter(Objects::nonNull)
                .map(this::mapToResponse)
                .toList();

        // 6. Return paginated response
        return new PageImpl<>(
                response,
                pageable,
                popularityPage.getTotalElements()
        );
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
    @Transactional
    @CacheEvict(value = "menuItems", key = "#foodItemId")
    public MenuItemResponseDTO updateMenuItem(Long restaurantId, Long foodItemId, MenuItemRequestDTO request){
        log.info(
                "CACHE EVICT: updating menu item {}, new price {}",
                foodItemId,
                request.getFoodItem().getPrice()
        );

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
    @CacheEvict(value = "menuItems", key = "#menuId")
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


    @KafkaListener(
            topics = "food-popularity-daily",
            groupId = "foodkart-menu-popularity-group"
    )
    @Transactional
    public void consumePopularitySnapshot(String message) {

        // 1. Parse incoming snapshot
        Map<?, ?> payload =
                objectMapper.readValue(message, Map.class);

        Object timestampValue = payload.get("generatedAt");
        Object popularityValue = payload.get("popularity");

        if (!(timestampValue instanceof String)
                || !(popularityValue instanceof Map<?, ?>)) {
            throw new IllegalArgumentException(
                    "Invalid popularity snapshot"
            );
        }

        LocalDateTime snapshotTime = LocalDateTime.ofInstant(
                Instant.parse((String) timestampValue),
                ZoneOffset.UTC
        );

        Map<?, ?> incomingScores =
                (Map<?, ?>) popularityValue;

        Map<Long, Long> scores = new HashMap<>();

        for (Map.Entry<?, ?> entry : incomingScores.entrySet()) {

            Long foodItemId =
                    Long.valueOf(entry.getKey().toString());

            if (foodItemId <= 0
                    || !(entry.getValue() instanceof Number)) {
                throw new IllegalArgumentException(
                        "Invalid food popularity entry"
                );
            }

            long count =
                    ((Number) entry.getValue()).longValue();

            if (count < 0) {
                throw new IllegalArgumentException(
                        "Negative popularity score"
                );
            }

            scores.put(foodItemId, count);
        }

        // 2. Metadata record tracks latest applied snapshot.
        // foodItemId = 0 is reserved for metadata.
        FoodItemPopularity metadata =
                foodItemPopularityRepository
                        .findByFoodItemIdForUpdate(0L)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Popularity metadata record is missing"
                                )
                        );

        // 3. Ignore duplicate or older snapshots
        if (metadata.getUpdatedAt() != null
                && !snapshotTime.isAfter(metadata.getUpdatedAt())) {

            log.info("Skipping stale popularity snapshot");
            return;
        }

        // 4. Reset existing scores to zero
        List<FoodItemPopularity> existing =
                foodItemPopularityRepository.findAll();

        Map<Long, FoodItemPopularity> existingMap =
                new HashMap<>();

        for (FoodItemPopularity item : existing) {

            if (item.getFoodItemId().equals(0L)) {
                continue;
            }

            item.setTotalOrders(0L);
            existingMap.put(item.getFoodItemId(), item);
        }

        // 5. Apply new scores
        for (Map.Entry<Long, Long> entry : scores.entrySet()) {

            FoodItemPopularity item =
                    existingMap.get(entry.getKey());

            if (item == null) {
                item = FoodItemPopularity.builder()
                        .foodItemId(entry.getKey())
                        .totalOrders(entry.getValue())
                        .updatedAt(snapshotTime)
                        .build();

                existing.add(item);

            } else {
                item.setTotalOrders(entry.getValue());
                item.setUpdatedAt(snapshotTime);
            }
        }

        // 6. Save rankings and metadata atomically
        metadata.setUpdatedAt(snapshotTime);
        metadata.setTotalOrders(0L);

        foodItemPopularityRepository.saveAll(existing);

        log.info(
                "Weekly popularity snapshot applied at {}",
                snapshotTime
        );
    }

}