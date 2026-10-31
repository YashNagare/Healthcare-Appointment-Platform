package com.healthcare.appointment.platform.Auth_Service.controller;

import com.healthcare.appointment.platform.Auth_Service.dto.*;
import com.healthcare.appointment.platform.Auth_Service.entity.User;
import com.healthcare.appointment.platform.Auth_Service.repository.UserRepository;
import com.healthcare.appointment.platform.Auth_Service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @PostMapping("/logout")
    public MessageResponse logout(@Valid @RequestBody RefreshRequest request) {
        return authService.logout(request.refreshToken());
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        Long userId;
        try {
            userId = Long.valueOf(authentication.getName());
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid identity");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User Not Found"));

        return Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "firstname", user.getFirstName(),
                "lastname", user.getLastName(),
                "role", user.getRole()
        );
    }
}
