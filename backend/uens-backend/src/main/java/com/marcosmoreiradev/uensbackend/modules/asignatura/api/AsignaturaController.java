package com.marcosmoreiradev.uensbackend.modules.asignatura.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.marcosmoreiradev.uensbackend.common.api.response.ApiResponse;
import com.marcosmoreiradev.uensbackend.common.api.response.PageResponseDto;
import com.marcosmoreiradev.uensbackend.common.api.util.ResponseFactory;
import com.marcosmoreiradev.uensbackend.modules.asignatura.api.dto.AsignaturaCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.asignatura.api.dto.AsignaturaListItemDto;
import com.marcosmoreiradev.uensbackend.modules.asignatura.api.dto.AsignaturaPatchEstadoRequestDto;
import com.marcosmoreiradev.uensbackend.modules.asignatura.api.dto.AsignaturaResponseDto;
import com.marcosmoreiradev.uensbackend.modules.asignatura.api.dto.AsignaturaUpdateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.asignatura.application.AsignaturaCommandService;
import com.marcosmoreiradev.uensbackend.modules.asignatura.application.AsignaturaQueryService;
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
@Tag(name = "Asignatura", description = "Endpoints del modulo Asignatura.")
@RestController
@RequestMapping("/api/v1/asignaturas")
/**
 * Define la responsabilidad de AsignaturaController dentro del backend UENS.
 * Contexto: modulo asignatura, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: exponer endpoints REST y delegar casos de uso sin filtrar reglas de negocio en capa HTTP.
 */
public class AsignaturaController {

    private static final int MAX_PAGE_SIZE = 100;

    private final AsignaturaCommandService commandService;
    private final AsignaturaQueryService queryService;
/**
 * Construye la instancia de AsignaturaController para operar en el modulo asignatura.
 * Contexto: capa api con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param commandService servicio de comandos que aplica reglas de negocio de escritura
     * @param queryService servicio de consultas que entrega lecturas optimizadas para API
 */

    public AsignaturaController(
            AsignaturaCommandService commandService,
            AsignaturaQueryService queryService
    ) {
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
 * Implementa la operacion 'listar' del modulo asignatura en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param page indice de pagina solicitado (base cero) para paginacion de resultados
     * @param size tamano de pagina solicitado respetando limites del contrato API
     * @param sort criterios de ordenamiento solicitados por cliente segun whitelist permitida
     * @param q filtro de busqueda textual para consultas por coincidencia parcial
     * @param estado estado operativo del recurso (ACTIVO/INACTIVO) para filtrar o actualizar
     * @param grado grado academico de Educacion General Basica para filtrar/validar datos
     * @param area area academica de la asignatura (por ejemplo Lenguaje o Ciencias)
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ApiResponse<PageResponseDto<AsignaturaListItemDto>>> listar(
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(required = false) @Min(1) @Max(MAX_PAGE_SIZE) Integer size,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) @Size(max = 100) String q,
            @RequestParam(required = false) @Size(max = 20) String estado,
            @RequestParam(required = false) @Min(1) @Max(7) Integer grado,
            @RequestParam(required = false) @Size(max = 60) String area
    ) {
        Page<AsignaturaListItemDto> result = queryService.listar(q, estado, grado, area, page, size, sort);
        return ResponseEntity.ok(ResponseFactory.page("Listado de asignaturas obtenido correctamente.", result));
    }

    @Operation(summary = "Operacion.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @GetMapping("/{asignaturaId}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
/**
 * Implementa la operacion 'obtenerPorId' del modulo asignatura en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param asignaturaId identificador de la asignatura dentro del catalogo academico
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiResponse<AsignaturaResponseDto>> obtenerPorId(@PathVariable Long asignaturaId) {
        AsignaturaResponseDto dto = queryService.obtenerPorId(asignaturaId);
        return ResponseEntity.ok(ResponseFactory.ok("Asignatura obtenida correctamente.", dto));
    }

    @Operation(summary = "Crear.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Operacion exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
/**
 * Implementa la operacion 'crear' del modulo asignatura en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AsignaturaResponseDto>> crear(
            @Valid @RequestBody AsignaturaCreateRequestDto request
    ) {
        AsignaturaResponseDto created = commandService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseFactory.created("Asignatura creada correctamente.", created));
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
 * Implementa la operacion 'actualizar' del modulo asignatura en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param asignaturaId identificador de la asignatura dentro del catalogo academico
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    @PutMapping("/{asignaturaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AsignaturaResponseDto>> actualizar(
            @PathVariable Long asignaturaId,
            @Valid @RequestBody AsignaturaUpdateRequestDto request
    ) {
        AsignaturaResponseDto updated = commandService.actualizar(asignaturaId, request);
        return ResponseEntity.ok(ResponseFactory.ok("Asignatura actualizada correctamente.", updated));
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
 * Implementa la operacion 'cambiarEstado' del modulo asignatura en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param asignaturaId identificador de la asignatura dentro del catalogo academico
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    @PatchMapping("/{asignaturaId}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AsignaturaResponseDto>> cambiarEstado(
            @PathVariable Long asignaturaId,
            @Valid @RequestBody AsignaturaPatchEstadoRequestDto request
    ) {
        AsignaturaResponseDto updated = commandService.cambiarEstado(asignaturaId, request);
        return ResponseEntity.ok(ResponseFactory.ok("Estado de asignatura actualizado correctamente.", updated));
    }
}





