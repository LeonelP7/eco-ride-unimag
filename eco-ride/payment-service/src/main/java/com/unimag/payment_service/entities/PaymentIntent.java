package com.unimag.payment_service.entities;

import com.unimag.payment_service.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table("payment_intents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntent {

    @Id
    private String id;

    @Column("reservation_id")
    private String reservationId;

    private Double amount;

    private String currency;

    private PaymentStatus status;
}
