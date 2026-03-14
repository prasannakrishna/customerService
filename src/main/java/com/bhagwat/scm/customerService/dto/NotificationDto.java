package com.bhagwat.scm.customerService.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class NotificationDto {
    private UUID id;
    private UUID customerId;
    private String type;      // order, promo, community, subscription
    private String title;
    private String message;
    private boolean read;
    private Instant createdAt;
}
