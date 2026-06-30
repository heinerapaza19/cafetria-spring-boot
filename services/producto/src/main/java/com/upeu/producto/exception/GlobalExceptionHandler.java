package com.upeu.producto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================
    // 🔴 ERROR 400 - VALIDACIONES (@Valid)
    // =========================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // =========================
    // 🔴 ERROR 401 - CREDENCIALES INCORRECTAS
    // =========================
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {

        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuario o contraseña incorrectos");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // =========================
    // 🔴 ERROR 404 - RECURSO NO ENCONTRADO
    // =========================
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {

        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // =========================
    // 🔴 ERROR 403 - ACCESO DENEGADO
    // =========================
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(Exception ex) {

        Map<String, String> response = new HashMap<>();
        response.put("message", "No tienes permisos para acceder a este recurso");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // =========================
    // 🔴 ERROR 500 - ERROR GENERAL
    // =========================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {

        Map<String, String> response = new HashMap<>();
        response.put("message", "Error interno del servidor");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}