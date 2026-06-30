package com.upeu.producto.service.impl;

import com.upeu.producto.client.CategoriaClient;
import com.upeu.producto.dto.ProductoRequest;
import com.upeu.producto.dto.ProductoResponse;
import com.upeu.producto.entity.Producto;
import com.upeu.producto.exception.ResourceNotFoundException;
import com.upeu.producto.mapper.ProductoMapper;
import com.upeu.producto.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaClient categoriaClient; // 🔥 FALTABA ESTO

    @Spy
    private ProductoMapper productoMapper = new ProductoMapper();

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    void shouldCreateProducto() {

        ProductoRequest request = ProductoRequest.builder()
                .nombre("Laptop")
                .descripcion("Portatil de oficina")
                .idCategoria(3)
                .precio(new java.math.BigDecimal("1000"))
                .stock(10)
                .build();

        Producto savedEntity = Producto.builder()
                .id(1)
                .nombre("Laptop")
                .descripcion("Portatil de oficina")
                .idCategoria(3)
                .precio(new java.math.BigDecimal("1000"))
                .stock(10)
                .build();

        when(productoRepository.save(any(Producto.class)))
                .thenReturn(savedEntity);

        ProductoResponse response = productoService.create(request);

        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getNombre()).isEqualTo("Laptop");
        assertThat(response.getIdCategoria()).isEqualTo(3);
    }

    @Test
    void shouldThrowWhenProductoNotFound() {

        when(productoRepository.findById(99))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}