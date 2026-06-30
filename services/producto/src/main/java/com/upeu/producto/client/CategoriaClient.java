package com.upeu.producto.client;

import com.upeu.producto.dto.CategoriaDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "categoria")
public interface CategoriaClient {

    @GetMapping("/api/v1/categorias/{id}")
    CategoriaDto findCategoriaById(@PathVariable("id") Long id);
}