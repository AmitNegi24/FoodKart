package com.foodkart.order_service.service;

import com.foodkart.order_service.client.MenuClient;
import com.foodkart.order_service.dto.*;
import com.foodkart.order_service.entity.Order;
import com.foodkart.order_service.entity.OrderItem;
import com.foodkart.order_service.entity.OrderStatus;
import com.foodkart.order_service.kafka.OrderEventProducer;
import com.foodkart.order_service.repository.OrderRepository;
import com.foodkart.order_service.security.AuthenticatedUser;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;
    private final MenuClient menuClient;

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request) {

        // 1. Get logged-in user from JWT
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("User not authenticated");
        }

        AuthenticatedUser authenticatedUser =
                (AuthenticatedUser) authentication.getPrincipal();

        Long userId = authenticatedUser.getUserId();

        // 2. Create Order
        Order order = Order.builder()
                .userId(userId)
                .restaurantId(request.getRestaurantId())
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 3. Process each menu item
        for (var itemRequest : request.getItems()) {

            // Get menu item from Menu Service
            MenuItemDTO menuItem =
                    menuClient.getMenuItemById(itemRequest.getMenuItemId());

            // 4. Check availability
            if (!menuItem.isAvailable()) {
                throw new RuntimeException(
                        "Menu item is not available: " + menuItem.getName()
                );
            }

            // 5. Check restaurant
            if (!menuItem.getRestaurantId()
                    .equals(request.getRestaurantId())) {

                throw new RuntimeException(
                        "Menu item does not belong to this restaurant: "
                                + menuItem.getName()
                );
            }

            // 6. Calculate subtotal
            BigDecimal subtotal =
                    menuItem.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(itemRequest.getQuantity())
                            );

            // 7. Create OrderItem
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuItemId(menuItem.getId())
                    .itemName(menuItem.getName())
                    .price(menuItem.getPrice())
                    .quantity(itemRequest.getQuantity())
                    .subtotal(subtotal)
                    .build();

            order.getItems().add(orderItem);

            // 8. Add to order total
            totalAmount = totalAmount.add(subtotal);
        }

        // 9. Set total amount
        order.setTotalAmount(totalAmount);

        // 10. Save Order
        Order savedOrder = orderRepository.save(order);

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .amount(savedOrder.getTotalAmount())
                .build();

        orderEventProducer.publishOrderCreated(event);

        // 11. Convert OrderItems to response DTO
        List<OrderItemResponseDTO> itemResponses =
                savedOrder.getItems()
                        .stream()
                        .map(item -> OrderItemResponseDTO.builder()
                                .menuItemId(item.getMenuItemId())
                                .itemName(item.getItemName())
                                .price(item.getPrice())
                                .quantity(item.getQuantity())
                                .subtotal(item.getSubtotal())
                                .build()
                        )
                        .toList();

        // 12. Return response
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