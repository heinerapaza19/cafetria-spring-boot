package com.upeu.pedido.service.impl;

import com.upeu.pedido.client.ProductoClient;
import com.upeu.pedido.dto.*;
import com.upeu.pedido.entity.EstadoPedido;
import com.upeu.pedido.entity.Pedido;
import com.upeu.pedido.entity.PedidoDetalle;
import com.upeu.pedido.repository.PedidoRepository;
import com.upeu.pedido.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

	private final PedidoRepository pedidoRepository;
	private final ProductoClient productoClient;

	@Override
	@Transactional
	public PedidoResponse create(PedidoRequest request) {

		Pedido pedido = new Pedido();
		pedido.setIdUsuario(request.getIdUsuario());
		pedido.setEstado(EstadoPedido.PENDIENTE);
		pedido.setFechaCreacion(LocalDateTime.now());
		pedido.setTotal(BigDecimal.ZERO);
		pedido.setDetalles(new ArrayList<>()); // ✅ seguro

		BigDecimal total = BigDecimal.ZERO;

		for (var detalleRequest : request.getDetalles()) {

			var producto = productoClient.obtenerProducto(detalleRequest.getIdProducto().longValue());

			if (producto.getStock() == null ||
					producto.getStock() < detalleRequest.getCantidad()) {

				throw new ResponseStatusException(
						HttpStatus.BAD_REQUEST,
						"Stock insuficiente para producto: " + detalleRequest.getIdProducto()
				);
			}

			BigDecimal subtotal = producto.getPrecio()
					.multiply(BigDecimal.valueOf(detalleRequest.getCantidad()));

			total = total.add(subtotal);

			PedidoDetalle detalle = PedidoDetalle.builder()
					.pedido(pedido)
					.idProducto(producto.getId())
					.nombreProducto(producto.getNombre())
					.cantidad(detalleRequest.getCantidad())
					.precioUnitario(producto.getPrecio())
					.subtotal(subtotal)
					.build();

			pedido.getDetalles().add(detalle);
		}

		pedido.setTotal(total);

		Pedido saved = pedidoRepository.save(pedido);

		for (var detalle : saved.getDetalles()) {
			productoClient.descontarStock(
					detalle.getIdProducto(),
					StockAjusteRequest.builder()
							.cantidad(detalle.getCantidad())
							.build()
			);
		}

		return toResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PedidoResponse> findAll() {
		return pedidoRepository.findAll()
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public PedidoResponse findById(Long id) {
		Pedido pedido = pedidoRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Pedido no encontrado: " + id
				));

		return toResponse(pedido);
	}

	@Override
	@Transactional
	public PedidoResponse actualizarEstado(Long id, EstadoPedido estado) {

		Pedido pedido = pedidoRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Pedido no encontrado: " + id
				));

		pedido.setEstado(estado);

		return toResponse(pedidoRepository.save(pedido));
	}

	private PedidoResponse toResponse(Pedido pedido) {

		return PedidoResponse.builder()
				.id(pedido.getId())
				.idUsuario(pedido.getIdUsuario())
				.total(pedido.getTotal())
				.estado(pedido.getEstado())
				.fechaCreacion(pedido.getFechaCreacion())
				.detalles(
						pedido.getDetalles() == null
								? List.of()
								: pedido.getDetalles().stream()
								  .map(detalle -> PedidoDetalleResponse.builder()
												  .idProducto(detalle.getIdProducto())
												  .nombreProducto(detalle.getNombreProducto())
												  .cantidad(detalle.getCantidad())
												  .precioUnitario(detalle.getPrecioUnitario())
												  .subtotal(detalle.getSubtotal())
												  .build())
								  .toList()
				)
				.build();
	}
}