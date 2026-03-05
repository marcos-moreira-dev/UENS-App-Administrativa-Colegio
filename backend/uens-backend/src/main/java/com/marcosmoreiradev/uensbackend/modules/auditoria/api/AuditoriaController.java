package com.marcosmoreiradev.uensbackend.modules.auditoria.api;

import com.marcosmoreiradev.uensbackend.common.api.response.ApiResponse;
import com.marcosmoreiradev.uensbackend.common.api.response.PageResponseDto;
import com.marcosmoreiradev.uensbackend.common.api.util.ResponseFactory;
import com.marcosmoreiradev.uensbackend.modules.auditoria.api.dto.AuditoriaEventoListItemDto;
import com.marcosmoreiradev.uensbackend.modules.auditoria.api.dto.CrearAuditoriaReporteRequestDto;
import com.marcosmoreiradev.uensbackend.modules.auditoria.application.AuditoriaQueryService;
import com.marcosmoreiradev.uensbackend.modules.auditoria.application.AuditoriaReporteService;
import com.marcosmoreiradev.uensbackend.modules.reporte.api.dto.ReporteSolicitudCreadaResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Validated
@Tag(name = "Auditoria", description = "Endpoints de auditoria operativa y reporte administrativo.")
@RestController
@RequestMapping("/api/v1/auditoria")
public class AuditoriaController {

    private static final int MAX_PAGE_SIZE = 100;

    private final AuditoriaQueryService queryService;
    private final AuditoriaReporteService reporteService;

    public AuditoriaController(
            AuditoriaQueryService queryService,
            AuditoriaReporteService reporteService
    ) {
        this.queryService = queryService;
        this.reporteService = reporteService;
    }

    @Operation(summary = "Listar eventos de auditoria (solo ADMIN).")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @GetMapping("/eventos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponseDto<AuditoriaEventoListItemDto>>> listarEventos(
            @RequestParam(required = false) @Size(max = 120) String q,
            @RequestParam(required = false) @Size(max = 80) String modulo,
            @RequestParam(required = false) @Size(max = 120) String accion,
            @RequestParam(required = false) @Size(max = 20) String resultado,
            @RequestParam(required = false) @Size(max = 80) String actorLogin,
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(required = false) @Min(1) @Max(MAX_PAGE_SIZE) Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        Page<AuditoriaEventoListItemDto> result = queryService.listar(
                q,
                modulo,
                accion,
                resultado,
                actorLogin,
                fechaDesde,
                fechaHasta,
                page,
                size,
                sort
        );
        return ResponseEntity.ok(ResponseFactory.page("Listado de eventos de auditoria obtenido correctamente.", result));
    }

    @Operation(summary = "Solicitar reporte asincrono de auditoria (solo ADMIN).")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Operacion exitosa."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @PostMapping("/reportes/solicitudes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReporteSolicitudCreadaResponseDto>> solicitarReporteAuditoria(
            @Valid @RequestBody CrearAuditoriaReporteRequestDto request
    ) {
        ReporteSolicitudCreadaResponseDto created = reporteService.solicitarReporte(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseFactory.created("Solicitud de reporte de auditoria creada correctamente.", created));
    }
}

