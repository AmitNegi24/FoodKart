package com.foodKart.restaurant_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.foodKart.restaurant_service.dto.RestaurantRequestDTO;
import com.foodKart.restaurant_service.dto.RestaurantResponseDTO;
import com.foodKart.restaurant_service.entity.Restaurant;
import com.foodKart.restaurant_service.exception.RestaurantNotFoundException;
import com.foodKart.restaurant_service.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

        RestaurantResponseDTO response = restaurantService.createRestaurant(dto);

        assertNotNull(response);
        assertEquals("Spice Garden", response.getName());
        assertEquals("Bengaluru", response.getCity());
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void createRestaurant_nullName(){
        RestaurantRequestDTO dto = new RestaurantRequestDTO(
                null, "Indian food", "12 MG Road", "Bengaluru", "9876543210"
        );

        assertThrows(IllegalArgumentException.class, () -> restaurantService.createRestaurant(dto));
        verifyNoInteractions(restaurantRepository);
    }

    @Test
    void createRestaurant_emptyName(){
        RestaurantRequestDTO dto = new RestaurantRequestDTO(
                "   ", "Indian food", "12 MG Road", "Bengaluru", "9876543210"
        );

        assertThrows(IllegalArgumentException.class, () -> restaurantService.createRestaurant(dto));
        verifyNoInteractions(restaurantRepository);
    }

    @Test
    void createRestaurant_saveFailure(){
        RestaurantRequestDTO dto = new RestaurantRequestDTO(
                "Spice Garden", "Indian food", "12 MG Road", "Bengaluru", "9876543210"
        );

        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(null);

        assertThrows(RuntimeException.class, () -> restaurantService.createRestaurant(dto));
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void getAllRestaurants_success(){
        Restaurant restaurant1 = new Restaurant(
                1L, "Spice Garden", "Indian food", "12 MG Road", "Bengaluru",
                "9876543210", true, LocalDateTime.now()
        );
        Restaurant restaurant2 = new Restaurant(
                2L, "Pizza Hut", "Italian food", "45 Brigade Road", "Bengaluru",
                "9876543211", true, LocalDateTime.now()
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> restaurantPage = new PageImpl<>(List.of(restaurant1, restaurant2));

        when(restaurantRepository.findAll(pageable)).thenReturn(restaurantPage);

        Page<RestaurantResponseDTO> response = restaurantService.getAllRestaurants(pageable);

        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals("Spice Garden", response.getContent().get(0).getName());
        assertEquals("Pizza Hut", response.getContent().get(1).getName());
        verify(restaurantRepository, times(1)).findAll(pageable);
    }

    @Test
    void getRestaurantById_success(){
        Restaurant restaurant = new Restaurant(
                1L, "Spice Garden", "Indian food", "12 MG Road", "Bengaluru",
                "9876543210", true, LocalDateTime.now()
        );

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        RestaurantResponseDTO response = restaurantService.getRestaurantById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Spice Garden", response.getName());
        verify(restaurantRepository, times(1)).findById(1L);
    }

    @Test
    void getRestaurantById_notFound(){
        when(restaurantRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () -> restaurantService.getRestaurantById(999L));
        verify(restaurantRepository, times(1)).findById(999L);
    }

    @Test
    void updateRestaurant_success(){
        Restaurant existingRestaurant = new Restaurant(
                1L, "Spice Garden", "Indian food", "12 MG Road", "Bengaluru",
                "9876543210", true, LocalDateTime.now()
        );

        RestaurantRequestDTO updateDto = new RestaurantRequestDTO(
                "Spice Garden Updated", "Updated description", "Updated Address", "Mumbai", "9876543212"
        );

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(existingRestaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(existingRestaurant);

        RestaurantResponseDTO response = restaurantService.updateRestaurant(1L, updateDto);

        assertNotNull(response);
        verify(restaurantRepository, times(1)).findById(1L);
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void updateRestaurant_notFound(){
        RestaurantRequestDTO updateDto = new RestaurantRequestDTO(
                "Spice Garden Updated", "Updated description", "Updated Address", "Mumbai", "9876543212"
        );

        when(restaurantRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () -> restaurantService.updateRestaurant(999L, updateDto));
        verify(restaurantRepository, times(1)).findById(999L);
        verify(restaurantRepository, never()).save(any(Restaurant.class));
    }

    @Test
    void deleteRestaurant_success(){
        when(restaurantRepository.existsById(1L)).thenReturn(true);
        doNothing().when(restaurantRepository).deleteById(1L);

        restaurantService.deleteRestaurant(1L);

        verify(restaurantRepository, times(1)).existsById(1L);
        verify(restaurantRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteRestaurant_notFound(){
        when(restaurantRepository.existsById(999L)).thenReturn(false);

        assertThrows(RestaurantNotFoundException.class, () -> restaurantService.deleteRestaurant(999L));
        verify(restaurantRepository, times(1)).existsById(999L);
        verify(restaurantRepository, never()).deleteById(anyLong());
    }

    @Test
    void getActiveRestaurantsByCity_success(){
        Restaurant restaurant1 = new Restaurant(
                1L, "Spice Garden", "Indian food", "12 MG Road", "Bengaluru",
                "9876543210", true, LocalDateTime.now()
        );
        Restaurant restaurant2 = new Restaurant(
                2L, "Pizza Hut", "Italian food", "45 Brigade Road", "Bengaluru",
                "9876543211", true, LocalDateTime.now()
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> restaurantPage = new PageImpl<>(List.of(restaurant1, restaurant2));

        when(restaurantRepository.findByCityAndActiveTrue("Bengaluru", pageable)).thenReturn(restaurantPage);

        Page<RestaurantResponseDTO> response = restaurantService.getActiveRestaurantsByCity("Bengaluru", pageable);

        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals("Bengaluru", response.getContent().get(0).getCity());
        verify(restaurantRepository, times(1)).findByCityAndActiveTrue("Bengaluru", pageable);
    }

    @Test
    void getActiveRestaurantsByCity_emptyResult(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> emptyPage = new PageImpl<>(List.of());

        when(restaurantRepository.findByCityAndActiveTrue("UnknownCity", pageable)).thenReturn(emptyPage);

        Page<RestaurantResponseDTO> response = restaurantService.getActiveRestaurantsByCity("UnknownCity", pageable);

        assertNotNull(response);
        assertEquals(0, response.getContent().size());
        verify(restaurantRepository, times(1)).findByCityAndActiveTrue("UnknownCity", pageable);
    }

    @Test
    void getRestaurantsByNameContaining_success(){
        Restaurant restaurant1 = new Restaurant(
                1L, "Spice Garden", "Indian food", "12 MG Road", "Bengaluru",
                "9876543210", true, LocalDateTime.now()
        );
        Restaurant restaurant2 = new Restaurant(
                2L, "Spice Hub", "Multi cuisine", "45 Brigade Road", "Bengaluru",
                "9876543211", true, LocalDateTime.now()
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> restaurantPage = new PageImpl<>(List.of(restaurant1, restaurant2));

        when(restaurantRepository.findByNameContainingAndActiveTrue("Spice", pageable)).thenReturn(restaurantPage);

        Page<RestaurantResponseDTO> response = restaurantService.getRestaurantsByNameContaining("Spice", pageable);

        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertTrue(response.getContent().get(0).getName().contains("Spice"));
        verify(restaurantRepository, times(1)).findByNameContainingAndActiveTrue("Spice", pageable);
    }

    @Test
    void getRestaurantsByNameContaining_emptyResult(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> emptyPage = new PageImpl<>(List.of());

        when(restaurantRepository.findByNameContainingAndActiveTrue("NonExistent", pageable)).thenReturn(emptyPage);

        Page<RestaurantResponseDTO> response = restaurantService.getRestaurantsByNameContaining("NonExistent", pageable);

        assertNotNull(response);
        assertEquals(0, response.getContent().size());
        verify(restaurantRepository, times(1)).findByNameContainingAndActiveTrue("NonExistent", pageable);
    }
}
