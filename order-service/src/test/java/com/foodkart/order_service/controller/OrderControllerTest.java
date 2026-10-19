package com.foodkart.order_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import tools.jackson.databind.ObjectMapper;
import com.foodkart.order_service.dto.OrderItemRequestDTO;
import com.foodkart.order_service.dto.OrderRequestDTO;
import com.foodkart.order_service.dto.OrderResponseDTO;
import com.foodkart.order_service.entity.OrderStatus;
import com.foodkart.order_service.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private OrderRequestDTO orderRequestDTO;
    private OrderResponseDTO orderResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper();

        OrderItemRequestDTO item = new OrderItemRequestDTO();
        item.setMenuItemId(1L);
        item.setQuantity(2);

        orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setRestaurantId(100L);
        orderRequestDTO.setItems(List.of(item));

        orderResponseDTO = OrderResponseDTO.builder()
                .id(1L)
                .userEmailId("test@example.com")
                .restaurantId(100L)
                .totalAmount(new BigDecimal("20.00"))
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .items(List.of())
                .build();
    }

    @Test
    void createOrder_success() throws JsonProcessingException {
        when(orderService.createOrder(any(OrderRequestDTO.class), any(String.class)))
                .thenReturn(orderResponseDTO);

        ResponseEntity<OrderResponseDTO> result = orderController.createOrder(orderRequestDTO, "correlation-123");

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(1L, result.getBody().getId());
        assertEquals("test@example.com", result.getBody().getUserEmailId());
        verify(orderService, times(1)).createOrder(orderRequestDTO, "correlation-123");
    }

    @Test
    void createOrder_withNullCorrelationId() throws JsonProcessingException {
        when(orderService.createOrder(any(OrderRequestDTO.class), isNull()))
                .thenReturn(orderResponseDTO);

        ResponseEntity<OrderResponseDTO> result = orderController.createOrder(orderRequestDTO, null);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(orderService, times(1)).createOrder(orderRequestDTO, null);
    }

    @Test
    void getOrderById_success() {
        when(orderService.getOrderById(1L)).thenReturn(orderResponseDTO);

        ResponseEntity<OrderResponseDTO> result = orderController.getOrderById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().getId());
        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    void getOrderById_notFound_throwsException() {
        when(orderService.getOrderById(999L))
                .thenThrow(new RuntimeException("Order not found"));

        assertThrows(RuntimeException.class, () -> orderController.getOrderById(999L));
        verify(orderService, times(1)).getOrderById(999L);
    }

    @Test
    void startPreparing_success() {
        OrderResponseDTO preparingOrder = OrderResponseDTO.builder()
                .id(1L)
                .userEmailId("test@example.com")
                .restaurantId(100L)
                .totalAmount(new BigDecimal("20.00"))
                .status(OrderStatus.PREPARING)
                .createdAt(LocalDateTime.now())
                .items(List.of())
                .build();

        when(orderService.updateOrderStatus(1L, OrderStatus.PREPARING))
                .thenReturn(preparingOrder);

        ResponseEntity<OrderResponseDTO> result = orderController.startPreparing(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(OrderStatus.PREPARING, result.getBody().getStatus());
        verify(orderService, times(1)).updateOrderStatus(1L, OrderStatus.PREPARING);
    }

    @Test
    void markReadyForPickup_success() {
        OrderResponseDTO readyOrder = OrderResponseDTO.builder()
                .id(1L)
                .userEmailId("test@example.com")
                .restaurantId(100L)
                .totalAmount(new BigDecimal("20.00"))
                .status(OrderStatus.READY_FOR_PICKUP)
                .createdAt(LocalDateTime.now())
                .items(List.of())
                .build();

        when(orderService.updateOrderStatus(1L, OrderStatus.READY_FOR_PICKUP))
                .thenReturn(readyOrder);

        ResponseEntity<OrderResponseDTO> result = orderController.markReadyForPickup(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(OrderStatus.READY_FOR_PICKUP, result.getBody().getStatus());
        verify(orderService, times(1)).updateOrderStatus(1L, OrderStatus.READY_FOR_PICKUP);
    }

    @Test
    void createOrder_withMockMvc_success() throws Exception {
        when(orderService.createOrder(any(OrderRequestDTO.class), any(String.class)))
                .thenReturn(orderResponseDTO);

        mockMvc.perform(post("/orders/createOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDTO))
                        .header("X-Correlation-ID", "correlation-123"))
                .andExpect(status().isCreated());
    }

    @Test
    void getOrderById_withMockMvc_success() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(orderResponseDTO);

        mockMvc.perform(get("/orders/getOrder/1"))
                .andExpect(status().isOk());
    }

    @Test
    void startPreparing_withMockMvc_success() throws Exception {
        when(orderService.updateOrderStatus(1L, OrderStatus.PREPARING))
                .thenReturn(orderResponseDTO);

        mockMvc.perform(patch("/orders/1/start-preparing"))
                .andExpect(status().isOk());
    }

    @Test
    void markReadyForPickup_withMockMvc_success() throws Exception {
        when(orderService.updateOrderStatus(1L, OrderStatus.READY_FOR_PICKUP))
                .thenReturn(orderResponseDTO);

        mockMvc.perform(patch("/orders/1/ready-for-pickup"))
                .andExpect(status().isOk());
    }
}
