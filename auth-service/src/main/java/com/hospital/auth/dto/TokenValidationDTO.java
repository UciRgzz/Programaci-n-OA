package com.hospital.auth.dto;

public class TokenValidationDTO {
    private boolean valido;
    private String username;
    private String error;

    public TokenValidationDTO() {}

    public TokenValidationDTO(boolean valido, String username) {
        this.valido = valido;
        this.username = username;
    }

    public boolean isValido() { return valido; }
    public void setValido(boolean valido) { this.valido = valido; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
