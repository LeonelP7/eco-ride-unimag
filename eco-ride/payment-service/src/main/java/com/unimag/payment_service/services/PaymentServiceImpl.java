package com.unimag.payment_service.services;

import com.unimag.payment_service.dtos.PaymentResponseDTO;
import com.unimag.payment_service.entities.Charge;
import com.unimag.payment_service.entities.PaymentIntent;
import com.unimag.payment_service.enums.PaymentStatus;
import com.unimag.payment_service.events.PaymentAuthorizedEvent;
import com.unimag.payment_service.events.ReservationCancelledEvent;
import com.unimag.payment_service.events.ReservationRequestedEvent;
import com.unimag.payment_service.exceptions.notFound.PaymentNotFoundException;
import com.unimag.payment_service.mappers.PaymentIntendMapper;
import com.unimag.payment_service.repositories.ChargeRepository;
import com.unimag.payment_service.repositories.PaymentIntendRepository;
import com.unimag.payment_service.services.publisher.EventPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentIntendRepository paymentIntendRepository;
    private final ChargeRepository chargeRepository;
    private final PaymentIntendMapper paymentIntendMapper;
    private final EventPublisherService eventPublisher;

    @Override
    public Mono<PaymentResponseDTO> processReservationRequested(ReservationRequestedEvent event) {
        log.info("Processing ReservationRequested event for reservationId: {}", event.reservationId());

        PaymentIntent paymentIntent = paymentIntendMapper.ReservationRequestedEventToPaymentIntent(event);
        paymentIntent.setId(UUID.randomUUID().toString());
        paymentIntent.setStatus(PaymentStatus.REQUIRES_ACTION);

        return authorizePaymentIntent(paymentIntent)
                .flatMap(authorizedIntent -> paymentIntendRepository.save(authorizedIntent))
                .flatMap(savedIntent -> createAndSaveCharge(savedIntent)
                        .map(charge -> Map.entry(savedIntent, charge)))
                .flatMap(entry -> {
                    PaymentIntent intent = entry.getKey();
                    Charge charge = entry.getValue();

                    return eventPublisher.publishPaymentAuthorized(
                            new PaymentAuthorizedEvent(
                                    intent.getReservationId(),
                                    intent.getId(),
                                    charge.getId(),
                                    "Conectarse con passenger service para solicitar email y nombre del pasajero",
                                    "Avendaño"
                                    // event.email(), // Asumiendo que viene en el evento
                                    // event.passengerName() // Asumiendo que viene en el evento
                            )
                    ).thenReturn(intent);
                })
                .map(paymentIntendMapper::paymentIntentToPaymentResponseDTO)
                .doOnSuccess(dto -> log.info("Successfully processed payment for reservationId: {}",
                        event.reservationId()))
                .doOnError(e -> log.error("Failed to process ReservationRequested for reservationId: {}",
                        event.reservationId(), e));
    }

    private Mono<PaymentIntent> authorizePaymentIntent(PaymentIntent paymentIntent) {
        log.info("Authorizing PaymentIntent for reservationId: {}", paymentIntent.getReservationId());

        // Aquí puedes agregar validaciones con OpenFeign (convertido a reactivo con WebClient)
        // Por ejemplo: return reservationClient.validateReservation(paymentIntent.getReservationId())
        //                  .thenReturn(paymentIntent);

        // Por ahora solo retorna el intent autorizado
        paymentIntent.setStatus(PaymentStatus.AUTHORIZED);
        return Mono.just(paymentIntent)
                .doOnNext(intent -> log.info("PaymentIntent authorized for reservationId: {}",
                        intent.getReservationId()));
    }

    private Mono<Charge> createAndSaveCharge(PaymentIntent paymentIntent) {
        Charge charge = Charge.builder()
                .id(UUID.randomUUID().toString())
                .paymentIntentId(paymentIntent.getId())
                .capturedAt(LocalDateTime.now())
                .build();

        return chargeRepository.save(charge)
                .doOnSuccess(savedCharge -> log.info("Charge created with id: {}", savedCharge.getId()));
    }

    @Override
    public Mono<Void> processReservationCancelled(ReservationCancelledEvent event) {
        log.info("Processing ReservationCancelled event for reservationId: {}", event.reservationId());

        return paymentIntendRepository.findByReservationId(event.reservationId())
                .switchIfEmpty(Mono.error(new PaymentNotFoundException(
                        "Payment not found for reservationId: " + event.reservationId())))
                .flatMap(paymentIntent -> {
                    paymentIntent.setStatus(PaymentStatus.FAILED);
                    return paymentIntendRepository.save(paymentIntent);
                })
                .doOnSuccess(paymentIntent -> log.info("Payment cancelled for reservationId: {}",
                        event.reservationId()))
                .doOnError(e -> log.error("Failed to cancel payment for reservationId: {}",
                        event.reservationId(), e))
                .then();
    }

}
