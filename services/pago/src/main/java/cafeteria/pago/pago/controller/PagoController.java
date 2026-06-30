package cafeteria.pago.pago.controller;

import cafeteria.pago.pago.dto.PagoDTO;
import cafeteria.pago.pago.service.PagoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Gestión de pagos de la cafetería")
public class PagoController {

    private final PagoService service;

    // Crear pago
    @PostMapping
    public ResponseEntity<PagoDTO> crear(@RequestBody PagoDTO dto) {
        return ResponseEntity.ok(service.crear(dto));
    }

    // Listar pagos
    @GetMapping
    public ResponseEntity<List<PagoDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    // Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<PagoDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    // Eliminar pago
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}