package com.marcosmoreiradev.uensbackend.modules.usuario.api;

import com.marcosmoreiradev.uensbackend.common.api.response.ApiResponse;
import com.marcosmoreiradev.uensbackend.common.api.response.PageResponseDto;
import com.marcosmoreiradev.uensbackend.common.api.util.ResponseFactory;
import com.marcosmoreiradev.uensbackend.modules.usuario.api.dto.UsuarioSistemaAdministrativoCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.usuario.api.dto.UsuarioSistemaAdministrativoListItemDto;
import com.marcosmoreiradev.uensbackend.modules.usuario.api.dto.UsuarioSistemaAdministrativoPatchEstadoRequestDto;
import com.marcosmoreiradev.uensbackend.modules.usuario.api.dto.UsuarioSistemaAdministrativoResponseDto;
import com.marcosmoreiradev.uensbackend.modules.usuario.api.dto.UsuarioSistemaAdministrativoUpdateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.usuario.application.UsuarioCommandService;
import com.marcosmoreiradev.uensbackend.modules.usuario.application.UsuarioQueryService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Expone el CRUD administrativo del modulo de usuarios del sistema.
 */
@Validated
@Tag(name = "Usuario Sistema", description = "Endpoints del modulo Usuario del sistema administrativo.")
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioSistemaAdministrativoController {

    private static final int MAX_PAGE_SIZE = 100;

    private final UsuarioCommandService commandService;
    private final UsuarioQueryService queryService;

    public UsuarioSistemaAdministrativoController(
            UsuarioCommandService commandService,
            UsuarioQueryService queryService
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @Operation(summary = "Listar usuarios del sistema.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponseDto<UsuarioSistemaAdministrativoListItemDto>>> listar(
            @RequestParam(required = false) @Size(max = 120) String q,
            @RequestParam(required = false) @Size(max = 10) String estado,
            @RequestParam(required = false) @Size(max = 20) String rol,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(required = false) @Min(1) @Max(MAX_PAGE_SIZE) Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        Page<UsuarioSistemaAdministrativoListItemDto> result = queryService.listar(q, estado, rol, page, size, sort);
        return ResponseEntity.ok(ResponseFactory.page("Listado de usuarios obtenido correctamente.", result));
    }

    @Operation(summary = "Obtener usuario del sistema por id.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @GetMapping("/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UsuarioSistemaAdministrativoResponseDto>> obtenerPorId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ResponseFactory.ok(
                "Usuario del sistema obtenido correctamente.",
                queryService.obtenerPorId(usuarioId)
        ));
    }

    @Operation(summary = "Registrar usuario del sistema.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Operacion exitosa."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UsuarioSistemaAdministrativoResponseDto>> crear(
            @Valid @RequestBody UsuarioSistemaAdministrativoCreateRequestDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseFactory.created(
                        "Usuario del sistema creado correctamente.",
                        commandService.crear(request)
                ));
    }

    @Operation(summary = "Actualizar usuario del sistema.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @PutMapping("/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UsuarioSistemaAdministrativoResponseDto>> actualizar(
            @PathVariable Long usuarioId,
            @Valid @RequestBody UsuarioSistemaAdministrativoUpdateRequestDto request
    ) {
        return ResponseEntity.ok(ResponseFactory.ok(
                "Usuario del sistema actualizado correctamente.",
                commandService.actualizar(usuarioId, request)
        ));
    }

    @Operation(summary = "Cambiar estado de usuario del sistema.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @PatchMapping("/{usuarioId}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UsuarioSistemaAdministrativoResponseDto>> cambiarEstado(
            @PathVariable Long usuarioId,
            @Valid @RequestBody UsuarioSistemaAdministrativoPatchEstadoRequestDto request
    ) {
        return ResponseEntity.ok(ResponseFactory.ok(
                "Estado de usuario actualizado correctamente.",
                commandService.cambiarEstado(usuarioId, request)
        ));
    }
}
