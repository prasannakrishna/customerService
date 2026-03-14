package com.bhagwat.scm.customerService.command.service;

import com.bhagwat.scm.customerService.command.entity.ShoppingBag;
import com.bhagwat.scm.customerService.command.entity.ShoppingBagItem;
import com.bhagwat.scm.customerService.command.repository.ShoppingBagRepository;
import com.bhagwat.scm.customerService.constant.ShoppingBagStatus;
import com.bhagwat.scm.customerService.dto.CartItemRequest;
import com.bhagwat.scm.customerService.dto.CartItemResponse;
import com.bhagwat.scm.customerService.dto.CartResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final ShoppingBagRepository bagRepository;

    public CartService(ShoppingBagRepository bagRepository) {
        this.bagRepository = bagRepository;
    }

    public CartResponse getCart(UUID customerId) {
        ShoppingBag bag = getOrCreateActiveBag(customerId);
        return toResponse(bag);
    }

    @Transactional
    public CartResponse addOrUpdateItem(CartItemRequest request) {
        ShoppingBag bag = getOrCreateActiveBag(request.getCustomerId());

        ShoppingBagItem existing = bag.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst().orElse(null);

        if (existing != null) {
            existing.setQuantity(request.getQuantity());
        } else {
            ShoppingBagItem item = new ShoppingBagItem();
            item.setProductId(request.getProductId());
            item.setProductVariantId(request.getVariantId());
            item.setQuantity(request.getQuantity());
            item.setShoppingBag(bag);
            bag.getItems().add(item);
        }

        recalculate(bag);
        return toResponse(bagRepository.save(bag));
    }

    @Transactional
    public CartResponse removeItem(UUID customerId, UUID productId) {
        ShoppingBag bag = getOrCreateActiveBag(customerId);
        bag.getItems().removeIf(i -> i.getProductId().equals(productId));
        recalculate(bag);
        return toResponse(bagRepository.save(bag));
    }

    @Transactional
    public void clearCart(UUID customerId) {
        bagRepository.findByUserIdAndStatus(customerId, ShoppingBagStatus.FILLING)
                .ifPresent(bag -> {
                    bag.getItems().clear();
                    bag.setTotalItemCount(0);
                    bag.setTotalAmount(BigDecimal.ZERO);
                    bag.setLastUpdatedTimestamp(Instant.now());
                    bagRepository.save(bag);
                });
    }

    private ShoppingBag getOrCreateActiveBag(UUID customerId) {
        return bagRepository.findByUserIdAndStatus(customerId, ShoppingBagStatus.FILLING)
                .orElseGet(() -> {
                    ShoppingBag bag = new ShoppingBag();
                    bag.setUserId(customerId);
                    bag.setStatus(ShoppingBagStatus.FILLING);
                    bag.setTotalItemCount(0);
                    bag.setTotalAmount(BigDecimal.ZERO);
                    bag.setItems(new ArrayList<>());
                    bag.setCreatedTimestamp(Instant.now());
                    bag.setLastUpdatedTimestamp(Instant.now());
                    return bagRepository.save(bag);
                });
    }

    private void recalculate(ShoppingBag bag) {
        int count = bag.getItems().stream().mapToInt(ShoppingBagItem::getQuantity).sum();
        bag.setTotalItemCount(count);
        bag.setLastUpdatedTimestamp(Instant.now());
    }

    private CartResponse toResponse(ShoppingBag bag) {
        CartResponse r = new CartResponse();
        r.setCartId(bag.getId());
        r.setCustomerId(bag.getUserId());
        r.setStatus(bag.getStatus());
        r.setTotalItemCount(bag.getTotalItemCount());
        r.setTotalAmount(bag.getTotalAmount());
        List<CartItemResponse> items = bag.getItems().stream().map(i -> {
            CartItemResponse ci = new CartItemResponse();
            ci.setId(i.getId());
            ci.setProductId(i.getProductId());
            ci.setVariantId(i.getProductVariantId());
            ci.setQuantity(i.getQuantity());
            return ci;
        }).collect(Collectors.toList());
        r.setItems(items);
        return r;
    }
}
