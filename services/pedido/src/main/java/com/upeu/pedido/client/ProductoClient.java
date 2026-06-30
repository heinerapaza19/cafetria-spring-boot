package com.upeu.pedido.client;

import com.upeu.pedido.dto.ProductoDTO;
import com.upeu.pedido.dto.StockAjusteRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "producto")
public interface ProductoClient {

	@GetMapping("/api/v1/productos/{id}")
	ProductoDTO obtenerProducto(@PathVariable("id") Long id);

	@PostMapping("/api/v1/productos/{id}/stock")
	void descontarStock(
			@PathVariable("id") Long id,
			@RequestBody StockAjusteRequest request
	);
}