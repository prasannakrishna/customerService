package com.bhagwat.scm.customerService.command.repository;

import com.bhagwat.scm.customerService.command.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
    void deleteByIdAndCustomerId(UUID id, UUID customerId);
}
