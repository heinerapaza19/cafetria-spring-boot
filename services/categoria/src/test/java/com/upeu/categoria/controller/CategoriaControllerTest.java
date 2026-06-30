package com.upeu.categoria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upeu.categoria.controller.CategoriaController;
import com.upeu.categoria.dto.CategoriaRequest;
import com.upeu.categoria.dto.CategoriaResponse;
import com.upeu.categoria.exception.GlobalExceptionHandler;
import com.upeu.categoria.service.CategoriaService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoriaController.class)
@Import(GlobalExceptionHandler.class)
class CategoriaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private CategoriaService categoriaService;

        @Test
        void shouldReturnCategorias() throws Exception {
                when(categoriaService.findAll()).thenReturn(List.of(
                                CategoriaResponse.builder().id(1L).nombre("Tecnologia").descripcion("Productos")
                                                .build()));

                mockMvc.perform(get("/api/v1/categorias"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1L))
                                .andExpect(jsonPath("$[0].nombre").value("Tecnologia"));
        }

        @Test
        void shouldValidateCreateRequest() throws Exception {
                CategoriaRequest request = CategoriaRequest.builder()
                                .nombre("")
                                .descripcion("Productos")
                                .build();

                mockMvc.perform(post("/api/v1/categorias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Error de validación"))
                                .andExpect(jsonPath("$.validationErrors.nombre").exists());
        }
}