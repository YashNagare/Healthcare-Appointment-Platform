package com.healthcare.appointment.platform.Patient.Service.repositiories;

import com.healthcare.appointment.platform.Patient.Service.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Optional<Patient> findByPatientId(UUID patientId);
}
