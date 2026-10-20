package com.foodkart.delivery_service.controller;

import com.foodkart.delivery_service.dto.AssignDeliveryPartnerRequestDTO;
import com.foodkart.delivery_service.dto.DeliveryResponseDTO;
import com.foodkart.delivery_service.dto.UpdateDeliveryStatusRequestDTO;
import com.foodkart.delivery_service.entity.DeliveryStatus;
import com.foodkart.delivery_service.service.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DeliveryControllerTest {

    private DeliveryService deliveryService;
    private DeliveryController deliveryController;

    @BeforeEach
    void setUp() {
        deliveryService = mock(DeliveryService.class);
        deliveryController = new DeliveryController(deliveryService);
    }

    @Test
    void testGetDeliveryByOrderId_success() {
        DeliveryResponseDTO responseDTO = DeliveryResponseDTO.builder()
                .deliveryId(1L)
                .orderId(101L)
                .status(DeliveryStatus.ASSIGNED)
                .deliveryPartnerId(10L)
                .deliveryPartnerName("John Doe")
                .build();

        when(deliveryService.getDeliveryByOrderId(101L)).thenReturn(responseDTO);

        ResponseEntity<DeliveryResponseDTO> response =
                deliveryController.getDeliveryByOrderId(101L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(deliveryService, times(1)).getDeliveryByOrderId(101L);
    }

    @Test
    void testGetDeliveryByOrderId_differentOrderId() {
        DeliveryResponseDTO responseDTO = DeliveryResponseDTO.builder()
                .deliveryId(2L)
                .orderId(202L)
                .status(DeliveryStatus.PICKED_UP)
                .build();

        when(deliveryService.getDeliveryByOrderId(202L)).thenReturn(responseDTO);

        ResponseEntity<DeliveryResponseDTO> response =
                deliveryController.getDeliveryByOrderId(202L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(deliveryService, times(1)).getDeliveryByOrderId(202L);
    }

    @Test
    void testUpdateDeliveryStatus_success() {
        UpdateDeliveryStatusRequestDTO request = UpdateDeliveryStatusRequestDTO.builder()
                .status(DeliveryStatus.OUT_FOR_DELIVERY)
                .build();

        DeliveryResponseDTO responseDTO = DeliveryResponseDTO.builder()
                .deliveryId(1L)
                .orderId(101L)
                .status(DeliveryStatus.OUT_FOR_DELIVERY)
                .build();

        when(deliveryService.updateDeliveryStatus(eq(101L), eq(DeliveryStatus.OUT_FOR_DELIVERY)))
                .thenReturn(responseDTO);

        ResponseEntity<DeliveryResponseDTO> response =
                deliveryController.updateDeliveryStatus(101L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(deliveryService, times(1)).updateDeliveryStatus(eq(101L), eq(DeliveryStatus.OUT_FOR_DELIVERY));
    }

    @Test
    void testUpdateDeliveryStatus_differentStatus() {
        UpdateDeliveryStatusRequestDTO request = UpdateDeliveryStatusRequestDTO.builder()
                .status(DeliveryStatus.DELIVERED)
                .build();

        DeliveryResponseDTO responseDTO = DeliveryResponseDTO.builder()
                .deliveryId(1L)
                .orderId(101L)
                .status(DeliveryStatus.DELIVERED)
                .build();

        when(deliveryService.updateDeliveryStatus(eq(101L), eq(DeliveryStatus.DELIVERED)))
                .thenReturn(responseDTO);

        ResponseEntity<DeliveryResponseDTO> response =
                deliveryController.updateDeliveryStatus(101L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(deliveryService, times(1)).updateDeliveryStatus(eq(101L), eq(DeliveryStatus.DELIVERED));
    }

    @Test
    void testAssignPartner_success() {
        AssignDeliveryPartnerRequestDTO request = new AssignDeliveryPartnerRequestDTO();
        request.setDeliveryPartnerId(10L);

        DeliveryResponseDTO responseDTO = DeliveryResponseDTO.builder()
                .deliveryId(1L)
                .orderId(101L)
                .status(DeliveryStatus.ASSIGNED)
                .deliveryPartnerId(10L)
                .deliveryPartnerName("John Doe")
                .build();

        when(deliveryService.assignPartner(eq(101L), eq(10L))).thenReturn(responseDTO);

        ResponseEntity<DeliveryResponseDTO> response =
                deliveryController.assignPartner(101L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(deliveryService, times(1)).assignPartner(eq(101L), eq(10L));
    }

    @Test
    void testAssignPartner_differentPartner() {
        AssignDeliveryPartnerRequestDTO request = new AssignDeliveryPartnerRequestDTO();
        request.setDeliveryPartnerId(20L);

        DeliveryResponseDTO responseDTO = DeliveryResponseDTO.builder()
                .deliveryId(1L)
                .orderId(101L)
                .status(DeliveryStatus.ASSIGNED)
                .deliveryPartnerId(20L)
                .deliveryPartnerName("Jane Smith")
                .build();

        when(deliveryService.assignPartner(eq(101L), eq(20L))).thenReturn(responseDTO);

        ResponseEntity<DeliveryResponseDTO> response =
                deliveryController.assignPartner(101L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(deliveryService, times(1)).assignPartner(eq(101L), eq(20L));
    }
}
