package com.hospital.patient.controller;

import com.hospital.patient.model.Patient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final List<Patient> patients = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public PatientController() {
        // Datos de ejemplo
        patients.add(new Patient(idCounter.getAndIncrement(), "Juan", "Garcia", 35, "Hipertension", "8112345678"));
        patients.add(new Patient(idCounter.getAndIncrement(), "Maria", "Lopez", 28, "Diabetes tipo 2", "8119876543"));
    }

    @GetMapping
    public List<Patient> getAll() {
        return patients;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return patients.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Patient> create(@RequestBody Patient patient) {
        patient.setId(idCounter.getAndIncrement());
        patients.add(patient);
        return ResponseEntity.status(201).body(patient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Patient updated) {
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getId().equals(id)) {
                updated.setId(id);
                patients.set(i, updated);
                return ResponseEntity.ok(updated);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean removed = patients.removeIf(p -> p.getId().equals(id));
        if (removed) return ResponseEntity.ok().body("Paciente eliminado");
        return ResponseEntity.notFound().build();
    }
}
