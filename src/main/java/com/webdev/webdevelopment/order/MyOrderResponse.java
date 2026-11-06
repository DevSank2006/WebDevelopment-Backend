package com.webdev.webdevelopment.order;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MyOrderResponse {

    private Long id;
    private String orderNumber;
    private LocalDateTime date;

    private List<OrderItemSummary> items;

    private BigDecimal total;
    private String payment;
    private String status;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class OrderItemSummary {
        private String name;
        private Integer quantity;
    }
}