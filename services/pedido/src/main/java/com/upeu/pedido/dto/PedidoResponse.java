package com.upeu.pedido.dto;

import com.upeu.pedido.entity.EstadoPedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PedidoResponse {
	Long id;
	Long idUsuario;
	BigDecimal total;
	EstadoPedido estado;
	LocalDateTime fechaCreacion;
	List<PedidoDetalleResponse> detalles;
}
