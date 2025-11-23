package com.unimag.notification_service.enums;

public enum Templates {
    RESERVATION_CONFIRMED("777"),
    RESERVATION_CANCELLED("111");

    private final String code;

    Templates(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
