package com.bhagwat.scm.customerService.command.controller;

import com.bhagwat.scm.customerService.command.service.NotificationService;
import com.bhagwat.scm.customerService.dto.NotificationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(@RequestParam UUID customerId) {
        return ResponseEntity.ok(notificationService.getNotifications(customerId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDto> markAsRead(@PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@RequestParam UUID customerId) {
        notificationService.markAllAsRead(customerId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id, @RequestParam UUID customerId) {
        notificationService.deleteNotification(id, customerId);
        return ResponseEntity.noContent().build();
    }
}
