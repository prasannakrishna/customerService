package com.bhagwat.scm.customerService.command.service;

import com.bhagwat.scm.customerService.command.entity.Notification;
import com.bhagwat.scm.customerService.command.repository.NotificationRepository;
import com.bhagwat.scm.customerService.dto.NotificationDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<NotificationDto> getNotifications(UUID customerId) {
        return notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public NotificationDto markAsRead(UUID notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        n.setRead(true);
        return toDto(notificationRepository.save(n));
    }

    @Transactional
    public void markAllAsRead(UUID customerId) {
        List<Notification> notifications = notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public void deleteNotification(UUID notificationId, UUID customerId) {
        notificationRepository.deleteByIdAndCustomerId(notificationId, customerId);
    }

    public void createNotification(UUID customerId, String type, String title, String message) {
        Notification n = new Notification();
        n.setCustomerId(customerId);
        n.setType(type);
        n.setTitle(title);
        n.setMessage(message);
        n.setRead(false);
        n.setCreatedAt(Instant.now());
        notificationRepository.save(n);
    }

    private NotificationDto toDto(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        dto.setCustomerId(n.getCustomerId());
        dto.setType(n.getType());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}
