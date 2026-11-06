package com.webdev.webdevelopment.cart;


import com.webdev.webdevelopment.User.User;
import com.webdev.webdevelopment.Services.ServiceEntity;
import com.webdev.webdevelopment.auth.UserRepository;
import com.webdev.webdevelopment.Services.ServiceRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    // Find the user's cart or create a new one
    private CartEntity getOrCreateCart(String email) {

        return cartRepository.findByUser_Email(email)
                .orElseGet(() -> {

                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() ->
                                    new RuntimeException("User not found")
                            );

                    CartEntity cart = new CartEntity();
                    cart.setUser(user);

                    return cartRepository.save(cart);
                });
    }

    // Get the current user's cart
    @Transactional(readOnly = true)
    public CartResponse getCart(String email) {

        CartEntity cart = cartRepository.findByUser_Email(email)
                .orElse(null);

        if (cart == null) {
            CartResponse response = new CartResponse();
            response.setItems(List.of());
            response.setTotalAmount(BigDecimal.ZERO);
            return response;
        }

        List<CartItemResponse> items = cart.getItems()
                .stream()
                .map(this::convertToResponse)
                .toList();

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setItems(items);
        response.setTotalAmount(totalAmount);

        return response;
    }

    // Add a package to the cart
    @Transactional
    public CartResponse addToCart(
            String email,
            AddToCartRequest request
    ) {

        CartEntity cart = getOrCreateCart(email);

        ServiceEntity service = serviceRepository
                .findById(request.getServiceId())
                .orElseThrow(() ->
                        new RuntimeException("Package not found")
                );

        CartItemEntity cartItem = cartItemRepository
                .findByCart_IdAndService_Id(
                        cart.getId(),
                        service.getId()
                )
                .orElse(null);

        if (cartItem != null) {

            cartItem.setQuantity(
                    cartItem.getQuantity() + request.getQuantity()
            );

        } else {

            cartItem = new CartItemEntity();
            cartItem.setCart(cart);
            cartItem.setService(service);
            cartItem.setQuantity(request.getQuantity());

            cart.getItems().add(cartItem);
        }

        cartItemRepository.save(cartItem);

        return getCart(email);
    }

    // Update the quantity of an existing cart item
    @Transactional
    public CartResponse updateQuantity(
            String email,
            Long itemId,
            UpdateCartItemRequest request
    ) {

        CartEntity cart = getOrCreateCart(email);

        CartItemEntity cartItem = cartItemRepository
                .findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Cart item not found")
                );

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to this user");
        }

        cartItem.setQuantity(request.getQuantity());

        cartItemRepository.save(cartItem);

        return getCart(email);
    }

    // Remove one item from the cart
    @Transactional
    public CartResponse removeItem(String email, Long itemId) {

        // Find the logged-in user's cart
        CartEntity cart = getOrCreateCart(email);

        // Find the cart item
        CartItemEntity cartItem = cartItemRepository
                .findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Cart item not found")
                );

        // Check whether this item belongs to this user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to this user");
        }

        // Remove item from the cart collection
        cart.getItems().remove(cartItem);

        // Return updated cart
        return getCart(email);
    }

    // Clear the current user's cart
    @Transactional
    public void clearCart(String email) {

        CartEntity cart = getOrCreateCart(email);

        List<CartItemEntity> items = cart.getItems();

        cartItemRepository.deleteAll(items);

        cart.getItems().clear();
    }

    // Convert database entity into frontend response DTO
    private CartItemResponse convertToResponse(CartItemEntity item) {

        CartItemResponse response = new CartItemResponse();

        response.setId(item.getId());
        response.setServiceId(item.getService().getId());
        response.setTitle(item.getService().getTitle());
        response.setPrice(item.getService().getPrice());
        response.setQuantity(item.getQuantity());

        BigDecimal subtotal = item.getService()
                .getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        response.setSubtotal(subtotal);

        return response;
    }
}

