package com.upeu.pedido.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PedidoDetalleResponse {
	Long idProducto;
	String nombreProducto;
	Integer cantidad;
	BigDecimal precioUnitario;
	BigDecimal subtotal;
}
