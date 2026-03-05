package com.marcosmoreiradev.uensbackend.modules.consultaacademica.api;

import com.marcosmoreiradev.uensbackend.common.api.response.ApiResponse;
import com.marcosmoreiradev.uensbackend.common.api.util.ResponseFactory;
import com.marcosmoreiradev.uensbackend.modules.consultaacademica.api.dto.DocentePorSeccionItemDto;
import com.marcosmoreiradev.uensbackend.modules.consultaacademica.api.dto.SeccionPorDocenteItemDto;
import com.marcosmoreiradev.uensbackend.modules.consultaacademica.application.ConsultaAcademicaQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints de consultas academicas multi-entidad para apoyo administrativo.
 */
@Validated
@Tag(name = "Consultas Academicas", description = "Consultas agregadas derivadas del contexto academico.")
@RestController
@RequestMapping("/api/v1/consultas")
public class ConsultaAcademicaController {

    private final ConsultaAcademicaQueryService queryService;

    public ConsultaAcademicaController(ConsultaAcademicaQueryService queryService) {
        this.queryService = queryService;
    }

    @Operation(summary = "Consultar docentes por seccion.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @GetMapping("/docentes-por-seccion")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ApiResponse<List<DocentePorSeccionItemDto>>> docentesPorSeccion(
            @RequestParam @Min(1) Long seccionId
    ) {
        return ResponseEntity.ok(ResponseFactory.ok(
                "Docentes asociados a la seccion obtenidos correctamente.",
                queryService.obtenerDocentesPorSeccion(seccionId)
        ));
    }

    @Operation(summary = "Consultar secciones por docente.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @GetMapping("/secciones-por-docente")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ApiResponse<List<SeccionPorDocenteItemDto>>> seccionesPorDocente(
            @RequestParam @Min(1) Long docenteId
    ) {
        return ResponseEntity.ok(ResponseFactory.ok(
                "Secciones asociadas al docente obtenidas correctamente.",
                queryService.obtenerSeccionesPorDocente(docenteId)
        ));
    }
}
