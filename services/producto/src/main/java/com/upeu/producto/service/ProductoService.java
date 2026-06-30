package com.upeu.producto.service;

import com.upeu.producto.dto.ProductoRequest;
import com.upeu.producto.dto.ProductoResponse;

import java.util.List;

public interface ProductoService {

    ProductoResponse create(ProductoRequest request);

    List<ProductoResponse> findAll();

    ProductoResponse findById(Integer id);

    ProductoResponse update(Integer id, ProductoRequest request);

    void delete(Integer id);

    ProductoResponse findDetalleById(Integer id);
}
