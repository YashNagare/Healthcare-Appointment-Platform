package com.healthcare.appointment.platform.Patient.Service.controllers;

import com.healthcare.appointment.platform.Patient.Service.dto.request.CreatePatientRequest;
import com.healthcare.appointment.platform.Patient.Service.dto.response.PatientResponse;
import com.healthcare.appointment.platform.Patient.Service.services.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@RequestBody CreatePatientRequest request) throws Exception {
        PatientResponse response = patientService.createPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<PatientResponse> getMyProfile() {
        return ResponseEntity.ok(patientService.getMyProfile());
    }
}
