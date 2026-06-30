package cafeteria.pago.pago.mapper;



import cafeteria.pago.pago.dto.PagoDTO;
import cafeteria.pago.pago.entity.Pago;

import java.time.LocalDateTime;

public class PagoMapper {

    public static Pago toEntity(PagoDTO dto) {
        return Pago.builder()
                .pedidoId(dto.getPedidoId())
                .monto(dto.getMonto())
                .metodo(dto.getMetodo())
                .fecha(LocalDateTime.now())
                .build();
    }

    public static PagoDTO toDTO(Pago pago) {
        return PagoDTO.builder()
                .pedidoId(pago.getPedidoId())
                .monto(pago.getMonto())
                .metodo(pago.getMetodo())
                .build();
    }
}