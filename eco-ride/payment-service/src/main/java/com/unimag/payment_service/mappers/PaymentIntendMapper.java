package com.unimag.payment_service.mappers;

import com.unimag.payment_service.dtos.PaymentResponseDTO;
import com.unimag.payment_service.entities.PaymentIntent;
import com.unimag.payment_service.events.ReservationRequestedEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentIntendMapper {

    PaymentIntent ReservationRequestedEventToPaymentIntent(ReservationRequestedEvent event);
    PaymentResponseDTO paymentIntentToPaymentResponseDTO(PaymentIntent paymentIntent);
}
