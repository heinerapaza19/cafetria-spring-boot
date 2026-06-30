package com.upeu.auth.service;

import com.upeu.auth.config.JwtProperties;
import com.upeu.auth.dto.AuthLoginRequest;
import com.upeu.auth.dto.AuthLoginResponse;
import com.upeu.auth.dto.AuthRegisterRequest;
import com.upeu.auth.entity.AuthUser;
import com.upeu.auth.entity.Role;
import com.upeu.auth.repository.AuthUserRepository;
import com.upeu.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;
        private final JwtProperties jwtProperties;

        // 🔥 NUEVO PARA REGISTER
        private final AuthUserRepository userRepository;
        private final RoleRepository roleRepository;
        private final PasswordEncoder passwordEncoder;

        // =========================
        // 🔵 LOGIN (TU CÓDIGO)
        // =========================
        public AuthLoginResponse login(AuthLoginRequest request) {

                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getUsername(),
                                                request.getPassword()));

                UserDetails userDetails = (UserDetails) authentication.getPrincipal();

                String token = jwtService.generateToken(userDetails);

                List<String> roles = userDetails.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList();

                return AuthLoginResponse.builder()
                                .accessToken(token)
                                .tokenType("Bearer")
                                .expiresIn(jwtProperties.getExpiration())
                                .username(userDetails.getUsername())
                                .roles(roles)
                                .build();
        }

        // =========================
        // 🟢 REGISTER (NUEVO)
        // =========================
        public void register(AuthRegisterRequest request) {

                // 1. validar si existe usuario
                if (userRepository.existsByUsername(request.getUsername())) {
                        throw new RuntimeException("Usuario ya existe");
                }

                // 2. obtener o crear rol USER
                Role userRole = roleRepository.findByName("USER")
                                .orElseGet(() -> roleRepository.save(
                                                Role.builder().name("USER").build()));

                // 3. crear usuario
                AuthUser user = AuthUser.builder()
                                .username(request.getUsername())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .enabled(true)
                                .roles(Set.of(userRole))
                                .build();

                // 4. guardar en BD
                userRepository.save(user);
        }
}