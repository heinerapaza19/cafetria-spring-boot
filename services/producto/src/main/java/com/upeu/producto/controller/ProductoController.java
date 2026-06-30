package com.upeu.producto.controller;

import com.upeu.producto.dto.ProductoRequest;
import com.upeu.producto.dto.ProductoResponse;
import com.upeu.producto.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // =========================
    // CREAR PRODUCTO (CON IMAGEN)
    // =========================
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ProductoResponse> create(
            @Valid @ModelAttribute ProductoRequest request) {

        // 👉 @ModelAttribute permite recibir:
        // - texto (nombre, precio, etc.)
        // - archivos (MultipartFile imagen)

        ProductoResponse response = productoService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =========================
    // LISTAR TODOS
    // =========================
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> findAll() {
        return ResponseEntity.ok(productoService.findAll());
    }

    // =========================
    // BUSCAR POR ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    // =========================
    // ACTUALIZAR (CON IMAGEN TAMBIÉN)
    // =========================
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ProductoResponse> update(
            @PathVariable Integer id,
            @Valid @ModelAttribute ProductoRequest request) {

        return ResponseEntity.ok(productoService.update(id, request));
    }

    // =========================
    // ELIMINAR
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // DETALLE
    // =========================
    @GetMapping("/detalle/{id}")
    public ResponseEntity<?> findDetalleById(@PathVariable Integer id) {
        return ResponseEntity.ok(productoService.findDetalleById(id));
    }
}
