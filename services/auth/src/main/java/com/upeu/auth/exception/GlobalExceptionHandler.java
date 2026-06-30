package com.upeu.auth.exception;

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

    // 🔴 ERROR 401 - credenciales incorrectas (login)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Usuario o contraseña incorrectos");
        response.put("code", 401);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 🔴 ERROR 400 - validaciones (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, Object> response = new HashMap<>();

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        response.put("success", false);
        response.put("message", "Errores de validación");
        response.put("errors", errors);
        response.put("code", 400);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 🔴 ERROR 404 - recurso no encontrado
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", ex.getMessage());
        response.put("code", 404);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 🔴 ERROR 500 - error general del sistema
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Error interno del servidor");
        response.put("code", 500);

        // opcional (solo desarrollo)
        response.put("debug", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}