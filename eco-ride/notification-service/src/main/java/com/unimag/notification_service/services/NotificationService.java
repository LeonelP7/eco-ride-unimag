package com.unimag.notification_service.services;

import com.unimag.notification_service.dtos.ReservationBaseDTO;
import reactor.core.publisher.Mono;


public interface NotificationService {
    Mono<String> createNotification(ReservationBaseDTO event, String code);
}
