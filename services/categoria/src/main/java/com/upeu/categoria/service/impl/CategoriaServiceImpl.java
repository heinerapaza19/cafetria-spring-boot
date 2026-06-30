package com.upeu.categoria.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.upeu.categoria.dto.CategoriaRequest;
import com.upeu.categoria.dto.CategoriaResponse;
import com.upeu.categoria.entity.Categoria;
import com.upeu.categoria.exception.ResourceNotFoundException;
import com.upeu.categoria.mapper.CategoriaMapper;
import com.upeu.categoria.repository.CategoriaRepository;
import com.upeu.categoria.service.CategoriaService;

import java.util.List;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    // =========================
    // CREAR CATEGORIA
    // =========================
    @Override
    @Transactional
    public CategoriaResponse create(CategoriaRequest request) {

        try {

            log.info("Iniciando creación de categoría con nombre: {}",
                    request.getNombre());

            String nombreImagen = null;

            // Verifica si viene imagen
            if (request.getImagen() != null
                    && !request.getImagen().isEmpty()) {

                // Obtiene el nombre original
                nombreImagen = request.getImagen().getOriginalFilename();

                // Ruta donde se guardará la imagen
                Path ruta = Paths.get("uploads/categorias");

                // Si no existe carpeta, la crea
                if (!Files.exists(ruta)) {
                    Files.createDirectories(ruta);
                }

                // Guarda la imagen
                Files.copy(
                        request.getImagen().getInputStream(),
                        ruta.resolve(nombreImagen),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            // Convierte request a entidad
            Categoria categoria = categoriaMapper.toEntity(request);

            // Guarda nombre de imagen
            categoria.setImagen(nombreImagen);

            // Guarda en BD
            Categoria savedCategoria = categoriaRepository.save(categoria);

            log.info("Categoría creada exitosamente con ID: {}",
                    savedCategoria.getId());

            return categoriaMapper.toResponse(savedCategoria);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error al guardar imagen: "
                            + e.getMessage());
        }
    }

    // =========================
    // LISTAR TODAS
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> findAll() {

        log.info("Recuperando lista de categorías");

        List<CategoriaResponse> categorias = categoriaRepository.findAll()
                .stream()
                .map(categoriaMapper::toResponse)
                .toList();

        log.info("Se encontraron {} categorías",
                categorias.size());

        return categorias;
    }

    // =========================
    // BUSCAR POR ID
    // =========================
    @Override
    @Transactional(readOnly = true)
    public CategoriaResponse findById(Long id) {

        log.info("Buscando categoría con ID: {}", id);

        Categoria categoria = getCategoriaById(id);

        return categoriaMapper.toResponse(categoria);
    }

    // =========================
    // ACTUALIZAR
    // =========================
    @Override
    @Transactional
    public CategoriaResponse update(Long id,
            CategoriaRequest request) {

        try {

            log.info("Iniciando actualización de categoría ID: {}",
                    id);

            Categoria categoria = getCategoriaById(id);

            // Actualiza nombre
            categoria.setNombre(request.getNombre());

            // Actualiza descripción
            categoria.setDescripcion(request.getDescripcion());

            // Verifica si viene imagen
            if (request.getImagen() != null
                    && !request.getImagen().isEmpty()) {

                // Obtiene nombre original
                String nombreImagen = request.getImagen().getOriginalFilename();

                // Ruta
                Path ruta = Paths.get("uploads/categorias");

                // Crea carpeta si no existe
                if (!Files.exists(ruta)) {
                    Files.createDirectories(ruta);
                }

                // Guarda imagen
                Files.copy(
                        request.getImagen().getInputStream(),
                        ruta.resolve(nombreImagen),
                        StandardCopyOption.REPLACE_EXISTING);

                // Guarda nombre en BD
                categoria.setImagen(nombreImagen);
            }

            Categoria updatedCategoria = categoriaRepository.save(categoria);

            log.info("Categoría ID: {} actualizada exitosamente",
                    id);

            return categoriaMapper.toResponse(updatedCategoria);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error al actualizar categoría: "
                            + e.getMessage());
        }
    }

    // =========================
    // ELIMINAR
    // =========================
    @Override
    @Transactional
    public void delete(Long id) {

        log.info("Iniciando eliminación de categoría ID: {}",
                id);

        Categoria categoria = getCategoriaById(id);

        // Elimina imagen física si existe
        if (categoria.getImagen() != null) {

            try {

                Path ruta = Paths.get(
                        "uploads/categorias",
                        categoria.getImagen());

                Files.deleteIfExists(ruta);

            } catch (Exception e) {

                log.error("Error eliminando imagen: {}",
                        e.getMessage());
            }
        }

        // Elimina de MySQL
        categoriaRepository.deleteById(id);

        log.info("Categoría ID: {} eliminada exitosamente",
                id);
    }

    // =========================
    // BUSCAR CATEGORIA
    // =========================
    private Categoria getCategoriaById(Long id) {

        return categoriaRepository.findById(id)
                .orElseThrow(() -> {

                    log.warn(
                            "Categoría no encontrada: ID {}",
                            id);

                    return new ResourceNotFoundException(
                            "Categoría con id "
                                    + id
                                    + " no encontrada");
                });
    }
}