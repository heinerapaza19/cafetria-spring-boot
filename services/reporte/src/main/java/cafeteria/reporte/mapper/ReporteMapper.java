package cafeteria.reporte.mapper;


import cafeteria.reporte.dto.ReporteDTO;
import cafeteria.reporte.entity.Reporte;

public class ReporteMapper {

    public static ReporteDTO toDTO(Reporte r) {
        return ReporteDTO.builder()
                .id(r.getId())
                .totalVentas(r.getTotalVentas())
                .tipo(r.getTipo())
                .fecha(r.getFecha())
                .build();
    }

    public static Reporte toEntity(ReporteDTO dto) {
        return Reporte.builder()
                .id(dto.getId())
                .totalVentas(dto.getTotalVentas())
                .tipo(dto.getTipo())
                .fecha(dto.getFecha())
                .build();
    }
}