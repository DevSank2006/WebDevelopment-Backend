package com.webdev.webdevelopment.order;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Create a new order
    @PostMapping
    public ResponseEntity<OrderEntity> createOrder(
            Authentication authentication,
            @RequestBody CreateOrderRequest request
    ) {
        OrderEntity order = orderService.createOrder(
                authentication.getName(),
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(order);
    }
    @GetMapping
    public ResponseEntity<List<MyOrderResponse>> getMyOrders(
            Authentication authentication
    ) {

        String email = authentication.getName();

        List<MyOrderResponse> orders =
                orderService.getMyOrders(email);

        return ResponseEntity.ok(orders);
    }

    // Get details of one order
    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderDetailsResponse> getOrderDetails(
            @PathVariable String orderNumber,
            Authentication authentication
    ) {

        String email = authentication.getName();

        OrderDetailsResponse order =
                orderService.getOrderDetails(orderNumber, email);

        return ResponseEntity.ok(order);
    }
}
