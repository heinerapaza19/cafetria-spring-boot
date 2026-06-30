package com.upeu.pedido.client;

import com.upeu.pedido.dto.CategoriaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "categoria")
public interface CategoriaClient {

    @GetMapping("/api/v1/categorias/{id}")
    CategoriaDTO obtenerCategoria(@PathVariable Long id);
}