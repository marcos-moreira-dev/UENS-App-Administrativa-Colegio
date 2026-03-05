package com.marcosmoreiradev.uens_backend.modules.estudiante.api;

import com.marcosmoreiradev.uensbackend.common.api.response.ApiResponse;
import com.marcosmoreiradev.uensbackend.common.api.response.PageResponseDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.api.EstudianteController;
import com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto.EstudianteCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto.EstudianteListItemDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto.EstudianteResponseDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.application.EstudianteCommandService;
import com.marcosmoreiradev.uensbackend.modules.estudiante.application.EstudianteQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
/**
 * Define la responsabilidad de EstudianteControllerTest dentro del backend UENS.
 * Contexto: modulo estudiante, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
class EstudianteControllerTest {

    @Mock
    private EstudianteCommandService commandService;

    @Mock
    private EstudianteQueryService queryService;

    private EstudianteController controller;

    @BeforeEach
    void setUp() {
        controller = new EstudianteController(commandService, queryService);
    }

    @Test
    void listarReturnsPageResponse() throws Exception {
        EstudianteListItemDto item = new EstudianteListItemDto(
                1L, "Juan", "Perez", LocalDate.of(2015, 1, 10), "ACTIVO", 10L, 20L
        );

        when(queryService.listar(any(), any(), any(), any(), eq(0), eq(10), anyList()))
                .thenReturn(new PageImpl<>(List.of(item), PageRequest.of(0, 10), 1));

        ResponseEntity<ApiResponse<PageResponseDto<EstudianteListItemDto>>> entity =
                controller.listar(null, null, null, null, 0, 10, List.of());

        assertEquals(200, entity.getStatusCode().value());
        assertTrue(entity.getBody().isOk());
        assertEquals(1, entity.getBody().getData().getItems().size());
        assertEquals(1L, entity.getBody().getData().getTotalElements());
    }

    @Test
    void crearReturnsCreated() throws Exception {
        EstudianteResponseDto response = new EstudianteResponseDto(
                99L, "Juan", "Perez", LocalDate.of(2015, 1, 10), "ACTIVO", 10L, 20L
        );

        when(commandService.crear(any())).thenReturn(response);

        ResponseEntity<ApiResponse<EstudianteResponseDto>> entity = controller.crear(
                new EstudianteCreateRequestDto("Juan", "Perez", LocalDate.of(2015, 1, 10), 10L, 20L)
        );

        assertEquals(201, entity.getStatusCode().value());
        assertTrue(entity.getBody().isOk());
        assertEquals(99L, entity.getBody().getData().id());
        assertEquals("Estudiante creado correctamente.", entity.getBody().getMessage());
    }
}

