package com.upeu.auth.config;

import com.upeu.auth.entity.AuthUser;
import com.upeu.auth.entity.Role;
import com.upeu.auth.repository.AuthUserRepository;
import com.upeu.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initAuthData() {
        return args -> {

            // Crear rol ADMIN si no existe
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> roleRepository.save(
                            Role.builder().name("ADMIN").build()));

            // Crear rol USER si no existe
            Role userRole = roleRepository.findByName("USER")
                    .orElseGet(() -> roleRepository.save(
                            Role.builder().name("USER").build()));

            // Usuario admin de prueba
            if (!authUserRepository.existsByUsername("admin")) {
                authUserRepository.save(
                        AuthUser.builder()
                                .username("admin")
                                .password(passwordEncoder.encode("admin123"))
                                .enabled(true)
                                .roles(Set.of(adminRole))
                                .build());
            }

            // Usuario user de prueba
            if (!authUserRepository.existsByUsername("user")) {
                authUserRepository.save(
                        AuthUser.builder()
                                .username("user")
                                .password(passwordEncoder.encode("user123"))
                                .enabled(true)
                                .roles(Set.of(userRole))
                                .build());
            }

            // Estos usuarios son solo de prueba inicial.
            // Luego puedes usar /auth/register para crear usuarios desde Postman.
        };
    }
}