package com.webdev.webdevelopment.cart;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponse {

    private Long id;

    private Long serviceId;

    private String title;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal subtotal;
}
