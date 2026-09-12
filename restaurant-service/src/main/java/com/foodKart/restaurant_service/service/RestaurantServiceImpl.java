package com.foodKart.restaurant_service.service;

import com.foodKart.restaurant_service.dto.RestaurantRequestDTO;
import com.foodKart.restaurant_service.dto.RestaurantResponseDTO;
import com.foodKart.restaurant_service.entity.Restaurant;
import com.foodKart.restaurant_service.exception.RestaurantNotFoundException;
import com.foodKart.restaurant_service.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public RestaurantResponseDTO createRestaurant(RestaurantRequestDTO request) {

        log.info("Creating restaurant with name: {}", request.getName());

        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .city(request.getCity())
                .phone(request.getPhone())
                .active(true)
                .build();

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        log.info("Restaurant created successfully with id: {}",
                savedRestaurant.getId());

        return mapToResponse(savedRestaurant);
    }

    @Override
    public Page<RestaurantResponseDTO> getAllRestaurants(Pageable pageable) {

        log.info("Fetching restaurants - page: {}, size: {}",
                pageable.getPageNumber(),
                pageable.getPageSize());

        return restaurantRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public RestaurantResponseDTO getRestaurantById(Long id) {

        log.info("Fetching restaurant with id: {}", id);

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() ->
                        new RestaurantNotFoundException("Restaurant not found with id: " + id));

        return mapToResponse(restaurant);
    }

    @Override
    public RestaurantResponseDTO updateRestaurant(
            Long id,
            RestaurantRequestDTO request) {

        log.info("Updating restaurant with id: {}", id);

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() ->
                        new RestaurantNotFoundException(
                                "Restaurant not found with id: " + id));

        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setAddress(request.getAddress());
        restaurant.setCity(request.getCity());
        restaurant.setPhone(request.getPhone());

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);

        log.info("Restaurant updated successfully with id: {}", id);

        return mapToResponse(updatedRestaurant);
    }

    @Override
    public void deleteRestaurant(Long id) {

        log.info("Deleting restaurant with id: {}", id);

        if (!restaurantRepository.existsById(id)) {
            throw new RestaurantNotFoundException(
                    "Restaurant not found with id: " + id);
        }

        restaurantRepository.deleteById(id);

        log.info("Restaurant deleted successfully with id: {}", id);
    }

    @Override
    public Page<RestaurantResponseDTO> getActiveRestaurantsByCity(
            String city,
            Pageable pageable) {

        Page<Restaurant> restaurants =
                restaurantRepository.findByCityAndActiveTrue(city, pageable);

        return restaurants.map(this::mapToResponse);
    }

    @Override
    public Page<RestaurantResponseDTO> getRestaurantsByNameContaining(String name, Pageable pageable) {
        Page<Restaurant> restaurants =
                restaurantRepository.findByNameContainingAndActiveTrue(name, pageable);

        return restaurants.map(this::mapToResponse);
    }

    private RestaurantResponseDTO mapToResponse(Restaurant restaurant) {

        return RestaurantResponseDTO.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .address(restaurant.getAddress())
                .city(restaurant.getCity())
                .phone(restaurant.getPhone())
                .active(restaurant.isActive())
                .createdAt(restaurant.getCreatedAt())
                .build();
    }
}
