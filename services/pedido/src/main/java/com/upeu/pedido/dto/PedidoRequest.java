package com.upeu.pedido.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequest {

	private Long idUsuario;
	private List<DetalleRequest> detalles; // 👈 CLAVE
}