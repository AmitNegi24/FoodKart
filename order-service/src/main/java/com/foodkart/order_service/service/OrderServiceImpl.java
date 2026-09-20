package com.foodkart.order_service.service;

import com.foodkart.order_service.client.MenuClient;
import com.foodkart.order_service.dto.OrderItemResponseDTO;
import com.foodkart.order_service.dto.OrderRequestDTO;
import com.foodkart.order_service.dto.OrderResponseDTO;
import com.foodkart.order_service.entity.Order;
import com.foodkart.order_service.entity.OrderItem;
import com.foodkart.order_service.entity.OrderStatus;
import com.foodkart.order_service.repository.OrderRepository;
import com.foodkart.order_service.security.AuthenticatedUser;
import com.foodkart.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MenuClient foodClient;

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request) {

        // 1. Get logged-in user from JWT
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        AuthenticatedUser authenticatedUser =
                (AuthenticatedUser) authentication.getPrincipal();

        Long userId = authenticatedUser.getUserId();

        // 2. Prepare order items
        List<OrderItem> orderItems = request.getItems()
                .stream()
                .map(item -> {

                    // Get current price from Food/Menu service
                    BigDecimal price =
                            foodClient.getFoodPrice(item.getMenuItemId());

                    // Calculate item price
//                    BigDecimal itemTotal =
//                            price.multiply(
//                                    BigDecimal.valueOf(item.getQuantity())
//                            );

                    return OrderItem.builder()
                            .menuItemId(item.getMenuItemId())
                            .quantity(item.getQuantity())
                            .price(price)
                            .build();

                })
                .toList();

        // 3. Calculate complete order total
        BigDecimal totalAmount = orderItems.stream()
                .map(item ->
                        item.getPrice().multiply(
                                BigDecimal.valueOf(item.getQuantity())
                        )
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Create Order entity
        Order order = Order.builder()
                .userId(userId)
                .restaurantId(request.getRestaurantId())
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .items(orderItems)
                .build();

        // 5. Save order
        Order savedOrder = orderRepository.save(order);

        // 6. Convert entity to response DTO
        List<OrderItemResponseDTO> itemResponses =
                savedOrder.getItems()
                        .stream()
                        .map(item -> OrderItemResponseDTO.builder()
                                .menuItemId(item.getMenuItemId())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .build()
                        )
                        .toList();

        // 7. Return response
        return OrderResponseDTO.builder()
                .id(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .restaurantId(savedOrder.getRestaurantId())
                .totalAmount(savedOrder.getTotalAmount())
                .status(savedOrder.getStatus())
                .createdAt(savedOrder.getCreatedAt())
                .items(itemResponses)
                .build();
    }
}