package com.foodkart.delivery_service.kafka;

import com.foodkart.delivery_service.dto.DeliveryResponseDTO;
import com.foodkart.delivery_service.dto.PaymentSuccessEvent;
import com.foodkart.delivery_service.service.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PaymentSuccessConsumerTest {

    private ObjectMapper objectMapper;
    private DeliveryService deliveryService;
    private PaymentSuccessConsumer paymentSuccessConsumer;

    private PaymentSuccessEvent paymentSuccessEvent;

    @BeforeEach
    void setUp() {
        objectMapper = mock(ObjectMapper.class);
        deliveryService = mock(DeliveryService.class);
        paymentSuccessConsumer = new PaymentSuccessConsumer(objectMapper, deliveryService);

        paymentSuccessEvent = PaymentSuccessEvent.builder()
                .paymentId(1L)
                .orderId(101L)
                .userEmailId("test@example.com")
                .amount(new BigDecimal("100.00"))
                .correlationId("corr-123")
                .build();
    }

    @Test
    void testConsumePaymentSuccess_success() throws Exception {
        when(objectMapper.readValue(anyString(), eq(PaymentSuccessEvent.class)))
                .thenReturn(paymentSuccessEvent);

        DeliveryResponseDTO responseDTO = DeliveryResponseDTO.builder()
                .deliveryId(1L)
                .orderId(101L)
                .build();

        when(deliveryService.createDelivery(101L)).thenReturn(responseDTO);

        assertDoesNotThrow(() ->
                paymentSuccessConsumer.consumePaymentSuccess("{\"orderId\":101}")
        );

        verify(deliveryService, times(1)).createDelivery(101L);
    }

    @Test
    void testConsumePaymentSuccess_differentOrderId() throws Exception {
        PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                .paymentId(2L)
                .orderId(202L)
                .userEmailId("user@example.com")
                .amount(new BigDecimal("200.00"))
                .correlationId("corr-456")
                .build();

        when(objectMapper.readValue(anyString(), eq(PaymentSuccessEvent.class)))
                .thenReturn(event);

        DeliveryResponseDTO responseDTO = DeliveryResponseDTO.builder()
                .deliveryId(2L)
                .orderId(202L)
                .build();

        when(deliveryService.createDelivery(202L)).thenReturn(responseDTO);

        assertDoesNotThrow(() ->
                paymentSuccessConsumer.consumePaymentSuccess("{\"orderId\":202}")
        );

        verify(deliveryService, times(1)).createDelivery(202L);
    }

    @Test
    void testConsumePaymentSuccess_deserializationError() throws Exception {
        when(objectMapper.readValue(anyString(), eq(PaymentSuccessEvent.class)))
                .thenThrow(new RuntimeException("Deserialization error"));

        assertThrows(
                RuntimeException.class,
                () -> paymentSuccessConsumer.consumePaymentSuccess("invalid json")
        );

        verify(deliveryService, never()).createDelivery(anyLong());
    }

    @Test
    void testConsumePaymentSuccess_serviceError() throws Exception {
        when(objectMapper.readValue(anyString(), eq(PaymentSuccessEvent.class)))
                .thenReturn(paymentSuccessEvent);

        when(deliveryService.createDelivery(101L))
                .thenThrow(new RuntimeException("Service error"));

        assertThrows(
                RuntimeException.class,
                () -> paymentSuccessConsumer.consumePaymentSuccess("{\"orderId\":101}")
        );
    }
}
