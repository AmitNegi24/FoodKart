package com.foodkart.delivery_service.service;

import com.foodkart.delivery_service.dto.CreateDeliveryPartnerRequestDTO;
import com.foodkart.delivery_service.dto.DeliveryPartnerResponseDTO;
import com.foodkart.delivery_service.entity.DeliveryPartner;
import com.foodkart.delivery_service.repository.DeliveryPartnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeliveryPartnerServiceImplTest {

    private DeliveryPartnerRepository deliveryPartnerRepository;
    private DeliveryPartnerServiceImpl deliveryPartnerService;

    private CreateDeliveryPartnerRequestDTO request;

    @BeforeEach
    void setUp() {
        deliveryPartnerRepository = mock(DeliveryPartnerRepository.class);
        deliveryPartnerService = new DeliveryPartnerServiceImpl(deliveryPartnerRepository);

        request = new CreateDeliveryPartnerRequestDTO();
        request.setName("John Doe");
        request.setPhone("1234567890");
    }

    @Test
    void testRegisterPartner_success() {
        DeliveryPartner partner = DeliveryPartner.builder()
                .id(1L)
                .name("John Doe")
                .phone("1234567890")
                .available(true)
                .build();

        when(deliveryPartnerRepository.existsByPhone("1234567890")).thenReturn(false);
        when(deliveryPartnerRepository.save(any(DeliveryPartner.class))).thenReturn(partner);

        DeliveryPartnerResponseDTO response = deliveryPartnerService.registerPartner(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John Doe", response.getName());
        assertEquals("1234567890", response.getPhone());
        assertTrue(response.isAvailable());

        ArgumentCaptor<DeliveryPartner> captor = ArgumentCaptor.forClass(DeliveryPartner.class);
        verify(deliveryPartnerRepository, times(1)).save(captor.capture());
        DeliveryPartner savedPartner = captor.getValue();
        assertEquals("John Doe", savedPartner.getName());
        assertEquals("1234567890", savedPartner.getPhone());
        assertTrue(savedPartner.isAvailable());
    }

    @Test
    void testRegisterPartner_phoneAlreadyExists() {
        when(deliveryPartnerRepository.existsByPhone("1234567890")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> deliveryPartnerService.registerPartner(request)
        );

        assertEquals("A delivery partner with this phone number already exists", exception.getMessage());
        verify(deliveryPartnerRepository, times(1)).existsByPhone("1234567890");
        verify(deliveryPartnerRepository, never()).save(any(DeliveryPartner.class));
    }

    @Test
    void testRegisterPartner_differentPartner() {
        CreateDeliveryPartnerRequestDTO differentRequest = new CreateDeliveryPartnerRequestDTO();
        differentRequest.setName("Jane Smith");
        differentRequest.setPhone("9876543210");

        DeliveryPartner partner = DeliveryPartner.builder()
                .id(2L)
                .name("Jane Smith")
                .phone("9876543210")
                .available(true)
                .build();

        when(deliveryPartnerRepository.existsByPhone("9876543210")).thenReturn(false);
        when(deliveryPartnerRepository.save(any(DeliveryPartner.class))).thenReturn(partner);

        DeliveryPartnerResponseDTO response = deliveryPartnerService.registerPartner(differentRequest);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("Jane Smith", response.getName());
        assertEquals("9876543210", response.getPhone());
        assertTrue(response.isAvailable());
    }
}
