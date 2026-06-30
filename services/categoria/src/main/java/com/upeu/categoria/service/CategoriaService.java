package com.upeu.categoria.service;

import java.util.List;

import com.upeu.categoria.dto.CategoriaRequest;
import com.upeu.categoria.dto.CategoriaResponse;

public interface CategoriaService {

    CategoriaResponse create(CategoriaRequest request);

    List<CategoriaResponse> findAll();

    CategoriaResponse findById(Long id);

    CategoriaResponse update(Long id, CategoriaRequest request);

    void delete(Long id);
}
