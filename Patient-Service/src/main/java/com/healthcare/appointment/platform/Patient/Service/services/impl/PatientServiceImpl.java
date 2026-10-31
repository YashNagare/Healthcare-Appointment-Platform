package com.healthcare.appointment.platform.Patient.Service.services.impl;

import com.healthcare.appointment.platform.Patient.Service.dto.request.CreatePatientRequest;
import com.healthcare.appointment.platform.Patient.Service.dto.response.PatientResponse;
import com.healthcare.appointment.platform.Patient.Service.entities.Patient;
import com.healthcare.appointment.platform.Patient.Service.mapper.PatientMapper;
import com.healthcare.appointment.platform.Patient.Service.repositiories.PatientRepository;
import com.healthcare.appointment.platform.Patient.Service.services.PatientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientServiceImpl(PatientRepository patientRepository, PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    @Override
    public PatientResponse createPatient(CreatePatientRequest request) throws Exception {
        UUID patientId = UUID.randomUUID();

        if (patientRepository.existsById(patientId)) {
            throw new Exception("Patient profile already exists");
        }

        Patient patient = patientMapper.toEntity(request, patientId);

        try {
            Patient saved = patientRepository.saveAndFlush(patient); // what saveAndFlush does?
            return patientMapper.toResponse(saved);
        } catch (Exception exception) {
            throw new Exception("Patient profile already exits");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getMyProfile() {
        UUID patientId = UUID.fromString("1d8512e1-437c-48fb-b0a9-1020dabf1460");
        Patient patient = patientRepository.findByPatientId(patientId).orElseThrow(
                () -> new RuntimeException("Patient profile not found")
        );
        return patientMapper.toResponse(patient);
    }

    @Override
    public PatientResponse getPatientById(UUID patientId) {
        return null;
    }
}
