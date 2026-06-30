package com.upeu.categoria.mapper;

import org.springframework.stereotype.Component;

import com.upeu.categoria.dto.CategoriaRequest;
import com.upeu.categoria.dto.CategoriaResponse;
import com.upeu.categoria.entity.Categoria;

@Component
public class CategoriaMapper {

    // Convierte el request que viene del frontend
    // hacia la entidad Categoria para guardar en MySQL
    public Categoria toEntity(CategoriaRequest request) {

        // Validación simple por si request viene null
        if (request == null) {
            return null;
        }

        // Construye la entidad Categoria
        return Categoria.builder()

                // Asigna nombre
                .nombre(request.getNombre())

                // Asigna descripción
                .descripcion(request.getDescripcion())

                .build();
    }

    // Convierte la entidad Categoria
    // hacia CategoriaResponse para devolver al frontend
    public CategoriaResponse toResponse(Categoria entity) {

        // Validación por seguridad
        if (entity == null) {
            return null;
        }

        // Construye el response
        return CategoriaResponse.builder()

                // ID de la categoría
                .id(entity.getId())

                // Nombre
                .nombre(entity.getNombre())

                // Descripción
                .descripcion(entity.getDescripcion())

                // Nombre de la imagen guardada
                .imagen(entity.getImagen())

                .build();
    }

    // Actualiza una categoría existente
    // usando los nuevos datos del request
    public void updateEntityFromRequest(
            Categoria entity,
            CategoriaRequest request) {

        // Actualiza nombre
        entity.setNombre(request.getNombre());

        // Actualiza descripción
        entity.setDescripcion(request.getDescripcion());
    }
}