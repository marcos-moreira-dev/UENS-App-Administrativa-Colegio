package com.marcosmoreiradev.uens_backend.modules.estudiante.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.BusinessRuleException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.StudentErrorCodes;
import com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto.EstudianteCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto.EstudianteResponseDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.application.EstudianteCommandService;
import com.marcosmoreiradev.uensbackend.modules.estudiante.application.mapper.EstudianteDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.estudiante.application.validator.EstudianteRequestValidator;
import com.marcosmoreiradev.uensbackend.modules.estudiante.infrastructure.persistence.entity.EstudianteJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.estudiante.infrastructure.persistence.repository.EstudianteJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.representante.infrastructure.persistence.entity.RepresentanteLegalJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.representante.infrastructure.persistence.repository.RepresentanteLegalJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.entity.SeccionJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.repository.SeccionJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
/**
 * Define la responsabilidad de EstudianteCommandServiceTest dentro del backend UENS.
 * Contexto: modulo estudiante, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
class EstudianteCommandServiceTest {

    @Mock
    private EstudianteJpaRepository estudianteRepository;
    @Mock
    private RepresentanteLegalJpaRepository representanteRepository;
    @Mock
    private SeccionJpaRepository seccionRepository;
    @Mock
    private EstudianteDtoMapper mapper;

    private EstudianteCommandService service;

    @BeforeEach
    void setUp() {
        service = new EstudianteCommandService(
                estudianteRepository,
                representanteRepository,
                seccionRepository,
                mapper,
                new EstudianteRequestValidator()
        );
    }

    @Test
    void crearFailsWhenDuplicate() {
        EstudianteCreateRequestDto request = new EstudianteCreateRequestDto(
                "Juan",
                "Perez",
                LocalDate.of(2015, 1, 10),
                10L,
                20L
        );
        when(estudianteRepository.existsByNombresIgnoreCaseAndApellidosIgnoreCaseAndFechaNacimiento(
                "Juan", "Perez", request.fechaNacimiento()
        )).thenReturn(true);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(request));

        assertEquals(StudentErrorCodes.RN_EST_01_ESTUDIANTE_DUPLICADO, ex.getErrorCode());
    }

    @Test
    void crearFailsWhenSeccionWithoutCapacity() {
        EstudianteCreateRequestDto request = new EstudianteCreateRequestDto(
                "Juan",
                "Perez",
                LocalDate.of(2015, 1, 10),
                10L,
                20L
        );
        when(estudianteRepository.existsByNombresIgnoreCaseAndApellidosIgnoreCaseAndFechaNacimiento(
                "Juan", "Perez", request.fechaNacimiento()
        )).thenReturn(false);

        RepresentanteLegalJpaEntity representante =
                new RepresentanteLegalJpaEntity("Ana", "Lopez", "099", "ana@mail.com");
        ReflectionTestUtils.setField(representante, "id", 10L);
        when(representanteRepository.findById(10L)).thenReturn(Optional.of(representante));

        SeccionJpaEntity seccion = SeccionJpaEntity.crear(3, "A", 1, "2025-2026");
        ReflectionTestUtils.setField(seccion, "id", 20L);
        when(seccionRepository.findById(20L)).thenReturn(Optional.of(seccion));
        when(estudianteRepository.countBySeccion_Id(20L)).thenReturn(1L);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(request));

        assertEquals(StudentErrorCodes.RN_EST_04_CUPO_SECCION_AGOTADO, ex.getErrorCode());
    }

    @Test
    void crearSuccessReturnsMappedResponse() {
        EstudianteCreateRequestDto request = new EstudianteCreateRequestDto(
                "Juan",
                "Perez",
                LocalDate.of(2015, 1, 10),
                10L,
                20L
        );
        when(estudianteRepository.existsByNombresIgnoreCaseAndApellidosIgnoreCaseAndFechaNacimiento(
                "Juan", "Perez", request.fechaNacimiento()
        )).thenReturn(false);

        RepresentanteLegalJpaEntity representante =
                new RepresentanteLegalJpaEntity("Ana", "Lopez", "099", "ana@mail.com");
        ReflectionTestUtils.setField(representante, "id", 10L);
        when(representanteRepository.findById(10L)).thenReturn(Optional.of(representante));

        SeccionJpaEntity seccion = SeccionJpaEntity.crear(3, "A", 30, "2025-2026");
        ReflectionTestUtils.setField(seccion, "id", 20L);
        when(seccionRepository.findById(20L)).thenReturn(Optional.of(seccion));
        when(estudianteRepository.countBySeccion_Id(20L)).thenReturn(0L);

        EstudianteJpaEntity saved =
                EstudianteJpaEntity.crear("Juan", "Perez", request.fechaNacimiento(), representante, seccion);
        ReflectionTestUtils.setField(saved, "id", 99L);
        when(estudianteRepository.save(any(EstudianteJpaEntity.class))).thenReturn(saved);

        EstudianteResponseDto response = new EstudianteResponseDto(
                99L,
                "Juan",
                "Perez",
                request.fechaNacimiento(),
                "ACTIVO",
                10L,
                20L
        );
        when(mapper.toResponseDto(saved)).thenReturn(response);

        EstudianteResponseDto result = service.crear(request);

        assertEquals(99L, result.id());
        assertEquals("ACTIVO", result.estado());
    }
}
