package com.unimag.notification_service.entities;

import com.unimag.notification_service.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity(name = "outboxes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Outbox {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String eventType;
    private String payload;
    private StatusEnum status;
    private int retries;
    private Instant nextAttemptAt;
    private Instant lastAttemptAt;
    private String errorMessage;
}
