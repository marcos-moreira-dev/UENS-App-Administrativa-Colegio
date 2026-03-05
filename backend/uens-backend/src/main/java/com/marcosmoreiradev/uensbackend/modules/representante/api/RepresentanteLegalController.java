package com.marcosmoreiradev.uensbackend.modules.representante.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.marcosmoreiradev.uensbackend.common.api.response.ApiResponse;
import com.marcosmoreiradev.uensbackend.common.api.response.PageResponseDto;
import com.marcosmoreiradev.uensbackend.common.api.util.ResponseFactory;
import com.marcosmoreiradev.uensbackend.modules.representante.api.dto.RepresentanteLegalCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.representante.api.dto.RepresentanteLegalListItemDto;
import com.marcosmoreiradev.uensbackend.modules.representante.api.dto.RepresentanteLegalResponseDto;
import com.marcosmoreiradev.uensbackend.modules.representante.api.dto.RepresentanteLegalUpdateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.representante.application.RepresentanteLegalCommandService;
import com.marcosmoreiradev.uensbackend.modules.representante.application.RepresentanteLegalQueryService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@Tag(name = "RepresentanteLegal", description = "Endpoints del modulo RepresentanteLegal.")
@RestController
@RequestMapping("/api/v1/representantes")
/**
 * Define la responsabilidad de RepresentanteLegalController dentro del backend UENS.
 * Contexto: modulo representante, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: exponer endpoints REST y delegar casos de uso sin filtrar reglas de negocio en capa HTTP.
 */
public class RepresentanteLegalController {

    private final RepresentanteLegalCommandService commandService;
    private final RepresentanteLegalQueryService queryService;
/**
 * Construye la instancia de RepresentanteLegalController para operar en el modulo representante.
 * Contexto: capa api con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param commandService servicio de comandos que aplica reglas de negocio de escritura
     * @param queryService servicio de consultas que entrega lecturas optimizadas para API
 */

    public RepresentanteLegalController(RepresentanteLegalCommandService commandService, RepresentanteLegalQueryService queryService) {
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
 * Implementa la operacion 'listar' del modulo representante en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param q filtro de busqueda textual para consultas por coincidencia parcial
     * @param page indice de pagina solicitado (base cero) para paginacion de resultados
     * @param size tamano de pagina solicitado respetando limites del contrato API
     * @param sort criterios de ordenamiento solicitados por cliente segun whitelist permitida
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ApiResponse<PageResponseDto<RepresentanteLegalListItemDto>>> listar(
            @RequestParam(required = false) @Size(max = 120) String q,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(required = false) @Min(1) @Max(100) Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        Page<RepresentanteLegalListItemDto> result = queryService.listar(q, page, size, sort);
        return ResponseEntity.ok(ResponseFactory.page("Listado de representantes obtenido correctamente.", result));
    }

    @Operation(summary = "Operacion.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @GetMapping("/{representanteId}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
/**
 * Implementa la operacion 'obtenerPorId' del modulo representante en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param representanteId identificador del representante legal asociado al estudiante
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiResponse<RepresentanteLegalResponseDto>> obtenerPorId(@PathVariable Long representanteId) {
        return ResponseEntity.ok(ResponseFactory.ok("Representante legal obtenido correctamente.", queryService.obtenerPorId(representanteId)));
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
 * Implementa la operacion 'crear' del modulo representante en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiResponse<RepresentanteLegalResponseDto>> crear(@Valid @RequestBody RepresentanteLegalCreateRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseFactory.created("Representante legal creado correctamente.", commandService.crear(request)));
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
 * Implementa la operacion 'actualizar' del modulo representante en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param representanteId identificador del representante legal asociado al estudiante
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    @PutMapping("/{representanteId}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ApiResponse<RepresentanteLegalResponseDto>> actualizar(
            @PathVariable Long representanteId,
            @Valid @RequestBody RepresentanteLegalUpdateRequestDto request
    ) {
        return ResponseEntity.ok(ResponseFactory.ok("Representante legal actualizado correctamente.", commandService.actualizar(representanteId, request)));
    }
}





