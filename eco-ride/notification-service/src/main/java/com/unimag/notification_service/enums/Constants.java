package com.unimag.notification_service.enums;

public class Constants {
    public static final String NOTIFICATION_CHANNEL = "email";
    public static final int MAX_RETRIES = 5;
    public static final String RESERVATION_CONFIRMED = "RESERVATION_CONFIRMED";
    public static final String RESERVATION_CANCELLED = "RESERVATION_CANCELLED";

    private final String value;

    Constants(String value) {
        this.value = value;
    }

}
