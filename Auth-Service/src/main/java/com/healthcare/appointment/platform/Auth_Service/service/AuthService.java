package com.healthcare.appointment.platform.Auth_Service.service;

import com.healthcare.appointment.platform.Auth_Service.dto.LoginRequest;
import com.healthcare.appointment.platform.Auth_Service.dto.MessageResponse;
import com.healthcare.appointment.platform.Auth_Service.dto.RegisterRequest;
import com.healthcare.appointment.platform.Auth_Service.dto.TokenResponse;
import com.healthcare.appointment.platform.Auth_Service.entity.RefreshToken;
import com.healthcare.appointment.platform.Auth_Service.entity.User;
import com.healthcare.appointment.platform.Auth_Service.repository.RefreshTokenRepository;
import com.healthcare.appointment.platform.Auth_Service.repository.UserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final long refreshExpirationMs;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder, JwtService jwtService, @Value("${app.jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @Transactional
    public MessageResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());

        user.setRole("PATIENT");
        user.setEnabled(true);

        userRepository.save(user);
        return new MessageResponse("Registration Successful");
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                ));
        if (!passwordEncoder.matches(
                request.password(), user.getPasswordHash())
                || !user.isEnabled()
        ) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Invalid username or password");
        }
        return issueTokens(user);
    }

    @Transactional
    public TokenResponse refresh(String rawToken) {
        String tokenHash = hashToken(rawToken);

        RefreshToken existing = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid refresh token"
                ));

        if (existing.isRevoked() || !existing.getExpiresAt().isAfter(Instant.now()) || !existing.getUser().isEnabled()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Expired or revoked refresh token");
        }

        existing.setRevoked(true);
        refreshTokenRepository.save(existing);

        return issueTokens(existing.getUser());
    }

    @Transactional
    public MessageResponse logout(String rawToken) {
        refreshTokenRepository.findByTokenHash(hashToken(rawToken))
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
        return new MessageResponse("Logged out");
    }

    private TokenResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = generateRefreshToken();

        RefreshToken stored = new RefreshToken(user, hashToken(refreshToken), Instant.now().plusMillis(refreshExpirationMs));

        refreshTokenRepository.save(stored);
        return new TokenResponse(accessToken, refreshToken, "Bearer", jwtService.getAccessExpirationMs() / 1000, user.getId(), user.getEmail(), user.getRole());
    }

    private String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is unavailable", ex);
        }
    }

    private String generateRefreshToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String normalizeEmail(@NotBlank @Email @Size(max = 255) String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
