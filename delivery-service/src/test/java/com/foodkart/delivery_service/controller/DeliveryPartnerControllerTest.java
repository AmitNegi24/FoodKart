package com.foodkart.delivery_service.controller;

import com.foodkart.delivery_service.dto.CreateDeliveryPartnerRequestDTO;
import com.foodkart.delivery_service.dto.DeliveryPartnerResponseDTO;
import com.foodkart.delivery_service.service.DeliveryPartnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeliveryPartnerControllerTest {

    private DeliveryPartnerService deliveryPartnerService;
    private DeliveryPartnerController deliveryPartnerController;

    @BeforeEach
    void setUp() {
        deliveryPartnerService = mock(DeliveryPartnerService.class);
        deliveryPartnerController = new DeliveryPartnerController(deliveryPartnerService);
    }

    @Test
    void testRegisterPartner_success() {
        CreateDeliveryPartnerRequestDTO request = new CreateDeliveryPartnerRequestDTO();
        request.setName("John Doe");
        request.setPhone("1234567890");

        DeliveryPartnerResponseDTO responseDTO = DeliveryPartnerResponseDTO.builder()
                .id(1L)
                .name("John Doe")
                .phone("1234567890")
                .available(true)
                .build();

        when(deliveryPartnerService.registerPartner(any(CreateDeliveryPartnerRequestDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<DeliveryPartnerResponseDTO> response =
                deliveryPartnerController.registerPartner(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(deliveryPartnerService, times(1)).registerPartner(request);
    }

    @Test
    void testRegisterPartner_differentPartner() {
        CreateDeliveryPartnerRequestDTO request = new CreateDeliveryPartnerRequestDTO();
        request.setName("Jane Smith");
        request.setPhone("9876543210");

        DeliveryPartnerResponseDTO responseDTO = DeliveryPartnerResponseDTO.builder()
                .id(2L)
                .name("Jane Smith")
                .phone("9876543210")
                .available(true)
                .build();

        when(deliveryPartnerService.registerPartner(any(CreateDeliveryPartnerRequestDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<DeliveryPartnerResponseDTO> response =
                deliveryPartnerController.registerPartner(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(deliveryPartnerService, times(1)).registerPartner(request);
    }

    @Test
    void testRegisterPartner_unavailablePartner() {
        CreateDeliveryPartnerRequestDTO request = new CreateDeliveryPartnerRequestDTO();
        request.setName("Bob Johnson");
        request.setPhone("5555555555");

        DeliveryPartnerResponseDTO responseDTO = DeliveryPartnerResponseDTO.builder()
                .id(3L)
                .name("Bob Johnson")
                .phone("5555555555")
                .available(false)
                .build();

        when(deliveryPartnerService.registerPartner(any(CreateDeliveryPartnerRequestDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<DeliveryPartnerResponseDTO> response =
                deliveryPartnerController.registerPartner(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(deliveryPartnerService, times(1)).registerPartner(request);
    }
}
