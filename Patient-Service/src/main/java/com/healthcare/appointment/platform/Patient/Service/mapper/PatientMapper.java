package com.healthcare.appointment.platform.Patient.Service.mapper;

import com.healthcare.appointment.platform.Patient.Service.dto.request.CreatePatientRequest;
import com.healthcare.appointment.platform.Patient.Service.dto.response.PatientResponse;
import com.healthcare.appointment.platform.Patient.Service.entities.Patient;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PatientMapper {

    public Patient toEntity(CreatePatientRequest request, UUID patientId) {
        Patient patient = new Patient();
        patient.setPatientId(patientId);
        patient.setFirstName(request.firstName());
        patient.setLastName(request.lastName());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender());
        patient.setPhoneNumber(request.phoneNumber());

        return patient;
    }

    public PatientResponse toResponse(Patient patient) {
        return new PatientResponse(
                patient.getPatientId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getPhoneNumber(),
                patient.getCreatedAt(),
                patient.getUpdatedAt()
        );
    }

}
