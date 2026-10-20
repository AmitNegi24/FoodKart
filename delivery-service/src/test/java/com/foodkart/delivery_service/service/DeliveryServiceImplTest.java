package com.foodkart.delivery_service.service;

import com.foodkart.delivery_service.dto.DeliveryResponseDTO;
import com.foodkart.delivery_service.entity.Delivery;
import com.foodkart.delivery_service.entity.DeliveryPartner;
import com.foodkart.delivery_service.entity.DeliveryStatus;
import com.foodkart.delivery_service.kafka.DeliveryStatusEventProducer;
import com.foodkart.delivery_service.repository.DeliveryPartnerRepository;
import com.foodkart.delivery_service.repository.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeliveryServiceImplTest {

    private DeliveryRepository deliveryRepository;
    private DeliveryPartnerRepository deliveryPartnerRepository;
    private DeliveryStatusEventProducer deliveryStatusEventProducer;
    private DeliveryServiceImpl deliveryService;

    private Delivery delivery;
    private DeliveryPartner partner;

    @BeforeEach
    void setUp() {
        deliveryRepository = mock(DeliveryRepository.class);
        deliveryPartnerRepository = mock(DeliveryPartnerRepository.class);
        deliveryStatusEventProducer = mock(DeliveryStatusEventProducer.class);
        deliveryService = new DeliveryServiceImpl(
                deliveryRepository,
                deliveryPartnerRepository,
                deliveryStatusEventProducer
        );

        partner = DeliveryPartner.builder()
                .id(10L)
                .name("John Doe")
                .phone("1234567890")
                .available(true)
                .build();

        delivery = Delivery.builder()
                .id(1L)
                .orderId(101L)
                .status(DeliveryStatus.ASSIGNED)
                .deliveryPartner(partner)
                .build();
    }

    @Test
    void testCreateDelivery_success() {
        doAnswer(invocation -> null).when(deliveryRepository).insertIfAbsent(101L);
        when(deliveryRepository.findByOrderId(101L)).thenReturn(Optional.of(delivery));

        DeliveryResponseDTO response = deliveryService.createDelivery(101L);

        assertNotNull(response);
        assertEquals(101L, response.getOrderId());
        assertEquals(DeliveryStatus.ASSIGNED, response.getStatus());
        verify(deliveryRepository, times(1)).insertIfAbsent(101L);
        verify(deliveryRepository, times(1)).findByOrderId(101L);
    }

    @Test
    void testCreateDelivery_alreadyExists() {
        doAnswer(invocation -> null).when(deliveryRepository).insertIfAbsent(101L);
        when(deliveryRepository.findByOrderId(101L)).thenReturn(Optional.of(delivery));

        DeliveryResponseDTO response = deliveryService.createDelivery(101L);

        assertNotNull(response);
        assertEquals(101L, response.getOrderId());
        verify(deliveryRepository, times(1)).insertIfAbsent(101L);
    }

    @Test
    void testCreateDelivery_failure() {
        doAnswer(invocation -> null).when(deliveryRepository).insertIfAbsent(101L);
        when(deliveryRepository.findByOrderId(101L)).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> deliveryService.createDelivery(101L)
        );

        assertEquals("Delivery could not be created for orderId: 101", exception.getMessage());
        verify(deliveryRepository, times(1)).insertIfAbsent(101L);
        verify(deliveryRepository, times(1)).findByOrderId(101L);
    }

    @Test
    void testGetDeliveryByOrderId_success() {
        when(deliveryRepository.findByOrderId(101L)).thenReturn(Optional.of(delivery));

        DeliveryResponseDTO response = deliveryService.getDeliveryByOrderId(101L);

        assertNotNull(response);
        assertEquals(1L, response.getDeliveryId());
        assertEquals(101L, response.getOrderId());
        assertEquals(DeliveryStatus.ASSIGNED, response.getStatus());
        assertEquals(10L, response.getDeliveryPartnerId());
        assertEquals("John Doe", response.getDeliveryPartnerName());
        verify(deliveryRepository, times(1)).findByOrderId(101L);
    }

    @Test
    void testGetDeliveryByOrderId_notFound() {
        when(deliveryRepository.findByOrderId(101L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> deliveryService.getDeliveryByOrderId(101L)
        );

        assertEquals("Delivery not found for orderId: 101", exception.getMessage());
        verify(deliveryRepository, times(1)).findByOrderId(101L);
    }

    @Test
    void testUpdateDeliveryStatus_success() {
        when(deliveryRepository.findByOrderId(101L)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        DeliveryResponseDTO response = deliveryService.updateDeliveryStatus(
                101L,
                DeliveryStatus.PICKED_UP
        );

        assertNotNull(response);
        assertEquals(DeliveryStatus.PICKED_UP, delivery.getStatus());
        verify(deliveryRepository, times(1)).findByOrderId(101L);
        verify(deliveryRepository, times(1)).save(any(Delivery.class));
        verify(deliveryStatusEventProducer, times(1)).publishStatusUpdate(eq(101L), eq("PICKED_UP"));
    }

    @Test
    void testUpdateDeliveryStatus_delivered_releasesPartner() {
        delivery.setStatus(DeliveryStatus.OUT_FOR_DELIVERY);
        when(deliveryRepository.findByOrderId(101L)).thenReturn(Optional.of(delivery));
        when(deliveryPartnerRepository.save(any(DeliveryPartner.class))).thenReturn(partner);
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        deliveryService.updateDeliveryStatus(101L, DeliveryStatus.DELIVERED);

        assertTrue(partner.isAvailable());
        verify(deliveryPartnerRepository, times(1)).save(any(DeliveryPartner.class));
        verify(deliveryStatusEventProducer, times(1)).publishStatusUpdate(eq(101L), eq("DELIVERED"));
    }

    @Test
    void testUpdateDeliveryStatus_cancelled_releasesPartner() {
        when(deliveryRepository.findByOrderId(101L)).thenReturn(Optional.of(delivery));
        when(deliveryPartnerRepository.save(any(DeliveryPartner.class))).thenReturn(partner);
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        deliveryService.updateDeliveryStatus(101L, DeliveryStatus.CANCELLED);

        assertTrue(partner.isAvailable());
        verify(deliveryPartnerRepository, times(1)).save(any(DeliveryPartner.class));
        verify(deliveryStatusEventProducer, times(1)).publishStatusUpdate(eq(101L), eq("CANCELLED"));
    }

    @Test
    void testUpdateDeliveryStatus_noPartner() {
        delivery.setDeliveryPartner(null);
        when(deliveryRepository.findByOrderId(101L)).thenReturn(Optional.of(delivery));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> deliveryService.updateDeliveryStatus(101L, DeliveryStatus.PICKED_UP)
        );

        assertEquals("Cannot update delivery status without an assigned delivery partner", exception.getMessage());
        verify(deliveryRepository, times(1)).findByOrderId(101L);
        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void testUpdateDeliveryStatus_invalidTransition() {
        delivery.setStatus(DeliveryStatus.DELIVERED);
        when(deliveryRepository.findByOrderId(101L)).thenReturn(Optional.of(delivery));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> deliveryService.updateDeliveryStatus(101L, DeliveryStatus.PICKED_UP)
        );

        assertTrue(exception.getMessage().contains("Invalid delivery status transition"));
        verify(deliveryRepository, times(1)).findByOrderId(101L);
        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void testAssignPartner_success() {
        delivery.setDeliveryPartner(null);
        when(deliveryRepository.findByOrderIdForUpdate(101L)).thenReturn(Optional.of(delivery));
        when(deliveryPartnerRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(partner));
        when(deliveryPartnerRepository.save(any(DeliveryPartner.class))).thenReturn(partner);
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        DeliveryResponseDTO response = deliveryService.assignPartner(101L, 10L);

        assertNotNull(response);
        assertFalse(partner.isAvailable());
        assertEquals(partner, delivery.getDeliveryPartner());
        verify(deliveryRepository, times(1)).findByOrderIdForUpdate(101L);
        verify(deliveryPartnerRepository, times(1)).findByIdForUpdate(10L);
        verify(deliveryPartnerRepository, times(1)).save(any(DeliveryPartner.class));
        verify(deliveryRepository, times(1)).save(any(Delivery.class));
    }

    @Test
    void testAssignPartner_alreadyAssigned() {
        when(deliveryRepository.findByOrderIdForUpdate(101L)).thenReturn(Optional.of(delivery));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> deliveryService.assignPartner(101L, 10L)
        );

        assertEquals("A partner is already assigned to this delivery", exception.getMessage());
        verify(deliveryRepository, times(1)).findByOrderIdForUpdate(101L);
        verify(deliveryPartnerRepository, never()).findByIdForUpdate(anyLong());
    }

    @Test
    void testAssignPartner_wrongStatus() {
        delivery.setDeliveryPartner(null);
        delivery.setStatus(DeliveryStatus.PICKED_UP);
        when(deliveryRepository.findByOrderIdForUpdate(101L)).thenReturn(Optional.of(delivery));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> deliveryService.assignPartner(101L, 10L)
        );

        assertEquals("Partner can only be assigned to a delivery in ASSIGNED status", exception.getMessage());
        verify(deliveryRepository, times(1)).findByOrderIdForUpdate(101L);
        verify(deliveryPartnerRepository, never()).findByIdForUpdate(anyLong());
    }

    @Test
    void testAssignPartner_notFound() {
        delivery.setDeliveryPartner(null);
        when(deliveryRepository.findByOrderIdForUpdate(101L)).thenReturn(Optional.of(delivery));
        when(deliveryPartnerRepository.findByIdForUpdate(10L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> deliveryService.assignPartner(101L, 10L)
        );

        assertEquals("Delivery partner not found: 10", exception.getMessage());
        verify(deliveryRepository, times(1)).findByOrderIdForUpdate(101L);
        verify(deliveryPartnerRepository, times(1)).findByIdForUpdate(10L);
    }

    @Test
    void testAssignPartner_notAvailable() {
        delivery.setDeliveryPartner(null);
        partner.setAvailable(false);
        when(deliveryRepository.findByOrderIdForUpdate(101L)).thenReturn(Optional.of(delivery));
        when(deliveryPartnerRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(partner));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> deliveryService.assignPartner(101L, 10L)
        );

        assertEquals("Delivery partner is not available", exception.getMessage());
        verify(deliveryRepository, times(1)).findByOrderIdForUpdate(101L);
        verify(deliveryPartnerRepository, times(1)).findByIdForUpdate(10L);
    }
}
