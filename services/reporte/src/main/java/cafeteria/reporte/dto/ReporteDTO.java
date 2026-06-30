package cafeteria.reporte.dto;


import cafeteria.reporte.entity.TipoReporte;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteDTO {

    private Long id;
    private BigDecimal totalVentas;
    private TipoReporte tipo;
    private LocalDateTime fecha;
}