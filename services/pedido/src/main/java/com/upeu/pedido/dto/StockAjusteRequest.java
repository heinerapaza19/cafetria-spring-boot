package com.upeu.pedido.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StockAjusteRequest {
	Integer cantidad;
}
