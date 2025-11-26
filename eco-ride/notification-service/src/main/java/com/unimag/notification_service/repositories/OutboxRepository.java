package com.unimag.notification_service.repositories;

import com.unimag.notification_service.entities.Outbox;
import com.unimag.notification_service.enums.StatusEnum;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.List;
import java.util.Collections;
import org.springframework.data.domain.Pageable;

@Repository
public interface OutboxRepository extends R2dbcRepository<Outbox, Integer> {

    Flux<Outbox> findByStatusAndNextAttemptAtLessThanEqual(StatusEnum status, Instant now);

    reactor.core.publisher.Mono<Outbox> findByReservationId(String reservationId);

    // Convenience helper to support synchronous poller code that expects a paged List
    default List<Outbox> findReady(Instant now, Pageable pageable) {
        List<Outbox> all = findByStatusAndNextAttemptAtLessThanEqual(StatusEnum.PENDING, now)
                .collectList()
                .block();
        if (all == null || all.isEmpty()) {
            return Collections.emptyList();
        }
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        return all.subList(from, to);
    }
}
