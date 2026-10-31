package com.healthcare.appointment.platform.Auth_Service.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        Long userId,
        String email,
        String role
) {
}
