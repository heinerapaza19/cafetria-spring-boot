package com.upeu.producto.mapper;

import com.upeu.producto.dto.ProductoRequest;
import com.upeu.producto.dto.ProductoResponse;
import com.upeu.producto.entity.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    // =========================
    // ENTITY <- REQUEST
    // =========================
    // Convierte datos del frontend/Postman a entidad lista para BD
    public Producto toEntity(ProductoRequest request) {

        if (request == null)
            return null;

        return Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .idCategoria(request.getIdCategoria())
                .precio(request.getPrecio())
                .stock(request.getStock())

                // ❌ NO se guarda MultipartFile en BD
                // 👉 aquí NO va imagen
                .build();
    }

    // =========================
    // ENTITY <- RESPONSE
    // =========================
    // Lo que devuelves al frontend
    public ProductoResponse toResponse(Producto entity) {

        if (entity == null)
            return null;

        return ProductoResponse.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .idCategoria(entity.getIdCategoria())
                .precio(entity.getPrecio())
                .stock(entity.getStock())

                // ✅ aquí sí va el nombre de la imagen (String)
                .imagen(entity.getImagen())
                .build();
    }

    // =========================
    // UPDATE ENTITY <- REQUEST
    // =========================
    public void updateEntityFromRequest(Producto entity, ProductoRequest request) {

        entity.setNombre(request.getNombre());
        entity.setDescripcion(request.getDescripcion());
        entity.setIdCategoria(request.getIdCategoria());
        entity.setPrecio(request.getPrecio());
        entity.setStock(request.getStock());

        // ❌ NO guardar MultipartFile en entidad
        // 👉 la imagen se maneja en el SERVICE (archivo físico)
    }
}