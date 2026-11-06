package com.webdev.webdevelopment.cart;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository
        extends JpaRepository<CartItemEntity, Long> {

    Optional<CartItemEntity> findByCart_IdAndService_Id(
            Long cartId,
            Long serviceId
    );
}
