package com.unimag.notification_service.senders;

import lombok.AllArgsConstructor;
import org.springframework.jmx.export.notification.UnableToSendNotificationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component("email")
@AllArgsConstructor
public class EmailSender implements  NotificationSender {
    private final JavaMailSender mailSender;

    public void send(String to, String subject, String body, String payloadMeta) throws UnableToSendNotificationException {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Destinatario inválido");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("conectaciudadxyz@gmail.com"); // cambia a un dominio tuyo

        try {
            mailSender.send(message);
            System.out.printf("Email enviado a %s con asunto '%s'%n", to, subject);
        } catch (Exception e) {
            System.err.printf("Error al intentar enviar correo a %s: %s%n", to, e.getMessage());
            throw e;
        }
    }

}
