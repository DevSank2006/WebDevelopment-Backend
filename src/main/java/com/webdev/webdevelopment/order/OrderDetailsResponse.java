package com.webdev.webdevelopment.order;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderDetailsResponse {

    private String orderNumber;
    private Integer totalPackages;
    private LocalDateTime orderDate;
    private String orderStatus;

    private List<PurchasedPackage> packages;

    private String paymentStatus;
    private String paymentMethod;
    private String transactionId;

    private BigDecimal totalAmount;

    public OrderDetailsResponse(String orderNumber, int size, LocalDateTime createdAt, String status, List<PurchasedPackage> packages, String paymentStatus, String paymentMethod, String transactionId, String projectDescription, String referenceWebsite, BigDecimal totalAmount) {
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PurchasedPackage {

        private Long serviceId;
        private String packageName;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal subtotal;
    }
}
