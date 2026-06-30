package com.upeu.pedido.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ProductoDTO {
	Long id;
	String nombre;
	String descripcion;
	Long idCategoria;
	BigDecimal precio;
	Integer stock;
}
