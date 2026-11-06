package com.webdev.webdevelopment.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository
        extends JpaRepository<OrderEntity, Long> {

@Query("""
            SELECT DISTINCT o
            FROM OrderEntity o
            JOIN FETCH o.items i
            JOIN FETCH i.service s
            WHERE o.user.email = :email
            ORDER BY o.createdAt DESC 
                        """)
List<OrderEntity> findMyOrders(@Param("email") String email);

    Optional<OrderEntity> findByOrderNumberAndUser_Email(
            String orderNumber,
            String email
    );

    List<OrderEntity> findByUser_EmailOrderByCreatedAtDesc(String email);
}