package com.upeu.producto.service.impl;

import com.upeu.producto.client.CategoriaClient;
import com.upeu.producto.dto.CategoriaDto;
import com.upeu.producto.dto.ProductoRequest;
import com.upeu.producto.dto.ProductoResponse;
import com.upeu.producto.entity.Producto;
import com.upeu.producto.exception.ResourceNotFoundException;
import com.upeu.producto.mapper.ProductoMapper;
import com.upeu.producto.repository.ProductoRepository;
import com.upeu.producto.service.ProductoService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final CategoriaClient categoriaClient;

    // =========================
    // CREAR PRODUCTO (POST + IMAGEN + CATEGORIA)
    // =========================
    @Override
    @Transactional
    public ProductoResponse create(ProductoRequest request) {

        try {

            log.info("Creando producto: {}", request.getNombre());

            String nombreImagen = null;

            // Guardar imagen en carpeta local
            if (request.getImagen() != null && !request.getImagen().isEmpty()) {

                nombreImagen = request.getImagen().getOriginalFilename();

                Path ruta = Paths.get("uploads/productos");

                if (!Files.exists(ruta)) {
                    Files.createDirectories(ruta);
                }

                Files.copy(
                        request.getImagen().getInputStream(),
                        ruta.resolve(nombreImagen),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            // Guardar producto en BD
            Producto producto = productoMapper.toEntity(request);
            producto.setImagen(nombreImagen);

            Producto saved = productoRepository.save(producto);

            // 🔥 TRAER CATEGORÍA (ESTO FALTABA EN POST)
            CategoriaDto categoria = null;

            try {
                categoria = categoriaClient.findCategoriaById(
                        saved.getIdCategoria().longValue());
            } catch (Exception e) {
                log.warn("No se pudo obtener categoría en CREATE: {}", e.getMessage());
            }

            return ProductoResponse.builder()
                    .id(saved.getId())
                    .nombre(saved.getNombre())
                    .descripcion(saved.getDescripcion())
                    .idCategoria(saved.getIdCategoria())
                    .precio(saved.getPrecio())
                    .stock(saved.getStock())
                    .imagen(saved.getImagen())
                    .categoria(categoria)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error creando producto: " + e.getMessage());
        }
    }

    // =========================
    // LISTAR TODOS (GET)
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> findAll() {

        return productoRepository.findAll()
                .stream()
                .map(producto -> {

                    CategoriaDto categoria = null;

                    try {
                        categoria = categoriaClient.findCategoriaById(
                                producto.getIdCategoria().longValue());
                    } catch (Exception e) {
                        log.warn("No se pudo obtener categoria id {}", producto.getIdCategoria());
                    }

                    return ProductoResponse.builder()
                            .id(producto.getId())
                            .nombre(producto.getNombre())
                            .descripcion(producto.getDescripcion())
                            .idCategoria(producto.getIdCategoria())
                            .precio(producto.getPrecio())
                            .stock(producto.getStock())
                            .imagen(producto.getImagen())
                            .categoria(categoria)
                            .build();
                })
                .toList();
    }

    // =========================
    // BUSCAR POR ID
    // =========================
    @Override
    @Transactional(readOnly = true)
    public ProductoResponse findById(Integer id) {

        Producto producto = getProductoById(id);

        CategoriaDto categoria = null;

        try {
            categoria = categoriaClient.findCategoriaById(
                    producto.getIdCategoria().longValue());
        } catch (Exception e) {
            log.warn("Error obteniendo categoría: {}", e.getMessage());
        }

        return ProductoResponse.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .idCategoria(producto.getIdCategoria())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .imagen(producto.getImagen())
                .categoria(categoria)
                .build();
    }

    // =========================
    // ACTUALIZAR
    // =========================
    @Override
    @Transactional
    public ProductoResponse update(Integer id, ProductoRequest request) {

        Producto producto = getProductoById(id);

        productoMapper.updateEntityFromRequest(producto, request);

        Producto updated = productoRepository.save(producto);

        // 🔥 TRAER CATEGORÍA (igual que GET y POST)
        CategoriaDto categoria = null;

        try {
            categoria = categoriaClient.findCategoriaById(
                    updated.getIdCategoria().longValue());
        } catch (Exception e) {
            log.warn("No se pudo obtener categoria en UPDATE: {}", e.getMessage());
        }

        return ProductoResponse.builder()
                .id(updated.getId())
                .nombre(updated.getNombre())
                .descripcion(updated.getDescripcion())
                .idCategoria(updated.getIdCategoria())
                .precio(updated.getPrecio())
                .stock(updated.getStock())
                .imagen(updated.getImagen())
                .categoria(categoria) // 🔥 AQUÍ estaba el problema
                .build();
    }

    // =========================
    // ELIMINAR
    // =========================
    @Override
    @Transactional
    public void delete(Integer id) {

        Producto producto = getProductoById(id);

        try {
            if (producto.getImagen() != null) {
                Path ruta = Paths.get("uploads/productos", producto.getImagen());
                Files.deleteIfExists(ruta);
            }
        } catch (Exception e) {
            log.error("Error eliminando imagen: {}", e.getMessage());
        }

        productoRepository.deleteById(id);
    }

    // =========================
    // DETALLE CON CIRCUIT BREAKER
    // =========================
    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "categoria", fallbackMethod = "fallbackCategoria")
    public ProductoResponse findDetalleById(Integer id) {

        Producto producto = getProductoById(id);

        CategoriaDto categoria = categoriaClient.findCategoriaById(
                producto.getIdCategoria().longValue());

        return ProductoResponse.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .idCategoria(producto.getIdCategoria())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .imagen(producto.getImagen())
                .categoria(categoria)
                .build();
    }

    // =========================
    // FALLBACK
    // =========================
    public ProductoResponse fallbackCategoria(Integer id, Throwable ex) {

        Producto producto = getProductoById(id);

        return ProductoResponse.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .idCategoria(producto.getIdCategoria())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .imagen(producto.getImagen())
                .categoria(null)
                .build();
    }

    // =========================
    // UTIL
    // =========================
    private Producto getProductoById(Integer id) {

        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto con id " + id + " no encontrado"));
    }
}