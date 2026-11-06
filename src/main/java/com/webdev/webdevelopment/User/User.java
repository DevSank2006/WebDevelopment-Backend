package com.webdev.webdevelopment.User;

import com.webdev.webdevelopment.order.OrderEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 15)
    private String phone;

    // Company details
    @Column(length = 150)
    private String companyName;

    // Address details
    @Column(length = 500)
    private String address;

    @Column(nullable = false)
    private boolean emailVerified = false;

    @Column(length = 6)
    private String verificationCode;

    private LocalDateTime verificationCodeExpiry;

    @OneToMany(mappedBy = "user")
    private List<OrderEntity> orders = new ArrayList<>();

}