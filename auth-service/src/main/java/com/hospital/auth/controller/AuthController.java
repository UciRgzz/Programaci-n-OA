package com.hospital.auth.controller;

import com.hospital.auth.dto.LoginRequestDTO;
import com.hospital.auth.dto.LoginResponseDTO;
import com.hospital.auth.dto.TokenValidationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // Usuarios hardcodeados para simplicidad
    private static final Map<String, String> USERS = Map.of(
        "admin", "admin123",
        "doctor", "doc123",
        "enfermera", "enf123"
    );

    // Tokens activos en memoria
    private static final Map<String, String> activeTokens = new HashMap<>();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        String username = request.getUsername();
        String password = request.getPassword();

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "username y password requeridos"));
        }

        if (USERS.containsKey(username) && USERS.get(username).equals(password)) {
            String token = UUID.randomUUID().toString();
            activeTokens.put(token, username);
            return ResponseEntity.ok(new LoginResponseDTO(token, username, "Login exitoso"));
        }

        return ResponseEntity.status(401).body(Map.of("error", "Credenciales invalidas"));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestParam String token) {
        if (activeTokens.containsKey(token)) {
            return ResponseEntity.ok(new TokenValidationDTO(true, activeTokens.get(token)));
        }
        TokenValidationDTO response = new TokenValidationDTO(false, null);
        response.setError("Token invalido");
        return ResponseEntity.status(401).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String token) {
        activeTokens.remove(token);
        return ResponseEntity.ok(Map.of("mensaje", "Sesion cerrada"));
    }
}
