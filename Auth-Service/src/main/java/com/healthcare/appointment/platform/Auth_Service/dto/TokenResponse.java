package com.healthcare.appointment.platform.Auth_Service.dto;

import com.healthcare.appointment.platform.Auth_Service.entity.Role;

import java.util.UUID;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UUID userId,
        String email,
        Role role
) {
}
