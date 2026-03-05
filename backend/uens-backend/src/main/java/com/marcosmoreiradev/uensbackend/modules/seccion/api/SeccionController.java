package com.marcosmoreiradev.uensbackend.modules.seccion.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.marcosmoreiradev.uensbackend.common.api.response.ApiResponse;
import com.marcosmoreiradev.uensbackend.common.api.response.PageResponseDto;
import com.marcosmoreiradev.uensbackend.common.api.util.ResponseFactory;
import com.marcosmoreiradev.uensbackend.modules.seccion.api.dto.SeccionCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.seccion.api.dto.SeccionListItemDto;
import com.marcosmoreiradev.uensbackend.modules.seccion.api.dto.SeccionPatchEstadoRequestDto;
import com.marcosmoreiradev.uensbackend.modules.seccion.api.dto.SeccionResponseDto;
import com.marcosmoreiradev.uensbackend.modules.seccion.api.dto.SeccionUpdateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.seccion.application.SeccionCommandService;
import com.marcosmoreiradev.uensbackend.modules.seccion.application.SeccionQueryService;
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
@Tag(name = "Seccion", description = "Endpoints del modulo Seccion.")
@RestController
@RequestMapping("/api/v1/secciones")
/**
 * Define la responsabilidad de SeccionController dentro del backend UENS.
 * Contexto: modulo seccion, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: exponer endpoints REST y delegar casos de uso sin filtrar reglas de negocio en capa HTTP.
 */
public class SeccionController {

    private static final int MAX_PAGE_SIZE = 100;

    private final SeccionCommandService commandService;
    private final SeccionQueryService queryService;
/**
 * Construye la instancia de SeccionController para operar en el modulo seccion.
 * Contexto: capa api con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param commandService servicio de comandos que aplica reglas de negocio de escritura
     * @param queryService servicio de consultas que entrega lecturas optimizadas para API
 */

    public SeccionController(SeccionCommandService commandService, SeccionQueryService queryService) {
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
 * Implementa la operacion 'listar' del modulo seccion en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param q filtro de busqueda textual para consultas por coincidencia parcial
     * @param estado estado operativo del recurso (ACTIVO/INACTIVO) para filtrar o actualizar
     * @param grado grado academico de Educacion General Basica para filtrar/validar datos
     * @param paralelo dato de entrada relevante para ejecutar esta operacion: 'paralelo'
     * @param anioLectivo dato de entrada relevante para ejecutar esta operacion: 'anioLectivo'
     * @param page indice de pagina solicitado (base cero) para paginacion de resultados
     * @param size tamano de pagina solicitado respetando limites del contrato API
     * @param sort criterios de ordenamiento solicitados por cliente segun whitelist permitida
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ApiResponse<PageResponseDto<SeccionListItemDto>>> listar(
            @RequestParam(required = false) @Size(max = 100) String q,
            @RequestParam(required = false) @Size(max = 20) String estado,
            @RequestParam(required = false) @Min(1) @Max(7) Integer grado,
            @RequestParam(required = false) @Size(max = 10) String paralelo,
            @RequestParam(required = false) @Size(max = 20) String anioLectivo,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(required = false) @Min(1) @Max(MAX_PAGE_SIZE) Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        Page<SeccionListItemDto> result = queryService.listar(
                q, estado, grado, paralelo, anioLectivo, page, size, sort
        );
        return ResponseEntity.ok(ResponseFactory.page("Listado de secciones obtenido correctamente.", result));
    }

    @Operation(summary = "Operacion.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @GetMapping("/{seccionId}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
/**
 * Implementa la operacion 'obtenerPorId' del modulo seccion en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param seccionId identificador de la seccion academica (grado/paralelo/anio lectivo)
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiResponse<SeccionResponseDto>> obtenerPorId(@PathVariable Long seccionId) {
        return ResponseEntity.ok(ResponseFactory.ok("Seccion obtenida correctamente.", queryService.obtenerPorId(seccionId)));
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
    @PreAuthorize("hasRole('ADMIN')")
/**
 * Implementa la operacion 'crear' del modulo seccion en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiResponse<SeccionResponseDto>> crear(@Valid @RequestBody SeccionCreateRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseFactory.created("Seccion creada correctamente.", commandService.crear(request)));
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
 * Implementa la operacion 'actualizar' del modulo seccion en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param seccionId identificador de la seccion academica (grado/paralelo/anio lectivo)
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    @PutMapping("/{seccionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SeccionResponseDto>> actualizar(
            @PathVariable Long seccionId,
            @Valid @RequestBody SeccionUpdateRequestDto request
    ) {
        return ResponseEntity.ok(ResponseFactory.ok("Seccion actualizada correctamente.", commandService.actualizar(seccionId, request)));
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
 * Implementa la operacion 'cambiarEstado' del modulo seccion en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param seccionId identificador de la seccion academica (grado/paralelo/anio lectivo)
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    @PatchMapping("/{seccionId}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SeccionResponseDto>> cambiarEstado(
            @PathVariable Long seccionId,
            @Valid @RequestBody SeccionPatchEstadoRequestDto request
    ) {
        return ResponseEntity.ok(ResponseFactory.ok("Estado de seccion actualizado correctamente.", commandService.cambiarEstado(seccionId, request)));
    }
}





