package com.bhagwat.scm.customerService.command.repository;

import com.bhagwat.scm.customerService.command.entity.ShoppingBag;
import com.bhagwat.scm.customerService.constant.ShoppingBagStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ShoppingBagRepository extends JpaRepository<ShoppingBag, UUID> {
    Optional<ShoppingBag> findByUserIdAndStatus(UUID userId, ShoppingBagStatus status);
}
