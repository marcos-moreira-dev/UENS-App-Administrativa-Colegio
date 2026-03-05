package com.marcosmoreiradev.uensbackend.modules.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.marcosmoreiradev.uensbackend.common.api.response.ApiResponse;
import com.marcosmoreiradev.uensbackend.common.api.util.ClientIpResolver;
import com.marcosmoreiradev.uensbackend.common.api.util.ResponseFactory;
import com.marcosmoreiradev.uensbackend.common.exception.base.AuthException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.AuthErrorCodes;
import com.marcosmoreiradev.uensbackend.modules.auth.api.dto.AuthUsuarioResumenDto;
import com.marcosmoreiradev.uensbackend.modules.auth.api.dto.LogoutRequestDto;
import com.marcosmoreiradev.uensbackend.modules.auth.api.dto.LoginRequestDto;
import com.marcosmoreiradev.uensbackend.modules.auth.api.dto.LoginResponseDto;
import com.marcosmoreiradev.uensbackend.modules.auth.api.dto.RefreshTokenRequestDto;
import com.marcosmoreiradev.uensbackend.modules.auth.application.AuthApplicationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Endpoints del modulo Auth.")
@RestController
@RequestMapping("/api/v1/auth")
/**
 * Define la responsabilidad de AuthController dentro del backend UENS.
 * Contexto: modulo auth, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: exponer endpoints REST y delegar casos de uso sin filtrar reglas de negocio en capa HTTP.
 */
public class AuthController {

    private final AuthApplicationService authApplicationService;
    private final ClientIpResolver clientIpResolver;
/**
 * Construye la instancia de AuthController para operar en el modulo auth.
 * Contexto: capa api con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param authApplicationService dato de entrada relevante para ejecutar esta operacion: 'authApplicationService'
     * @param clientIpResolver dato de entrada relevante para ejecutar esta operacion: 'clientIpResolver'
 */

    public AuthController(AuthApplicationService authApplicationService, ClientIpResolver clientIpResolver) {
        this.authApplicationService = authApplicationService;
        this.clientIpResolver = clientIpResolver;
    }

    @Operation(summary = "Operacion.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Operacion exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @PostMapping("/login")
/**
 * Implementa la operacion 'login' del modulo auth en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @param httpRequest request HTTP usado para derivar IP remota y trazabilidad
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @Valid @RequestBody LoginRequestDto request,
            HttpServletRequest httpRequest
    ) {
        LoginResponseDto data = authApplicationService.login(request, clientIpResolver.resolve(httpRequest));
        return ResponseEntity.ok(ResponseFactory.ok("Inicio de sesion exitoso.", data));
    }

    @Operation(summary = "Renovar access token.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh token invalido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponseDto>> refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        LoginResponseDto data = authApplicationService.refresh(request.refreshToken());
        return ResponseEntity.ok(ResponseFactory.ok("Sesion renovada correctamente.", data));
    }

    @Operation(summary = "Cerrar sesion renovable.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody(required = false) LogoutRequestDto request) {
        authApplicationService.logout(request == null ? null : request.refreshToken());
        return ResponseEntity.ok(ResponseFactory.okMessage("Sesion cerrada correctamente."));
    }

    @Operation(summary = "Operacion.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud invalida."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno.")
    })
    @GetMapping("/me")
/**
 * Implementa la operacion 'me' del modulo auth en la capa api.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param authentication contexto de autenticacion actual evaluado por seguridad
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiResponse<AuthUsuarioResumenDto>> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthException(
                    AuthErrorCodes.AUTH_02_TOKEN_INVALIDO,
                    AuthErrorCodes.AUTH_02_TOKEN_INVALIDO.defaultMessage()
            );
        }

        String login = authentication.getName();
        AuthUsuarioResumenDto data = authApplicationService.me(login);
        return ResponseEntity.ok(ResponseFactory.ok(data));
    }
}



