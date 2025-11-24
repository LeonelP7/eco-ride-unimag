package com.unimag.payment_service.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("refunds")
public class Refund {

    @Id
    private String id;

    @Column("charge_id")
    private String chargeId;

    private Double amount;

    private String reason;

    @Column("created_at")
    private LocalDateTime createdAt;
}
