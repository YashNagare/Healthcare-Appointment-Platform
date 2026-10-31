package com.healthcare.appointment.platform.Auth_Service.config;

import com.healthcare.appointment.platform.Auth_Service.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http, JwtService jwtService) throws Exception {

        OncePerRequestFilter jwtFilter = new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain chain)
                    throws ServletException, IOException {

                String header = request.getHeader(
                        HttpHeaders.AUTHORIZATION);

                if (header != null && header.startsWith("Bearer ")) {
                    try {
                        Claims claims = jwtService.parseToken(
                                header.substring(7));

                        var authentication =
                                new org.springframework.security
                                        .authentication
                                        .UsernamePasswordAuthenticationToken(
                                        claims.getSubject(),
                                        null,
                                        List.of(new org.springframework.security
                                                .core.authority
                                                .SimpleGrantedAuthority(
                                                "ROLE_" +
                                                        claims.get("role", String.class)
                                        ))
                                );

                        var context =
                                org.springframework.security.core.context
                                        .SecurityContextHolder
                                        .createEmptyContext();
                        context.setAuthentication(authentication);
                        org.springframework.security.core.context
                                .SecurityContextHolder
                                .setContext(context);

                    } catch (JwtException |
                             IllegalArgumentException ex) {
                        response.sendError(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Invalid or expired access token");
                        return;
                    }
                }

                chain.doFilter(request, response);
            }
        };

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh",
                                "/api/v1/auth/logout",
                                "/error"
                        ).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
