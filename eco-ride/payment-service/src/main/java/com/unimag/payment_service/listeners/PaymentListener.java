package com.unimag.payment_service.listeners;

import com.unimag.payment_service.dtos.PaymentResponseDTO;
import com.unimag.payment_service.events.PaymentAuthorizedEvent;
import com.unimag.payment_service.events.PaymentFailedEvent;
import com.unimag.payment_service.events.ReservationRequestedEvent;
import com.unimag.payment_service.services.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@AllArgsConstructor
@Configuration
public class PaymentListener {
    private final PaymentService paymentService;
    private final StreamBridge streamBridge;

    @Bean
    public Consumer<ReservationRequestedEvent> reservePayment() {
        return event -> {

            PaymentResponseDTO paymentResponse = paymentService.processReservePayment(event);
            if (paymentResponse.isAuthorized()){
                // Payment authorized, send event to order in progress
                PaymentAuthorizedEvent paymentAuthorizedEvent = paymentService.getPaymentDetails(event.reserveId());
                streamBridge.send("orderInProgress-out-0", paymentAuthorizedEvent);
                return;
            }
            // Payment rejected, send event to order rejected
            PaymentFailedEvent paymentFailedEvent = paymentService.getPaymentDetails(event.reserveId());
        };
    }
}
