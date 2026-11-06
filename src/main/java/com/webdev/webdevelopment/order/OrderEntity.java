package com.webdev.webdevelopment.order;

import com.webdev.webdevelopment.User.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();

    private BigDecimal totalAmount;

    private String status;
    private LocalDateTime createdAt;

    @Column(unique = true, nullable = false)
    private String orderNumber;

    @Column(nullable = false)
    private String paymentStatus;

    @Column(length = 30)
    private String paymentMethod;

    @Column(length = 100)
    private String transactionId;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();

    }
    @Column(length = 3000)
    private String projectDescription;

    @Column(length = 500)
    private String referenceWebsite;
}
