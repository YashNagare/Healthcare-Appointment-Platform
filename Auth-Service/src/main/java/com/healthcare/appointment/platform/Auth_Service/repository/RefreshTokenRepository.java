package com.healthcare.appointment.platform.Auth_Service.repository;

import com.healthcare.appointment.platform.Auth_Service.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

}
