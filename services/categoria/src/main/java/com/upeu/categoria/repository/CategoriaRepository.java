package com.upeu.categoria.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upeu.categoria.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
