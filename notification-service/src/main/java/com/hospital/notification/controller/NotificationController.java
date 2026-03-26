package com.hospital.notification.controller;

import com.hospital.notification.dto.NotificationRequestDTO;
import com.hospital.notification.model.Notification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final List<Notification> notifications = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @GetMapping
    public List<Notification> getAll() {
        return notifications;
    }

    @PostMapping("/email")
    public ResponseEntity<Notification> sendEmail(@RequestBody NotificationRequestDTO request) {
        if (request.getDestinatario() == null || request.getMensaje() == null) {
            return ResponseEntity.badRequest().build();
        }

        Notification notif = new Notification(idCounter.getAndIncrement(), "EMAIL",
                request.getDestinatario(), request.getMensaje());
        notifications.add(notif);

        System.out.println("[EMAIL] Para: " + request.getDestinatario() + " | Mensaje: " + request.getMensaje());
        return ResponseEntity.status(201).body(notif);
    }

    @PostMapping("/alerta")
    public ResponseEntity<Notification> sendAlert(@RequestBody NotificationRequestDTO request) {
        String destinatario = request.getDestinatario() != null ? request.getDestinatario() : "SISTEMA";

        if (request.getMensaje() == null) {
            return ResponseEntity.badRequest().build();
        }

        Notification notif = new Notification(idCounter.getAndIncrement(), "ALERTA",
                destinatario, request.getMensaje());
        notifications.add(notif);

        System.out.println("[ALERTA] Para: " + destinatario + " | Mensaje: " + request.getMensaje());
        return ResponseEntity.status(201).body(notif);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean removed = notifications.removeIf(n -> n.getId().equals(id));
        if (removed) return ResponseEntity.ok().body("Notificacion eliminada");
        return ResponseEntity.notFound().build();
    }
}
