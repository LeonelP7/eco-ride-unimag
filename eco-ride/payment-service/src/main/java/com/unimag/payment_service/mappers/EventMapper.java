package com.unimag.payment_service.mappers;

import com.unimag.payment_service.dtos.PaymentResponseDTO;
import com.unimag.payment_service.events.PaymentAuthorizedEvent;
import com.unimag.payment_service.events.PaymentFailedEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {
    PaymentAuthorizedEvent toPaymentAuthorizedEvent(PaymentResponseDTO paymentResponseDTO);

    PaymentFailedEvent toPaymentFailedEvent(PaymentResponseDTO paymentResponseDTO);
}
