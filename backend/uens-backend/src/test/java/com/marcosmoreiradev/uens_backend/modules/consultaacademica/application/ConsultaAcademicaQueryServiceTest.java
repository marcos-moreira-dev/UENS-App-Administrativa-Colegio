package com.marcosmoreiradev.uens_backend.modules.consultaacademica.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.modules.consultaacademica.api.dto.DocentePorSeccionItemDto;
import com.marcosmoreiradev.uensbackend.modules.consultaacademica.api.dto.SeccionPorDocenteItemDto;
import com.marcosmoreiradev.uensbackend.modules.consultaacademica.application.ConsultaAcademicaQueryService;
import com.marcosmoreiradev.uensbackend.modules.consultaacademica.application.mapper.ConsultaAcademicaDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.consultaacademica.application.model.DocentePorSeccionRow;
import com.marcosmoreiradev.uensbackend.modules.consultaacademica.application.model.SeccionPorDocenteRow;
import com.marcosmoreiradev.uensbackend.modules.consultaacademica.infrastructure.persistence.repository.ConsultaAcademicaJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.docente.infrastructure.persistence.repository.DocenteJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.repository.SeccionJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultaAcademicaQueryServiceTest {

    @Mock
    private ConsultaAcademicaJpaRepository consultaAcademicaJpaRepository;
    @Mock
    private DocenteJpaRepository docenteJpaRepository;
    @Mock
    private SeccionJpaRepository seccionJpaRepository;

    private ConsultaAcademicaQueryService service;

    @BeforeEach
    void setUp() {
        service = new ConsultaAcademicaQueryService(
                consultaAcademicaJpaRepository,
                docenteJpaRepository,
                seccionJpaRepository,
                new ConsultaAcademicaDtoMapper()
        );
    }

    @Test
    void obtenerDocentesPorSeccionFailsWhenSeccionNoExiste() {
        when(seccionJpaRepository.existsById(44L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.obtenerDocentesPorSeccion(44L));
        verifyNoInteractions(consultaAcademicaJpaRepository);
    }

    @Test
    void obtenerDocentesPorSeccionMapsRows() {
        when(seccionJpaRepository.existsById(10L)).thenReturn(true);
        when(consultaAcademicaJpaRepository.findDocentesPorSeccionId(10L)).thenReturn(List.of(
                new DocentePorSeccionRow(5L, "Kevin", "Alvarez", "kevin@uens.test", "099", "ACTIVO", null)
        ));

        List<DocentePorSeccionItemDto> result = service.obtenerDocentesPorSeccion(10L);

        assertEquals(1, result.size());
        assertEquals(5L, result.getFirst().docenteId());
        assertEquals("Kevin", result.getFirst().nombres());
        assertEquals(0L, result.getFirst().clasesActivasAsignadas());
    }

    @Test
    void obtenerSeccionesPorDocenteFailsWhenDocenteNoExiste() {
        when(docenteJpaRepository.existsById(11L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.obtenerSeccionesPorDocente(11L));
        verifyNoInteractions(consultaAcademicaJpaRepository);
    }

    @Test
    void obtenerSeccionesPorDocenteMapsRows() {
        when(docenteJpaRepository.existsById(8L)).thenReturn(true);
        when(consultaAcademicaJpaRepository.findSeccionesPorDocenteId(8L)).thenReturn(List.of(
                new SeccionPorDocenteRow(22L, (short) 3, "B", "2026-2027", "ACTIVO", 2L)
        ));

        List<SeccionPorDocenteItemDto> result = service.obtenerSeccionesPorDocente(8L);

        assertEquals(1, result.size());
        assertEquals(22L, result.getFirst().seccionId());
        assertEquals(3, result.getFirst().grado());
        assertEquals(2L, result.getFirst().clasesActivasAsignadas());
    }
}
