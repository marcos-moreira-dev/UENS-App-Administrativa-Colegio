package com.marcosmoreiradev.uensbackend.modules.reporte.api;

import com.marcosmoreiradev.uensbackend.common.api.response.ApiResponse;
import com.marcosmoreiradev.uensbackend.common.api.response.PageResponseDto;
import com.marcosmoreiradev.uensbackend.common.api.util.ResponseFactory;
import com.marcosmoreiradev.uensbackend.modules.reporte.api.dto.CrearReporteSolicitudRequestDto;
import com.marcosmoreiradev.uensbackend.modules.reporte.api.dto.ReporteSolicitudCreadaResponseDto;
import com.marcosmoreiradev.uensbackend.modules.reporte.api.dto.ReporteSolicitudDetalleResponseDto;
import com.marcosmoreiradev.uensbackend.modules.reporte.api.dto.ReporteSolicitudListItemDto;
import com.marcosmoreiradev.uensbackend.modules.reporte.api.dto.ReporteSolicitudResultadoResponseDto;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.ReporteSolicitudCommandService;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.ReporteSolicitudQueryService;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.model.ReporteArchivoDescarga;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ContentDisposition;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Controlador REST para gestionar solicitudes de reportes asincronos.
 */
@Validated
@Tag(name = "ReporteSolicitud", description = "Endpoints del modulo ReporteSolicitud.")
@RestController
@RequestMapping("/api/v1/reportes/solicitudes")
public class ReporteSolicitudController {

    private static final int MAX_PAGE_SIZE = 100;

    private final ReporteSolicitudCommandService commandService;
    private final ReporteSolicitudQueryService queryService;

    /**
     * Crea el controlador con los servicios de comando y consulta.
     *
     * @param commandService servicio para operaciones de escritura en cola de reportes
     * @param queryService servicio para consultas y descarga de resultados
     */
    public ReporteSolicitudController(
            ReporteSolicitudCommandService commandService,
            ReporteSolicitudQueryService queryService
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    /**
     * Crea una nueva solicitud de reporte en estado pendiente.
     *
     * @param request payload de creacion de solicitud
     * @return respuesta API con la solicitud creada
     */
    @Operation(summary = "Crear.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Operacion exitosa."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ApiResponse<ReporteSolicitudCreadaResponseDto>> crear(
            @Valid @RequestBody CrearReporteSolicitudRequestDto request
    ) {
        ReporteSolicitudCreadaResponseDto created = commandService.crearSolicitud(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseFactory.created("Solicitud de reporte creada correctamente.", created));
    }

    /**
     * Lista solicitudes de reporte con filtros, paginacion y orden.
     *
     * @param q filtro textual opcional
     * @param tipoReporte filtro opcional por tipo de reporte
     * @param estado filtro opcional por estado
     * @param page numero de pagina base cero
     * @param size tamano de pagina solicitado
     * @param sort lista de criterios de orden en formato {@code campo,direccion}
     * @return respuesta API paginada de solicitudes
     */
    @Operation(summary = "Listar.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ApiResponse<PageResponseDto<ReporteSolicitudListItemDto>>> listar(
            @RequestParam(required = false) @Size(max = 100) String q,
            @RequestParam(required = false) @Size(max = 100) String tipoReporte,
            @RequestParam(required = false) @Size(max = 20) String estado,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(required = false) @Min(1) @Max(MAX_PAGE_SIZE) Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        Page<ReporteSolicitudListItemDto> result = queryService.listar(q, tipoReporte, estado, page, size, sort);
        return ResponseEntity.ok(ResponseFactory.page("Listado de solicitudes de reporte obtenido correctamente.", result));
    }

    /**
     * Obtiene el detalle de una solicitud.
     *
     * @param solicitudId identificador de la solicitud
     * @return respuesta API con el detalle
     */
    @Operation(summary = "Operacion.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @GetMapping("/{solicitudId}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ApiResponse<ReporteSolicitudDetalleResponseDto>> obtenerDetalle(@PathVariable Long solicitudId) {
        return ResponseEntity.ok(ResponseFactory.ok(
                "Detalle de solicitud obtenido correctamente.",
                queryService.obtenerDetalle(solicitudId)
        ));
    }

    /**
     * Obtiene el estado actual de una solicitud.
     *
     * @param solicitudId identificador de la solicitud
     * @return respuesta API con estado y metadata
     */
    @Operation(summary = "Operacion.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @GetMapping("/{solicitudId}/estado")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ApiResponse<ReporteSolicitudResultadoResponseDto>> obtenerEstado(@PathVariable Long solicitudId) {
        return ResponseEntity.ok(ResponseFactory.ok(
                "Estado de solicitud obtenido correctamente.",
                queryService.obtenerEstado(solicitudId)
        ));
    }

    /**
     * Obtiene el resultado de una solicitud cuando ya fue completada.
     *
     * @param solicitudId identificador de la solicitud
     * @return respuesta API con payload de resultado
     */
    @Operation(summary = "Operacion.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @GetMapping("/{solicitudId}/resultado")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ApiResponse<ReporteSolicitudResultadoResponseDto>> obtenerResultado(@PathVariable Long solicitudId) {
        return ResponseEntity.ok(ResponseFactory.ok(
                "Resultado de solicitud obtenido correctamente.",
                queryService.obtenerResultado(solicitudId)
        ));
    }

    /**
     * Descarga el archivo generado para una solicitud completada.
     *
     * @param solicitudId identificador de la solicitud
     * @return respuesta binaria con el archivo en attachment
     */
    @Operation(summary = "Descargar archivo.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Archivo no encontrado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Resultado aun no disponible."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @GetMapping("/{solicitudId}/archivo")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<Resource> descargarArchivo(@PathVariable Long solicitudId) {
        ReporteArchivoDescarga archivo = queryService.obtenerArchivo(solicitudId);
        Resource resource = archivo.resource();
        MediaType mediaType = MediaType.parseMediaType(archivo.mimeType());

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(archivo.tamanoBytes())
                .cacheControl(CacheControl.noStore())
                .header("X-Content-Type-Options", "nosniff")
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(archivo.nombreArchivo(), StandardCharsets.UTF_8)
                        .build()
                        .toString())
                .body(resource);
    }

    /**
     * Reencola una solicitud fallida para intentar su procesamiento nuevamente.
     *
     * @param solicitudId identificador de la solicitud a reintentar
     * @return respuesta API con la solicitud reencolada
     */
    @Operation(summary = "Operacion.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Operacion exitosa."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @PostMapping("/{solicitudId}/reintentar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReporteSolicitudCreadaResponseDto>> reintentar(@PathVariable Long solicitudId) {
        return ResponseEntity.ok(ResponseFactory.ok(
                "Solicitud reencolada correctamente.",
                commandService.reintentar(solicitudId)
        ));
    }
}
