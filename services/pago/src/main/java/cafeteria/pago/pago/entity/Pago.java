package cafeteria.pago.pago.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pedidoId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    private String metodo; // EFECTIVO | YAPE

    private LocalDateTime fecha;
}