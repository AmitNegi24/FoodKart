package com.foodKart.restaurant_service.service;

import com.foodKart.restaurant_service.dto.RestaurantRequestDTO;
import com.foodKart.restaurant_service.dto.RestaurantResponseDTO;

import java.util.List;

public interface RestaurantService {

    RestaurantResponseDTO createRestaurant(RestaurantRequestDTO request);

    List<RestaurantResponseDTO> getAllRestaurants();

    RestaurantResponseDTO getRestaurantById(Long id);

    RestaurantResponseDTO updateRestaurant(Long id, RestaurantRequestDTO request);

    void deleteRestaurant(Long id);
}