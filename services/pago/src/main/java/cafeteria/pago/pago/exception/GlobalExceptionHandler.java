package cafeteria.pago.pago.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import feign.FeignException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================
    // ERROR GENÉRICO
    // =========================
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Error de negocio",
                ex.getMessage());
    }

    // =========================
    // VALIDACIONES @Valid
    // =========================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("error", "Validación fallida");
        response.put("details", errors);
        response.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // =========================
    // ERRORES FEIGN (MICROSERVICIOS)
    // =========================
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<?> handleFeignException(FeignException ex) {

        return buildError(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Error de comunicación entre microservicios",
                ex.getMessage());
    }

    // =========================
    // NULL POINTER / ERRORES INTERNOS
    // =========================
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointer(NullPointerException ex) {

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno (NullPointer)",
                ex.getMessage());
    }

    // =========================
    // ERROR GENERAL (FALLBACK)
    // =========================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                ex.getMessage());
    }

    // =========================
    // MÉTODO REUTILIZABLE
    // =========================
    private ResponseEntity<?> buildError(HttpStatus status, String error, String message) {

        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(body, status);
    }
}