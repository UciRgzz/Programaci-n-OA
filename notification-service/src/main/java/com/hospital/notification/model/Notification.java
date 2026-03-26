package com.hospital.notification.model;

import java.time.LocalDateTime;

public class Notification {
    private Long id;
    private String tipo;       // EMAIL o ALERTA
    private String destinatario;
    private String mensaje;
    private LocalDateTime fecha;

    public Notification() {}

    public Notification(Long id, String tipo, String destinatario, String mensaje) {
        this.id = id;
        this.tipo = tipo;
        this.destinatario = destinatario;
        this.mensaje = mensaje;
        this.fecha = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
