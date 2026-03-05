package com.marcosmoreiradev.uensbackend.modules.consultaacademica.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.modules.consultaacademica.api.dto.DocentePorSeccionItemDto;
import com.marcosmoreiradev.uensbackend.modules.consultaacademica.api.dto.SeccionPorDocenteItemDto;
import com.marcosmoreiradev.uensbackend.modules.consultaacademica.application.mapper.ConsultaAcademicaDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.consultaacademica.infrastructure.persistence.repository.ConsultaAcademicaJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.docente.infrastructure.persistence.repository.DocenteJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.repository.SeccionJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de lectura agregada para consultas academicas multi-entidad.
 */
@Service
public class ConsultaAcademicaQueryService {

    private final ConsultaAcademicaJpaRepository consultaAcademicaJpaRepository;
    private final DocenteJpaRepository docenteJpaRepository;
    private final SeccionJpaRepository seccionJpaRepository;
    private final ConsultaAcademicaDtoMapper mapper;

    public ConsultaAcademicaQueryService(
            ConsultaAcademicaJpaRepository consultaAcademicaJpaRepository,
            DocenteJpaRepository docenteJpaRepository,
            SeccionJpaRepository seccionJpaRepository,
            ConsultaAcademicaDtoMapper mapper
    ) {
        this.consultaAcademicaJpaRepository = consultaAcademicaJpaRepository;
        this.docenteJpaRepository = docenteJpaRepository;
        this.seccionJpaRepository = seccionJpaRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<DocentePorSeccionItemDto> obtenerDocentesPorSeccion(Long seccionId) {
        ensureSeccionExists(seccionId);
        return consultaAcademicaJpaRepository.findDocentesPorSeccionId(seccionId).stream()
                .map(mapper::toDocentePorSeccionItemDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SeccionPorDocenteItemDto> obtenerSeccionesPorDocente(Long docenteId) {
        ensureDocenteExists(docenteId);
        return consultaAcademicaJpaRepository.findSeccionesPorDocenteId(docenteId).stream()
                .map(mapper::toSeccionPorDocenteItemDto)
                .toList();
    }

    private void ensureSeccionExists(Long seccionId) {
        if (!seccionJpaRepository.existsById(seccionId)) {
            throw new ResourceNotFoundException("Seccion no encontrada.");
        }
    }

    private void ensureDocenteExists(Long docenteId) {
        if (!docenteJpaRepository.existsById(docenteId)) {
            throw new ResourceNotFoundException("Docente no encontrado.");
        }
    }
}
