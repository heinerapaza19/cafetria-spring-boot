package cafeteria.pago.pago.dto;


import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoDTO {

    private Long pedidoId;
    private BigDecimal monto;
    private String metodo; // EFECTIVO | YAPE
}
