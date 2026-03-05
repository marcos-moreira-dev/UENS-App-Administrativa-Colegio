package com.marcosmoreiradev.uensbackend.modules.consultaacademica.application.mapper;

import com.marcosmoreiradev.uensbackend.modules.consultaacademica.api.dto.DocentePorSeccionItemDto;
import com.marcosmoreiradev.uensbackend.modules.consultaacademica.api.dto.SeccionPorDocenteItemDto;
import com.marcosmoreiradev.uensbackend.modules.consultaacademica.application.model.DocentePorSeccionRow;
import com.marcosmoreiradev.uensbackend.modules.consultaacademica.application.model.SeccionPorDocenteRow;
import org.springframework.stereotype.Component;

/**
 * Mapper de filas de lectura hacia contratos API del modulo de consultas
 * academicas.
 */
@Component
public class ConsultaAcademicaDtoMapper {

    public DocentePorSeccionItemDto toDocentePorSeccionItemDto(DocentePorSeccionRow row) {
        return new DocentePorSeccionItemDto(
                row.docenteId(),
                row.nombres(),
                row.apellidos(),
                row.correoElectronico(),
                row.telefono(),
                row.estado(),
                row.clasesActivasAsignadas() == null ? 0L : row.clasesActivasAsignadas()
        );
    }

    public SeccionPorDocenteItemDto toSeccionPorDocenteItemDto(SeccionPorDocenteRow row) {
        return new SeccionPorDocenteItemDto(
                row.seccionId(),
                row.grado() == null ? null : row.grado().intValue(),
                row.paralelo(),
                row.anioLectivo(),
                row.estado(),
                row.clasesActivasAsignadas() == null ? 0L : row.clasesActivasAsignadas()
        );
    }
}
