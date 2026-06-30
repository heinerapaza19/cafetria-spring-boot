package com.upeu.auth.controller;

import com.upeu.auth.dto.AuthLoginRequest;
import com.upeu.auth.dto.AuthLoginResponse;
import com.upeu.auth.dto.AuthRegisterRequest;
import com.upeu.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // =========================
    // 🟢 REGISTER
    // =========================
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody AuthRegisterRequest request) {

        authService.register(request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuario registrado correctamente");

        return ResponseEntity.ok(response);
    }

    // =========================
    // 🔵 LOGIN
    // =========================
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthLoginRequest request) {

        AuthLoginResponse authResponse = authService.login(request);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Login exitoso");
        result.put("data", authResponse);

        return ResponseEntity.ok(result);
    }
}