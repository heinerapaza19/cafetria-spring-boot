package com.upeu.pedido.dto;

import com.upeu.pedido.entity.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ActualizarEstadoPedidoRequest {
	@NotNull
	EstadoPedido estado;
}
