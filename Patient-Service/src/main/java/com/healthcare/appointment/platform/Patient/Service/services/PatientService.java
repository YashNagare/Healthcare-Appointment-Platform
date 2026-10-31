package com.healthcare.appointment.platform.Patient.Service.services;

import com.healthcare.appointment.platform.Patient.Service.dto.request.CreatePatientRequest;
import com.healthcare.appointment.platform.Patient.Service.dto.response.PatientResponse;

import java.util.UUID;

public interface PatientService {

    PatientResponse createPatient(CreatePatientRequest request) throws Exception;

    PatientResponse getMyProfile();

//    PatientResponse updateMyProfile(UpdatePatientRequest request);

    PatientResponse getPatientById(UUID patientId);
}
