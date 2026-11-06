package com.webdev.webdevelopment.cart;



import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Get the logged-in user's cart
    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            Authentication authentication
    ) {
        String email = authentication.getName();

        return ResponseEntity.ok(
                cartService.getCart(email)
        );
    }

    // Add a package to the cart
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(
            Authentication authentication,
            @Valid @RequestBody AddToCartRequest request
    ) {
        String email = authentication.getName();

        return ResponseEntity.ok(
                cartService.addToCart(email, request)
        );
    }

    // Update cart item quantity
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateQuantity(
            Authentication authentication,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        String email = authentication.getName();

        return ResponseEntity.ok(
                cartService.updateQuantity(email, itemId, request)
        );
    }

    // Remove one item
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(
            Authentication authentication,
            @PathVariable Long itemId
    ) {
        String email = authentication.getName();

        return ResponseEntity.ok(
                cartService.removeItem(email, itemId)
        );
    }

    // Clear the entire cart
    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            Authentication authentication
    ) {
        String email = authentication.getName();

        cartService.clearCart(email);

        return ResponseEntity.noContent().build();
    }
}
