package com.upeu.producto.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ProductoRequest {

    @NotBlank
    // Valida que el campo no sea null ni vacío ni solo espacios
    private String nombre;

    private String descripcion;
    // Campo opcional, puede venir vacío

    @NotNull
    // No puede ser null (debe venir sí o sí desde el request)
    private Integer idCategoria;

    @NotNull
    // Precio obligatorio, se usa BigDecimal para dinero
    private BigDecimal precio;

    @NotNull
    // Stock obligatorio (cantidad disponible)
    private Integer stock;

    // IMAGEN DEL PRODUCTO
    // MultipartFile permite recibir archivos desde Postman (form-data)
    // Aquí no se guarda el archivo, solo se recibe temporalmente
    private MultipartFile imagen;
}