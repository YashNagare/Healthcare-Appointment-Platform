package com.healthcare.appointment.platform.Patient.Service.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreatePatientRequest(

        @NotBlank
        @Size(max = 100)
        String firstName,

        @NotBlank
        @Size(max = 100)
        String lastName,

        @Past
        LocalDate dateOfBirth,

        @Size(max = 30)
        String gender,

        @Pattern(regexp = "^\\+?[0-9]{7,15}$")
        String phoneNumber
) {
}
