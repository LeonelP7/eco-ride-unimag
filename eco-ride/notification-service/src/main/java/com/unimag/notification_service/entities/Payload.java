package com.unimag.notification_service.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payload {
    private String destination;
    private String channel;
    private String subject;
    private String body;
}
