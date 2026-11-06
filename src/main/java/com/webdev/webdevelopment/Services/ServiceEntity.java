package com.webdev.webdevelopment.Services;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "services")
@Getter
@Setter
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private BigDecimal price;

    @ElementCollection
    @CollectionTable(
            name = "service_features",
            joinColumns = @JoinColumn(name = "service_id")
    )
    @Column(name = "feature", nullable = false)
    @OrderColumn(name = "feature_order")
    private List<String> features = new ArrayList<>();
}
