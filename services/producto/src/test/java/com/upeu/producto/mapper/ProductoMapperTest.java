package com.upeu.producto.mapper;

import com.upeu.producto.dto.ProductoRequest;
import com.upeu.producto.dto.ProductoResponse;
import com.upeu.producto.entity.Producto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductoMapperTest {

    private final ProductoMapper productoMapper = new ProductoMapper();

    @Test
    void shouldMapRequestToEntity() {
        ProductoRequest request = ProductoRequest.builder()
            .nombre("Laptop")
            .descripcion("Portatil de oficina")
            .idCategoria(3)
                .build();

        Producto entity = productoMapper.toEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getNombre()).isEqualTo("Laptop");
        assertThat(entity.getDescripcion()).isEqualTo("Portatil de oficina");
        assertThat(entity.getIdCategoria()).isEqualTo(3);
    }

    @Test
    void shouldMapEntityToResponse() {
        Producto entity = Producto.builder()
            .id(1)
            .nombre("Mouse")
            .descripcion("Periferico")
            .idCategoria(4)
                .build();

        ProductoResponse response = productoMapper.toResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getNombre()).isEqualTo("Mouse");
        assertThat(response.getDescripcion()).isEqualTo("Periferico");
        assertThat(response.getIdCategoria()).isEqualTo(4);
    }

    @Test
    void shouldUpdateEntityFromRequest() {
        Producto entity = Producto.builder()
            .id(1)
                .nombre("Anterior")
                .descripcion("Anterior descripcion")
            .idCategoria(1)
                .build();
        ProductoRequest request = ProductoRequest.builder()
                .nombre("Nueva")
                .descripcion("Nueva descripcion")
            .idCategoria(2)
                .build();

        productoMapper.updateEntityFromRequest(entity, request);

        assertThat(entity.getNombre()).isEqualTo("Nueva");
        assertThat(entity.getDescripcion()).isEqualTo("Nueva descripcion");
        assertThat(entity.getIdCategoria()).isEqualTo(2);
    }
}
