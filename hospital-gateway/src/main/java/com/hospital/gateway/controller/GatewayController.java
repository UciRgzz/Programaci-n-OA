package com.hospital.gateway.controller;

import com.hospital.gateway.dto.AltaPacienteRequestDTO;
import com.hospital.gateway.dto.LoginAndRegisterRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/gateway")
public class GatewayController {

    private static final String AUTH_URL         = "http://localhost:8081";
    private static final String PATIENT_URL      = "http://localhost:8082";
    private static final String NOTIFICATION_URL = "http://localhost:8083";

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE =
            new ParameterizedTypeReference<Map<String, Object>>() {};

    @Autowired
    private RestTemplate restTemplate;

    // ---- AUTH ----
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            return restTemplate.postForEntity(AUTH_URL + "/auth/login", credentials, Object.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestParam String token) {
        return restTemplate.getForEntity(AUTH_URL + "/auth/validate?token=" + token, Object.class);
    }

    // ---- PATIENTS ----
    @GetMapping("/patients")
    public ResponseEntity<?> getPatients() {
        return restTemplate.getForEntity(PATIENT_URL + "/patients", Object.class);
    }

    @GetMapping("/patients/{id}")
    public ResponseEntity<?> getPatient(@PathVariable Long id) {
        return restTemplate.getForEntity(PATIENT_URL + "/patients/" + id, Object.class);
    }

    @PostMapping("/patients")
    public ResponseEntity<?> createPatient(@RequestBody Object patient) {
        return restTemplate.postForEntity(PATIENT_URL + "/patients", patient, Object.class);
    }

    // ---- NOTIFICATIONS ----
    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications() {
        return restTemplate.getForEntity(NOTIFICATION_URL + "/notifications", Object.class);
    }

    @PostMapping("/notifications/email")
    public ResponseEntity<?> sendEmail(@RequestBody Map<String, String> request) {
        return restTemplate.postForEntity(NOTIFICATION_URL + "/notifications/email", request, Object.class);
    }

    @PostMapping("/notifications/alerta")
    public ResponseEntity<?> sendAlert(@RequestBody Map<String, String> request) {
        return restTemplate.postForEntity(NOTIFICATION_URL + "/notifications/alerta", request, Object.class);
    }

    // ==================================================================
    // ENDPOINTS COMPUESTOS (composicion de servicios)
    // ==================================================================

    /**
     * Endpoint compuesto #1:
     * Login + Registro de paciente en una sola operacion.
     */
    @PostMapping("/login-y-registro")
    public ResponseEntity<?> loginYRegistro(@RequestBody LoginAndRegisterRequestDTO body) {
        // Paso 1: autenticar
        Map<String, String> creds = new HashMap<>();
        creds.put("username", body.getUsername());
        creds.put("password", body.getPassword());

        ResponseEntity<Map<String, Object>> authResp;
        try {
            authResp = restTemplate.exchange(
                    AUTH_URL + "/auth/login", HttpMethod.POST, new HttpEntity<>(creds), MAP_TYPE);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales invalidas"));
        }

        String token = (String) authResp.getBody().get("token");

        // Paso 2: registrar paciente
        Map<String, Object> patient = new HashMap<>();
        patient.put("nombre",      body.getNombre());
        patient.put("apellido",    body.getApellido());
        patient.put("edad",        body.getEdad());
        patient.put("diagnostico", body.getDiagnostico());
        patient.put("telefono",    body.getTelefono());

        ResponseEntity<Map<String, Object>> patientResp = restTemplate.exchange(
                PATIENT_URL + "/patients", HttpMethod.POST, new HttpEntity<>(patient), MAP_TYPE);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("token",    token);
        resultado.put("paciente", patientResp.getBody());
        resultado.put("mensaje",  "Login exitoso y paciente registrado");

        return ResponseEntity.status(201).body(resultado);
    }

    /**
     * Endpoint compuesto #2:
     * Valida token + Registra paciente + Envia notificacion de alta.
     */
    @PostMapping("/alta-paciente")
    public ResponseEntity<?> altaPaciente(@RequestParam String token,
                                           @RequestBody AltaPacienteRequestDTO body) {
        // Paso 1: validar token
        ResponseEntity<Map<String, Object>> validacion;
        try {
            validacion = restTemplate.exchange(
                    AUTH_URL + "/auth/validate?token=" + token, HttpMethod.GET, null, MAP_TYPE);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Token invalido o expirado"));
        }

        String username = (String) validacion.getBody().get("username");

        // Paso 2: registrar paciente
        Map<String, Object> patient = new HashMap<>();
        patient.put("nombre",      body.getNombre());
        patient.put("apellido",    body.getApellido());
        patient.put("edad",        body.getEdad());
        patient.put("diagnostico", body.getDiagnostico());
        patient.put("telefono",    body.getTelefono());

        ResponseEntity<Map<String, Object>> patientResp = restTemplate.exchange(
                PATIENT_URL + "/patients", HttpMethod.POST, new HttpEntity<>(patient), MAP_TYPE);

        // Paso 3: enviar notificacion de alta
        String email = body.getEmail() != null ? body.getEmail() : "hospital@sistema.com";
        Map<String, String> notif = new HashMap<>();
        notif.put("destinatario", email);
        notif.put("mensaje", "Alta de paciente: " + body.getNombre() + " " + body.getApellido()
            + " | Diagnostico: " + body.getDiagnostico() + " | Registrado por: " + username);

        restTemplate.postForEntity(NOTIFICATION_URL + "/notifications/email", notif, Object.class);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("paciente",            patientResp.getBody());
        resultado.put("notificacionEnviada", true);
        resultado.put("registradoPor",       username);
        resultado.put("mensaje",             "Paciente dado de alta y notificacion enviada");

        return ResponseEntity.status(201).body(resultado);
    }
}
