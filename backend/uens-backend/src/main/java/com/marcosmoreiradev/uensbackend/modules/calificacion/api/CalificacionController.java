package com.marcosmoreiradev.uensbackend.modules.calificacion.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.marcosmoreiradev.uensbackend.common.api.response.ApiResponse;
import com.marcosmoreiradev.uensbackend.common.api.response.PageResponseDto;
import com.marcosmoreiradev.uensbackend.common.api.util.ResponseFactory;
import com.marcosmoreiradev.uensbackend.modules.calificacion.api.dto.CalificacionCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.calificacion.api.dto.CalificacionListItemDto;
import com.marcosmoreiradev.uensbackend.modules.calificacion.api.dto.CalificacionResponseDto;
import com.marcosmoreiradev.uensbackend.modules.calificacion.api.dto.CalificacionUpdateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.calificacion.application.CalificacionCommandService;
import com.marcosmoreiradev.uensbackend.modules.calificacion.application.CalificacionQueryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@Tag(name = "Calificacion", description = "Endpoints del modulo Calificacion.")
@RestController
@RequestMapping("/api/v1/calificaciones")
/**
 * Define la responsabilidad de CalificacionController dentro del backend UENS.
 * Contexto: modulo calificacion, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: exponer endpoints REST y delegar casos de uso sin filtrar reglas de negocio en capa HTTP.
 */
public class CalificacionController {

    private static final int MAX_PAGE_SIZE = 100;

    private final CalificacionCommandService commandService;
    private final CalificacionQueryService queryService;
/**
 * Construye la instancia de CalificacionController para operar en el modulo calificacion.
 * Contexto: capa api con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param commandService servicio de comandos que aplica reglas de negocio de escritura
     * @param queryService servicio de consultas que entrega lecturas optimizadas para API
 */

    public CalificacionController(CalificacionCommandService commandService, CalificacionQueryService queryService) {
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
 * Implementa la operacion 'listar' del modulo calificacion en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param estudianteId identificador del estudiante dentro del dominio academico
     * @param claseId identificador de la clase ofertada por seccion y asignatura
     * @param numeroParcial numero de parcial academico permitido en fase 1 (1 o 2)
     * @param page indice de pagina solicitado (base cero) para paginacion de resultados
     * @param size tamano de pagina solicitado respetando limites del contrato API
     * @param sort criterios de ordenamiento solicitados por cliente segun whitelist permitida
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ApiResponse<PageResponseDto<CalificacionListItemDto>>> listar(
            @RequestParam(required = false) Long estudianteId,
            @RequestParam(required = false) Long claseId,
            @RequestParam(required = false) @Min(1) @Max(2) Integer numeroParcial,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(required = false) @Min(1) @Max(MAX_PAGE_SIZE) Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        Page<CalificacionListItemDto> result = queryService.listar(estudianteId, claseId, numeroParcial, page, size, sort);
        return ResponseEntity.ok(ResponseFactory.page("Listado de calificaciones obtenido correctamente.", result));
    }

    @Operation(summary = "Operacion.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @GetMapping("/{calificacionId}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
/**
 * Implementa la operacion 'obtenerPorId' del modulo calificacion en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param calificacionId identificador de la calificacion registrada por parcial
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiResponse<CalificacionResponseDto>> obtenerPorId(@PathVariable Long calificacionId) {
        return ResponseEntity.ok(ResponseFactory.ok("Calificacion obtenida correctamente.", queryService.obtenerPorId(calificacionId)));
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
 * Implementa la operacion 'crear' del modulo calificacion en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiResponse<CalificacionResponseDto>> crear(@Valid @RequestBody CalificacionCreateRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseFactory.created("Calificacion creada correctamente.", commandService.crear(request)));
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
 * Implementa la operacion 'actualizar' del modulo calificacion en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param calificacionId identificador de la calificacion registrada por parcial
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    @PutMapping("/{calificacionId}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ApiResponse<CalificacionResponseDto>> actualizar(
            @PathVariable Long calificacionId,
            @Valid @RequestBody CalificacionUpdateRequestDto request
    ) {
        return ResponseEntity.ok(ResponseFactory.ok("Calificacion actualizada correctamente.", commandService.actualizar(calificacionId, request)));
    }
}





