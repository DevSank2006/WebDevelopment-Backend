package com.webdev.webdevelopment.order;

import com.webdev.webdevelopment.Services.ServiceEntity;
import com.webdev.webdevelopment.Services.ServiceRepository;
import com.webdev.webdevelopment.User.User;

import com.webdev.webdevelopment.auth.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;


    private String generateOrderNumber() {

        String year = String.valueOf(LocalDateTime.now().getYear());

        String uniquePart = java.util.UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        return "AP-" + year + "-" + uniquePart;
    }

    @Transactional
    public OrderEntity createOrder(
            String email,
            CreateOrderRequest request
    ) {

        // 1. Find the logged-in customer
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // 2. Create a new order
        OrderEntity order = new OrderEntity();

        order.setUser(user);
        order.setStatus("PENDING");
        order.setPaymentStatus("PENDING");
        order.setOrderNumber(generateOrderNumber());
        order.setProjectDescription(request.projectDescription());
        order.setReferenceWebsite(request.referenceWebsite());

        BigDecimal totalAmount = BigDecimal.ZERO;
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        // 3. Process each purchased service
        for (OrderItemRequest itemRequest : request.items()) {

            // Find service from database
            ServiceEntity service = serviceRepository
                    .findById(itemRequest.serviceId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Service not found: "
                                            + itemRequest.serviceId()
                            ));
            if (itemRequest.quantity() == null || itemRequest.quantity() < 1) {
                throw new IllegalArgumentException("Quantity must be at least 1");
            }


            // Get actual price from database
            BigDecimal price = service.getPrice();

            // Calculate subtotal
            BigDecimal subtotal = price.multiply(
                    BigDecimal.valueOf(itemRequest.quantity())
            );

            // 4. Create order item
            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setService(service);
            orderItem.setServiceTitle(service.getTitle());
            orderItem.setPrice(price);
            orderItem.setQuantity(itemRequest.quantity());
            orderItem.setSubtotal(subtotal);

            // Add item to order
            order.getItems().add(orderItem);

            // Add subtotal to total
            totalAmount = totalAmount.add(subtotal);
        }

        // 5. Set total amount
        order.setTotalAmount(totalAmount);

        // 6. Save order and its items
        return orderRepository.save(order);
    }


    @Transactional(readOnly = true)
    public List<MyOrderResponse> getMyOrders(String email) {

        // 1. Find only this user's orders
        List<OrderEntity> orders =
                orderRepository.findByUser_EmailOrderByCreatedAtDesc(email);

        // 2. Convert each order into a response DTO
        return orders.stream()
                .map(order -> {

                    // 3. Get items inside this order
                    List<MyOrderResponse.OrderItemSummary> items =
                            order.getItems()
                                    .stream()
                                    .map(item ->
                                            new MyOrderResponse.OrderItemSummary(
                                                    item.getServiceTitle(),
                                                    item.getQuantity()
                                            )
                                    )
                                    .toList();

                    // 4. Return the order response
                    return new MyOrderResponse(
                            order.getId(),
                            order.getOrderNumber(),
                            order.getCreatedAt(),
                            items,
                            order.getTotalAmount(),
                            order.getPaymentStatus(),
                            order.getStatus()
                    );
                })
                .toList();
    }
    @Transactional(readOnly = true)
    public OrderDetailsResponse getOrderDetails(
            String orderNumber,
            String email
    ) {

        // 1. Find the order belonging to the logged-in user
        OrderEntity order = orderRepository
                .findByOrderNumberAndUser_Email(orderNumber, email)
                .orElseThrow(() ->
                        new RuntimeException("Order not found")
                );

        // 2. Get all purchased packages from OrderItem
        List<OrderDetailsResponse.PurchasedPackage> packages =
                order.getItems().stream()
                        .map(item -> new OrderDetailsResponse.PurchasedPackage(
                                item.getService().getId(),
                                item.getServiceTitle(),
                                item.getQuantity(),
                                item.getPrice(),
                                item.getSubtotal()
                        ))
                        .toList();

        // 3. Return complete order details
        return new OrderDetailsResponse(
                order.getOrderNumber(),
                order.getItems().size(),
                order.getCreatedAt(),
                order.getStatus(),
                packages,
                order.getPaymentStatus(),
                order.getPaymentMethod(),
                order.getTransactionId(),
                order.getProjectDescription(),
                order.getReferenceWebsite(),
                order.getTotalAmount()
        );
    }
}