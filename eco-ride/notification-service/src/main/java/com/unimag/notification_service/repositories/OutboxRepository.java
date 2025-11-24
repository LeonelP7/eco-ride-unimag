package com.unimag.notification_service.repositories;

import com.unimag.notification_service.entities.Outbox;
import com.unimag.notification_service.enums.StatusEnum;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.Instant;

@Repository
public interface OutboxRepository extends R2dbcRepository<Outbox, Integer> {

    Flux<Outbox> findByStatusAndNextAttemptAtLessThanEqual(StatusEnum status, Instant now);

    reactor.core.publisher.Mono<Outbox> findByReservationId(String reservationId);
}
