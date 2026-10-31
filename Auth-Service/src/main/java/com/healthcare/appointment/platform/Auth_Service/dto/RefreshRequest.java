package com.healthcare.appointment.platform.Auth_Service.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank
        String refreshToken
) {

}
