package com.foodKart.restaurant_service.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.foodKart.restaurant_service.dto.RestaurantRequestDTO;
import com.foodKart.restaurant_service.dto.RestaurantResponseDTO;
import com.foodKart.restaurant_service.exception.RestaurantNotFoundException;
import com.foodKart.restaurant_service.service.RestaurantService;
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

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class RestaurantControllerTest {

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private RestaurantController restaurantController;

    private RestaurantRequestDTO restaurantRequestDTO;
    private RestaurantResponseDTO restaurantResponseDTO;

    @BeforeEach
    void setUp() {
        restaurantRequestDTO = RestaurantRequestDTO.builder()
                .name("Spice Garden")
                .description("Indian food")
                .address("12 MG Road")
                .city("Bengaluru")
                .phone("9876543210")
                .build();

        restaurantResponseDTO = RestaurantResponseDTO.builder()
                .id(1L)
                .name("Spice Garden")
                .description("Indian food")
                .address("12 MG Road")
                .city("Bengaluru")
                .phone("9876543210")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createRestaurant_success() {
        when(restaurantService.createRestaurant(any(RestaurantRequestDTO.class)))
                .thenReturn(restaurantResponseDTO);

        ResponseEntity<RestaurantResponseDTO> response = restaurantController.createRestaurant(restaurantRequestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Spice Garden", response.getBody().getName());
        verify(restaurantService, times(1)).createRestaurant(any(RestaurantRequestDTO.class));
    }

    @Test
    void getAllRestaurants_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<RestaurantResponseDTO> page = new PageImpl<>(
                List.of(restaurantResponseDTO),
                pageable,
                1
        );

        when(restaurantService.getAllRestaurants(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<RestaurantResponseDTO>> response = restaurantController.getAllRestaurants(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("Spice Garden", response.getBody().getContent().get(0).getName());
        verify(restaurantService, times(1)).getAllRestaurants(any(Pageable.class));
    }

    @Test
    void getRestaurantById_success() {
        when(restaurantService.getRestaurantById(1L)).thenReturn(restaurantResponseDTO);

        ResponseEntity<RestaurantResponseDTO> response = restaurantController.getRestaurantById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Spice Garden", response.getBody().getName());
        verify(restaurantService, times(1)).getRestaurantById(1L);
    }

    @Test
    void getRestaurantById_notFound() {
        when(restaurantService.getRestaurantById(999L))
                .thenThrow(new RestaurantNotFoundException("Restaurant not found with id: 999"));

        assertThrows(RestaurantNotFoundException.class, () -> restaurantController.getRestaurantById(999L));
        verify(restaurantService, times(1)).getRestaurantById(999L);
    }

    @Test
    void updateRestaurant_success() {
        RestaurantResponseDTO updatedResponse = RestaurantResponseDTO.builder()
                .id(1L)
                .name("Spice Garden Updated")
                .description("Updated description")
                .address("Updated Address")
                .city("Mumbai")
                .phone("9876543212")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(restaurantService.updateRestaurant(eq(1L), any(RestaurantRequestDTO.class)))
                .thenReturn(updatedResponse);

        ResponseEntity<RestaurantResponseDTO> response = restaurantController.updateRestaurant(1L, restaurantRequestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Spice Garden Updated", response.getBody().getName());
        assertEquals("Mumbai", response.getBody().getCity());
        verify(restaurantService, times(1)).updateRestaurant(eq(1L), any(RestaurantRequestDTO.class));
    }

    @Test
    void updateRestaurant_notFound() {
        when(restaurantService.updateRestaurant(eq(999L), any(RestaurantRequestDTO.class)))
                .thenThrow(new RestaurantNotFoundException("Restaurant not found with id: 999"));

        assertThrows(RestaurantNotFoundException.class, () -> restaurantController.updateRestaurant(999L, restaurantRequestDTO));
        verify(restaurantService, times(1)).updateRestaurant(eq(999L), any(RestaurantRequestDTO.class));
    }

    @Test
    void deleteRestaurant_success() {
        doNothing().when(restaurantService).deleteRestaurant(1L);

        ResponseEntity<Void> response = restaurantController.deleteRestaurant(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(restaurantService, times(1)).deleteRestaurant(1L);
    }

    @Test
    void deleteRestaurant_notFound() {
        doThrow(new RestaurantNotFoundException("Restaurant not found with id: 999"))
                .when(restaurantService).deleteRestaurant(999L);

        assertThrows(RestaurantNotFoundException.class, () -> restaurantController.deleteRestaurant(999L));
        verify(restaurantService, times(1)).deleteRestaurant(999L);
    }

    @Test
    void getActiveRestaurantByCity_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<RestaurantResponseDTO> page = new PageImpl<>(
                List.of(restaurantResponseDTO),
                pageable,
                1
        );

        when(restaurantService.getActiveRestaurantsByCity(eq("Bengaluru"), any(Pageable.class)))
                .thenReturn(page);

        ResponseEntity<Page<RestaurantResponseDTO>> response = restaurantController.getActiveRestaurantByCity("Bengaluru", pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("Bengaluru", response.getBody().getContent().get(0).getCity());
        verify(restaurantService, times(1)).getActiveRestaurantsByCity(eq("Bengaluru"), any(Pageable.class));
    }

    @Test
    void getActiveRestaurantByCity_emptyResult() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<RestaurantResponseDTO> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(restaurantService.getActiveRestaurantsByCity(eq("UnknownCity"), any(Pageable.class)))
                .thenReturn(emptyPage);

        ResponseEntity<Page<RestaurantResponseDTO>> response = restaurantController.getActiveRestaurantByCity("UnknownCity", pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getContent().size());
        verify(restaurantService, times(1)).getActiveRestaurantsByCity(eq("UnknownCity"), any(Pageable.class));
    }

    @Test
    void getRestaurantsByContainingName_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<RestaurantResponseDTO> page = new PageImpl<>(
                List.of(restaurantResponseDTO),
                pageable,
                1
        );

        when(restaurantService.getRestaurantsByNameContaining(eq("Spice"), any(Pageable.class)))
                .thenReturn(page);

        ResponseEntity<Page<RestaurantResponseDTO>> response = restaurantController.getRestaurantsByContainingName("Spice", pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("Spice Garden", response.getBody().getContent().get(0).getName());
        verify(restaurantService, times(1)).getRestaurantsByNameContaining(eq("Spice"), any(Pageable.class));
    }

    @Test
    void getRestaurantsByContainingName_emptyResult() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<RestaurantResponseDTO> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(restaurantService.getRestaurantsByNameContaining(eq("NonExistent"), any(Pageable.class)))
                .thenReturn(emptyPage);

        ResponseEntity<Page<RestaurantResponseDTO>> response = restaurantController.getRestaurantsByContainingName("NonExistent", pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getContent().size());
        verify(restaurantService, times(1)).getRestaurantsByNameContaining(eq("NonExistent"), any(Pageable.class));
    }
}
