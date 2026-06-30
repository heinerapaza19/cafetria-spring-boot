package com.upeu.pedido.controller;

import com.upeu.pedido.dto.ActualizarEstadoPedidoRequest;
import com.upeu.pedido.dto.PedidoRequest;
import com.upeu.pedido.dto.PedidoResponse;
import com.upeu.pedido.service.PedidoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

	private final PedidoService pedidoService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PedidoResponse create(@Valid @RequestBody PedidoRequest request) {
		return pedidoService.create(request);
	}

	@GetMapping
	public List<PedidoResponse> findAll() {
		return pedidoService.findAll();
	}

	@GetMapping("/{id}")
	public PedidoResponse findById(@PathVariable Long id) {
		return pedidoService.findById(id);
	}

	@PatchMapping("/{id}/estado")
	public PedidoResponse actualizarEstado(@PathVariable Long id,
			@Valid @RequestBody ActualizarEstadoPedidoRequest request) {
		return pedidoService.actualizarEstado(id, request.getEstado());
	}
}
