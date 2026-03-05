package com.marcosmoreiradev.uensbackend.modules.system;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.marcosmoreiradev.uensbackend.common.api.response.ApiResponse;
import com.marcosmoreiradev.uensbackend.common.api.util.ResponseFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Ping", description = "Endpoints del modulo Ping.")
@RestController
/**
 * Define la responsabilidad de PingController dentro del backend UENS.
 * Contexto: modulo system, capa core, arquitectura monolito modular Spring Boot.
 * Alcance: exponer endpoints REST y delegar casos de uso sin filtrar reglas de negocio en capa HTTP.
 */
public class PingController {

    @Operation(summary = "Operacion.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @GetMapping("/api/v1/system/ping")
/**
 * Implementa la operacion 'ping' del modulo system en la capa core.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiResponse<String>> ping() {
        return ResponseEntity.ok(ResponseFactory.ok("Ping OK.", "ok"));
    }
}





