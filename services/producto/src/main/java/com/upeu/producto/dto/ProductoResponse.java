package com.upeu.producto.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {

    private Integer id;
    private String nombre;
    private String descripcion;
    private Integer idCategoria;

    private BigDecimal precio;
    private Integer stock;
    private String imagen;

    private CategoriaDto categoria;
}