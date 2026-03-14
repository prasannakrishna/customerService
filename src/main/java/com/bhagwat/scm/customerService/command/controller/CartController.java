package com.bhagwat.scm.customerService.command.controller;

import com.bhagwat.scm.customerService.command.service.CartService;
import com.bhagwat.scm.customerService.dto.CartItemRequest;
import com.bhagwat.scm.customerService.dto.CartResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestParam UUID customerId) {
        return ResponseEntity.ok(cartService.getCart(customerId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addOrUpdateItem(@RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addOrUpdateItem(request));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(@RequestParam UUID customerId,
                                                    @PathVariable UUID productId) {
        return ResponseEntity.ok(cartService.removeItem(customerId, productId));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestParam UUID customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.noContent().build();
    }
}
