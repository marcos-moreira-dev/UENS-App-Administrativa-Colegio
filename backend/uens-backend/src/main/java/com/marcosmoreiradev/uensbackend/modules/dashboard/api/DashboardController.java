package com.marcosmoreiradev.uensbackend.modules.dashboard.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.marcosmoreiradev.uensbackend.common.api.response.ApiResponse;
import com.marcosmoreiradev.uensbackend.common.api.util.ResponseFactory;
import com.marcosmoreiradev.uensbackend.modules.dashboard.api.dto.DashboardResumenResponseDto;
import com.marcosmoreiradev.uensbackend.modules.dashboard.application.DashboardQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboard", description = "Endpoints del modulo Dashboard.")
@RestController
@RequestMapping("/api/v1/dashboard")
/**
 * Define la responsabilidad de DashboardController dentro del backend UENS.
 * Contexto: modulo dashboard, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: exponer endpoints REST y delegar casos de uso sin filtrar reglas de negocio en capa HTTP.
 */
public class DashboardController {

    private final DashboardQueryService queryService;
/**
 * Construye la instancia de DashboardController para operar en el modulo dashboard.
 * Contexto: capa api con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param queryService servicio de consultas que entrega lecturas optimizadas para API
 */

    public DashboardController(DashboardQueryService queryService) {
        this.queryService = queryService;
    }

    @Operation(summary = "Operacion.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @GetMapping("/resumen")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
/**
 * Implementa la operacion 'obtenerResumen' del modulo dashboard en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiResponse<DashboardResumenResponseDto>> obtenerResumen() {
        return ResponseEntity.ok(ResponseFactory.ok("Resumen del dashboard obtenido correctamente.", queryService.obtenerResumen()));
    }
}





