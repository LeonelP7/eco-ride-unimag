package com.unimag.notification_service.repositories;

import com.unimag.notification_service.entities.Outbox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, Integer> {
    @Query("SELECT o FROM Outbox o WHERE o.status = 'PENDING' AND o.nextAttemptAt <= :now")
    List<Outbox> findReady(@Param("now") Instant now, Pageable pageable);

    Optional<Outbox> findByReservationId(String reservationId);
}
