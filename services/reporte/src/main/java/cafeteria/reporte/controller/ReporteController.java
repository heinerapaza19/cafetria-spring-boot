package cafeteria.reporte.controller;

import cafeteria.reporte.dto.ReporteDTO;
import cafeteria.reporte.service.ReporteService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor

public class ReporteController {

    private final ReporteService service;

    // CREATE
    @PostMapping
    public ReporteDTO crear(@RequestBody ReporteDTO dto) {
        return service.crear(dto);
    }

    // READ ALL
    @GetMapping
    public List<ReporteDTO> listar() {
        return service.listar();
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ReporteDTO obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ReporteDTO actualizar(@PathVariable Long id, @RequestBody ReporteDTO dto) {
        return service.actualizar(id, dto);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}