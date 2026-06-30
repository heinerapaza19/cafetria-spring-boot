package cafeteria.reporte.exception;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 - errores de negocio
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException e) {

        return buildError(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 404 - recurso no encontrado
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNotFound(NoSuchElementException e) {

        return buildError("Recurso no encontrado", HttpStatus.NOT_FOUND);
    }

    // 500 - errores inesperados
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception e) {

        return buildError("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // método reutilizable
    private ResponseEntity<?> buildError(String msg, HttpStatus status) {

        Map<String, Object> error = new HashMap<>();
        error.put("error", msg);
        error.put("status", status.value());

        return new ResponseEntity<>(error, status);
    }
}