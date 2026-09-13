package com.foodKart.restaurant_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.foodKart.restaurant_service.dto.RestaurantRequestDTO;
import com.foodKart.restaurant_service.dto.RestaurantResponseDTO;
import com.foodKart.restaurant_service.entity.Restaurant;
import com.foodKart.restaurant_service.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceImplTest {
    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    @Test
    void createRestaurant_success(){
        RestaurantRequestDTO dto = new RestaurantRequestDTO(
                "Spice Garden", "Indian food", "12 MG Road", "Bengaluru", "9876543210"
        );

        Restaurant savedEntity = new Restaurant(
                1L, "Spice Garden", "Indian food", "12 MG Road", "Bengaluru",
                "9876543210", true, LocalDateTime.now()
        );

        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(savedEntity);

        // Act
        RestaurantResponseDTO response = restaurantService.createRestaurant(dto);

        // Assert
        assertNotNull(response);
        assertEquals("Spice Garden", response.getName());
        assertEquals("Bengaluru", response.getCity());
    }

    @Test
    void createRestaurant_invalidInput(){
        RestaurantRequestDTO dto = new RestaurantRequestDTO(
                null, "Indian food", "12 MG Road", "Bengaluru", "9876543210"
        );

        assertThrows(IllegalArgumentException.class, () -> restaurantService.createRestaurant(dto));
        verifyNoInteractions(restaurantRepository);
    }
}
