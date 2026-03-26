package com.hospital.notification.dto;

public class NotificationRequestDTO {
    private String destinatario;
    private String mensaje;

    public NotificationRequestDTO() {}

    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
