package com.unimag.notification_service.services;

import com.unimag.notification_service.dtos.ReservationBaseDTO;
import com.unimag.notification_service.events.ReservationConfirmedEvent;


public interface NotificationService {
    String createNotification(ReservationBaseDTO event, String eventType);
}
