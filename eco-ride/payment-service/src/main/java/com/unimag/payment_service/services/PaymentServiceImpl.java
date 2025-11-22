package com.unimag.payment_service.services;

import com.unimag.payment_service.dtos.PaymentResponseDTO;
import com.unimag.payment_service.entities.Charge;
import com.unimag.payment_service.entities.PaymentIntent;
import com.unimag.payment_service.enums.PaymentStatus;
import com.unimag.payment_service.events.PaymentAuthorizedEvent;
import com.unimag.payment_service.events.ReservationCancelledEvent;
import com.unimag.payment_service.events.ReservationRequestedEvent;
import com.unimag.payment_service.mappers.PaymentIntendMapper;
import com.unimag.payment_service.repositories.ChargeRepository;
import com.unimag.payment_service.repositories.PaymentIntendRepository;
import com.unimag.payment_service.services.publisher.EventPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentIntendRepository paymentIntendRepository;
    private final ChargeRepository chargeRepository;
    private final PaymentIntendMapper paymentIntendMapper;
    private final EventPublisherService eventPublisher;

    @Override
    public PaymentResponseDTO processReservationRequested(ReservationRequestedEvent event) {
        PaymentIntent paymentIntent = paymentIntendMapper.ReservationRequestedEventToPaymentIntent(event);
        paymentIntent.setStatus(PaymentStatus.REQUIRES_ACTION);

        log.info("PaymentInted created with status REQUIRES_ACTION reservationId:{}", paymentIntent.getReservationId());

        // logica de autorizar paymentInted por ejemplo validar el reservation id, con open feign por ejemplo
        authorizePaymentIntend(paymentIntent);

        // si llega hasta aqui es porque esta atorizado
        paymentIntendRepository.save(paymentIntent);

        // falta ver lo de provider
        Charge charge = Charge.builder()
                .paymentIntent(paymentIntent)
                .capturedAt(LocalDateTime.now())
                .build();

        charge = chargeRepository.save(charge);

        try {
            eventPublisher.publishPaymentAuthorized( new PaymentAuthorizedEvent(
                    paymentIntent.getReservationId(),
                    paymentIntent.getId(),
                    charge.getId()
                    )
            );
        } catch (Exception e) {
            log.error("Failed to publish PaymentAuthorized event", e);
            // En producción, considera guardar en outbox table para retry
        }

        return paymentIntendMapper.paymentIntentToPaymentResponseDTO(paymentIntent);
    }

    @Override
    public void processReservationCancelled(ReservationCancelledEvent event) {

        // lol
    }

    private void authorizePaymentIntend(PaymentIntent toAuthorize) {

        // logica de autorizar

        //de momento
        toAuthorize.setStatus(PaymentStatus.AUTHORIZED);
    }

}
