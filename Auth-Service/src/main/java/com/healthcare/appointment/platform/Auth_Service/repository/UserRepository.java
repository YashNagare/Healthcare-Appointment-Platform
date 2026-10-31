package com.healthcare.appointment.platform.Auth_Service.repository;

import com.healthcare.appointment.platform.Auth_Service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

}
