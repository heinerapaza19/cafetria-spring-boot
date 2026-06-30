package com.upeu.pedido.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PedidoDetalleRequest {
	@NotNull
	Long idProducto;

	@NotNull
	@Min(1)
	Integer cantidad;
}
