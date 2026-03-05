package com.marcosmoreiradev.uensbackend.modules.estudiante.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.marcosmoreiradev.uensbackend.common.api.response.ApiResponse;
import com.marcosmoreiradev.uensbackend.common.api.response.PageResponseDto;
import com.marcosmoreiradev.uensbackend.common.api.util.ResponseFactory;
import com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto.AsignarSeccionVigenteRequestDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto.EstudianteCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto.EstudianteListItemDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto.EstudiantePatchEstadoRequestDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto.EstudianteResponseDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto.EstudianteUpdateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.application.EstudianteCommandService;
import com.marcosmoreiradev.uensbackend.modules.estudiante.application.EstudianteQueryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@Tag(name = "Estudiante", description = "Endpoints del modulo Estudiante.")
@RestController
@RequestMapping("/api/v1/estudiantes")
/**
 * Define la responsabilidad de EstudianteController dentro del backend UENS.
 * Contexto: modulo estudiante, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: exponer endpoints REST y delegar casos de uso sin filtrar reglas de negocio en capa HTTP.
 */
public class EstudianteController {

    private static final int MAX_PAGE_SIZE = 100;

    private final EstudianteCommandService commandService;
    private final EstudianteQueryService queryService;
/**
 * Construye la instancia de EstudianteController para operar en el modulo estudiante.
 * Contexto: capa api con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param commandService servicio de comandos que aplica reglas de negocio de escritura
     * @param queryService servicio de consultas que entrega lecturas optimizadas para API
 */

    public EstudianteController(EstudianteCommandService commandService, EstudianteQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @Operation(summary = "Listar.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
/**
 * Implementa la operacion 'listar' del modulo estudiante en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param q filtro de busqueda textual para consultas por coincidencia parcial
     * @param estado estado operativo del recurso (ACTIVO/INACTIVO) para filtrar o actualizar
     * @param seccionId identificador de la seccion academica (grado/paralelo/anio lectivo)
     * @param representanteLegalId identificador del representante legal principal del estudiante
     * @param page indice de pagina solicitado (base cero) para paginacion de resultados
     * @param size tamano de pagina solicitado respetando limites del contrato API
     * @param sort criterios de ordenamiento solicitados por cliente segun whitelist permitida
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ApiResponse<PageResponseDto<EstudianteListItemDto>>> listar(
            @RequestParam(required = false) @Size(max = 120) String q,
            @RequestParam(required = false) @Size(max = 20) String estado,
            @RequestParam(required = false) Long seccionId,
            @RequestParam(required = false) Long representanteLegalId,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(required = false) @Min(1) @Max(MAX_PAGE_SIZE) Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        Page<EstudianteListItemDto> result = queryService.listar(q, estado, seccionId, representanteLegalId, page, size, sort);
        return ResponseEntity.ok(ResponseFactory.page("Listado de estudiantes obtenido correctamente.", result));
    }

    @Operation(summary = "Operacion.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @GetMapping("/{estudianteId}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
/**
 * Implementa la operacion 'obtenerPorId' del modulo estudiante en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param estudianteId identificador del estudiante dentro del dominio academico
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiResponse<EstudianteResponseDto>> obtenerPorId(@PathVariable Long estudianteId) {
        return ResponseEntity.ok(ResponseFactory.ok("Estudiante obtenido correctamente.", queryService.obtenerPorId(estudianteId)));
    }

    @Operation(summary = "Operacion.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Operacion exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
/**
 * Implementa la operacion 'crear' del modulo estudiante en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiResponse<EstudianteResponseDto>> crear(@Valid @RequestBody EstudianteCreateRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseFactory.created("Estudiante creado correctamente.", commandService.crear(request)));
    }

    @Operation(summary = "Actualizar.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
/**
 * Implementa la operacion 'actualizar' del modulo estudiante en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param estudianteId identificador del estudiante dentro del dominio academico
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    @PutMapping("/{estudianteId}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ApiResponse<EstudianteResponseDto>> actualizar(
            @PathVariable Long estudianteId,
            @Valid @RequestBody EstudianteUpdateRequestDto request
    ) {
        return ResponseEntity.ok(ResponseFactory.ok("Estudiante actualizado correctamente.", commandService.actualizar(estudianteId, request)));
    }

    @Operation(summary = "Cambiar Estado.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
/**
 * Implementa la operacion 'cambiarEstado' del modulo estudiante en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param estudianteId identificador del estudiante dentro del dominio academico
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    @PatchMapping("/{estudianteId}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EstudianteResponseDto>> cambiarEstado(
            @PathVariable Long estudianteId,
            @Valid @RequestBody EstudiantePatchEstadoRequestDto request
    ) {
        return ResponseEntity.ok(ResponseFactory.ok("Estado de estudiante actualizado correctamente.", commandService.cambiarEstado(estudianteId, request)));
    }

    @Operation(summary = "Asignar Seccion Vigente.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
/**
 * Implementa la operacion 'asignarSeccionVigente' del modulo estudiante en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param estudianteId identificador del estudiante dentro del dominio academico
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    @PutMapping("/{estudianteId}/seccion-vigente")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ApiResponse<EstudianteResponseDto>> asignarSeccionVigente(
            @PathVariable Long estudianteId,
            @Valid @RequestBody AsignarSeccionVigenteRequestDto request
    ) {
        return ResponseEntity.ok(ResponseFactory.ok(
                "Seccion vigente actualizada correctamente.",
                commandService.asignarSeccionVigente(estudianteId, request)
        ));
    }
}





