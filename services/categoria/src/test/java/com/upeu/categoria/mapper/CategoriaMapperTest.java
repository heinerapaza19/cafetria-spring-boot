package com.upeu.categoria.mapper;

import org.junit.jupiter.api.Test;

import com.upeu.categoria.dto.CategoriaRequest;
import com.upeu.categoria.dto.CategoriaResponse;
import com.upeu.categoria.entity.Categoria;
import com.upeu.categoria.mapper.CategoriaMapper;

import static org.assertj.core.api.Assertions.assertThat;

class CategoriaMapperTest {

    private final CategoriaMapper categoriaMapper = new CategoriaMapper();

    @Test
    void shouldMapRequestToEntity() {
        CategoriaRequest request = CategoriaRequest.builder()
                .nombre("Tecnologia")
                .descripcion("Productos tecnologicos")
                .build();

        Categoria entity = categoriaMapper.toEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getNombre()).isEqualTo("Tecnologia");
        assertThat(entity.getDescripcion()).isEqualTo("Productos tecnologicos");
    }

    @Test
    void shouldMapEntityToResponse() {
        Categoria entity = Categoria.builder()
                .id(1L)
                .nombre("Hogar")
                .descripcion("Productos del hogar")
                .build();

        CategoriaResponse response = categoriaMapper.toResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNombre()).isEqualTo("Hogar");
        assertThat(response.getDescripcion()).isEqualTo("Productos del hogar");
    }

    @Test
    void shouldUpdateEntityFromRequest() {
        Categoria entity = Categoria.builder()
                .id(1L)
                .nombre("Anterior")
                .descripcion("Anterior descripcion")
                .build();
        CategoriaRequest request = CategoriaRequest.builder()
                .nombre("Nueva")
                .descripcion("Nueva descripcion")
                .build();

        categoriaMapper.updateEntityFromRequest(entity, request);

        assertThat(entity.getNombre()).isEqualTo("Nueva");
        assertThat(entity.getDescripcion()).isEqualTo("Nueva descripcion");
    }
}
