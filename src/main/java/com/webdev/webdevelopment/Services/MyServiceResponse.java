package com.webdev.webdevelopment.Services;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MyServiceResponse {

    private Long id;
    private Long serviceId;
    private String title;
    private String status;
    private LocalDateTime purchaseDate;
    private BigDecimal price;
    private BigDecimal subtotal;
    private Integer quantity;
    private List<String> features;

    public MyServiceResponse() {

    }
}
