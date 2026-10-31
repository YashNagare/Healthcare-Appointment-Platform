package com.healthcare.appointment.platform.Patient.Service.dto.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PatientResponse(
        UUID id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String gender,
        String phoneNumber,
        Instant createdAt,
        Instant updatedAt
) {
}
