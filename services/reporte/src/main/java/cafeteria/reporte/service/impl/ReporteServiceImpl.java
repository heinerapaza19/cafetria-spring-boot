package cafeteria.reporte.service.impl;

import cafeteria.reporte.dto.ReporteDTO;
import cafeteria.reporte.entity.Reporte;
import cafeteria.reporte.exception.RecursoNoEncontradoException;
import cafeteria.reporte.mapper.ReporteMapper;
import cafeteria.reporte.repository.ReporteRepository;
import cafeteria.reporte.service.ReporteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository repo;

    public ReporteServiceImpl(ReporteRepository repo) {
        this.repo = repo;
    }

    @Override
    public ReporteDTO crear(ReporteDTO dto) {
        Reporte r = ReporteMapper.toEntity(dto);
        return ReporteMapper.toDTO(repo.save(r));
    }

    @Override
    public List<ReporteDTO> listar() {
        return repo.findAll()
                .stream()
                .map(ReporteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReporteDTO obtenerPorId(Long id) {
        Reporte r = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
        return ReporteMapper.toDTO(r);
    }

    @Override

    public ReporteDTO actualizar(Long id, ReporteDTO dto) {

        Reporte r = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reporte no encontrado con id: " + id));

        r.setTotalVentas(dto.getTotalVentas());
        r.setTipo(dto.getTipo());
        r.setFecha(dto.getFecha());

        return ReporteMapper.toDTO(repo.save(r));
    }

    @Override
    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}