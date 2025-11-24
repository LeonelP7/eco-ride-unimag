package com.unimag.payment_service.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("charges")
public class Charge {

    @Id
    private String id;

    @Column("payment_intent_id")
    private String paymentIntentId; // antes PaymentIntent paymentIntent

    private String provider;

    @Column("provider_ref")
    private String providerRef;

    @Column("captured_at")
    private LocalDateTime capturedAt;
}

