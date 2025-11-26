package com.unimag.passenger_service.listeners;

import com.unimag.passenger_service.config.RabbitConfig;
import com.unimag.passenger_service.events.TripCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class TripListener {

    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    @RabbitListener(queues = RabbitConfig.QUEUE_TRIP_COMPLETED)
    public void onTripCompleted(TripCompletedEvent event) {
        log.info("TripListener: received TripCompleted: tripId={}", event.tripId());

        // Idempotencia
        String eventKey = "trip-completed-" + event.tripId();
        if (!processedEvents.add(eventKey)) {
            log.warn("TripListener: Event already processed, skipping: {}", eventKey);
            return;
        }

        try {
            // Aquí puedes agregar lógica de negocio
            // Por ejemplo: habilitar que los pasajeros se califiquen mutuamente
            log.info("Trip {} completed. Ratings now enabled for driver {} and passenger {}",
                    event.tripId(), event.driverId(), event.passengerId());

            // TODO: Implementar lógica de negocio si es necesaria

        } catch (Exception ex) {
            log.error("TripListener: Error processing TripCompleted for trip {}: {}",
                    event.tripId(), ex.getMessage(), ex);
            processedEvents.remove(eventKey);
            throw ex;
        }
    }

    @Scheduled(fixedRate = 3600000) // Cada hora
    public void cleanProcessedEventsCache() {
        int sizeBefore = processedEvents.size();
        processedEvents.clear();
        log.info("TripListener: Cleaned processed events cache: {} events removed", sizeBefore);
    }
}