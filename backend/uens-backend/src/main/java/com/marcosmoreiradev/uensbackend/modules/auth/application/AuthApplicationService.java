package com.marcosmoreiradev.uensbackend.modules.auth.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.AuthException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.AuthErrorCodes;
import com.marcosmoreiradev.uensbackend.config.properties.JwtProperties;
import com.marcosmoreiradev.uensbackend.modules.auth.api.dto.AuthUsuarioResumenDto;
import com.marcosmoreiradev.uensbackend.modules.auth.api.dto.LoginRequestDto;
import com.marcosmoreiradev.uensbackend.modules.auth.api.dto.LoginResponseDto;
import com.marcosmoreiradev.uensbackend.modules.auth.application.mapper.AuthDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.auth.application.support.LoginProtectionService;
import com.marcosmoreiradev.uensbackend.modules.auth.application.support.RefreshTokenService;
import com.marcosmoreiradev.uensbackend.modules.auth.application.model.IssuedRefreshToken;
import com.marcosmoreiradev.uensbackend.modules.auth.application.model.RefreshTokenSubject;
import com.marcosmoreiradev.uensbackend.modules.usuario.application.port.UsuarioPasswordService;
import com.marcosmoreiradev.uensbackend.modules.usuario.infrastructure.persistence.entity.UsuarioSistemaAdministrativoJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.usuario.infrastructure.persistence.repository.UsuarioSistemaAdministrativoJpaRepository;
import com.marcosmoreiradev.uensbackend.security.jwt.JwtTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 * Define la responsabilidad de AuthApplicationService dentro del backend UENS.
 * Contexto: modulo auth, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: orquestar comportamiento de aplicacion entre dominio, persistencia y seguridad.
 */
public class AuthApplicationService {

    private static final String ESTADO_ACTIVO = "ACTIVO";

    private final UsuarioSistemaAdministrativoJpaRepository usuarioRepository;
    private final AuthDtoMapper authDtoMapper;
    private final JwtTokenService jwtTokenService;
    private final UsuarioPasswordService usuarioPasswordService;
    private final JwtProperties jwtProperties;
    private final LoginProtectionService loginProtectionService;
    private final RefreshTokenService refreshTokenService;
/**
 * Construye la instancia de AuthApplicationService para operar en el modulo auth.
 * Contexto: capa application con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param usuarioRepository dato de entrada relevante para ejecutar esta operacion: 'usuarioRepository'
     * @param authDtoMapper dato de entrada relevante para ejecutar esta operacion: 'authDtoMapper'
     * @param jwtTokenService dato de entrada relevante para ejecutar esta operacion: 'jwtTokenService'
     * @param usuarioPasswordService dato de entrada relevante para ejecutar esta operacion: 'usuarioPasswordService'
     * @param jwtProperties dato de entrada relevante para ejecutar esta operacion: 'jwtProperties'
     * @param loginProtectionService dato de entrada relevante para ejecutar esta operacion: 'loginProtectionService'
     * @param refreshTokenService dato de entrada relevante para ejecutar esta operacion: 'refreshTokenService'
 */

    public AuthApplicationService(
            UsuarioSistemaAdministrativoJpaRepository usuarioRepository,
            AuthDtoMapper authDtoMapper,
            JwtTokenService jwtTokenService,
            UsuarioPasswordService usuarioPasswordService,
            JwtProperties jwtProperties,
            LoginProtectionService loginProtectionService,
            RefreshTokenService refreshTokenService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.authDtoMapper = authDtoMapper;
        this.jwtTokenService = jwtTokenService;
        this.usuarioPasswordService = usuarioPasswordService;
        this.jwtProperties = jwtProperties;
        this.loginProtectionService = loginProtectionService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional(readOnly = true)
/**
 * Implementa la operacion 'login' del modulo auth en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @param clientIp IP remota usada para rate limiting y lockout temporal
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public LoginResponseDto login(LoginRequestDto request, String clientIp) {
        String login = normalize(request.getLogin());
        String rawPassword = request.getPassword();
        loginProtectionService.assertLoginAllowed(login, clientIp);

        try {
            UsuarioSistemaAdministrativoJpaEntity usuario = usuarioRepository.findByNombreLoginIgnoreCase(login)
                    .orElseThrow(AuthApplicationService::invalidCredentials);

            if (!usuarioPasswordService.matches(rawPassword, usuario.getPasswordHash())) {
                throw invalidCredentials();
            }

            ensureActive(usuario);
            loginProtectionService.registerSuccess(login, clientIp);

            return buildLoginResponse(usuario);
        } catch (AuthException ex) {
            if (AuthErrorCodes.AUTH_01_CREDENCIALES_INVALIDAS.equals(ex.getErrorCode())) {
                loginProtectionService.registerFailure(login, clientIp);
            }
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    /**
     * Renueva la sesion emitiendo un nuevo access token y rotando el refresh
     * token vigente.
     *
     * @param refreshToken token opaco entregado previamente al cliente
     * @return nueva pareja de tokens con metadata del usuario
     */
    public LoginResponseDto refresh(String refreshToken) {
        IssuedRefreshToken issuedRefreshToken = refreshTokenService.rotate(refreshToken);
        UsuarioSistemaAdministrativoJpaEntity usuario = usuarioRepository.findByNombreLoginIgnoreCase(issuedRefreshToken.subject().login())
                .orElseThrow(() -> new AuthException(
                        AuthErrorCodes.AUTH_08_REFRESH_TOKEN_INVALIDO,
                        AuthErrorCodes.AUTH_08_REFRESH_TOKEN_INVALIDO.defaultMessage()
                ));
        ensureActive(usuario);

        return buildLoginResponse(usuario, issuedRefreshToken);
    }

    /**
     * Revoca el refresh token actual para cerrar la sesion renovable del
     * cliente. El logout local del desktop sigue siendo responsabilidad del UI.
     *
     * @param refreshToken token opaco actual del cliente
     */
    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    @Transactional(readOnly = true)
/**
 * Implementa la operacion 'me' del modulo auth en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param loginFromToken dato de entrada relevante para ejecutar esta operacion: 'loginFromToken'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public AuthUsuarioResumenDto me(String loginFromToken) {
        String login = normalize(loginFromToken);
        UsuarioSistemaAdministrativoJpaEntity usuario = usuarioRepository.findByNombreLoginIgnoreCase(login)
                .orElseThrow(() -> new AuthException(
                        AuthErrorCodes.AUTH_02_TOKEN_INVALIDO,
                        AuthErrorCodes.AUTH_02_TOKEN_INVALIDO.defaultMessage()
                ));

        ensureActive(usuario);
        return authDtoMapper.toUsuarioResumenDto(
                usuario.getId(),
                usuario.getNombreLogin(),
                usuario.getRol(),
                usuario.getEstado()
        );
    }

/**
 * Metodo de soporte interno 'ensureActive' para mantener cohesion en AuthApplicationService.
 * Contexto: modulo auth, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param usuario dato de entrada relevante para ejecutar esta operacion: 'usuario'
 */
    private void ensureActive(UsuarioSistemaAdministrativoJpaEntity usuario) {
        if (!ESTADO_ACTIVO.equalsIgnoreCase(usuario.getEstado())) {
            throw new AuthException(
                    AuthErrorCodes.AUTH_05_USUARIO_INACTIVO,
                    AuthErrorCodes.AUTH_05_USUARIO_INACTIVO.defaultMessage()
            );
        }
    }

/**
 * Metodo de soporte interno 'invalidCredentials' para mantener cohesion en AuthApplicationService.
 * Contexto: modulo auth, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static AuthException invalidCredentials() {
        return new AuthException(
                AuthErrorCodes.AUTH_01_CREDENCIALES_INVALIDAS,
                AuthErrorCodes.AUTH_01_CREDENCIALES_INVALIDAS.defaultMessage()
        );
    }

    private LoginResponseDto buildLoginResponse(UsuarioSistemaAdministrativoJpaEntity usuario) {
        return buildLoginResponse(
                usuario,
                refreshTokenService.issue(new RefreshTokenSubject(
                        usuario.getId(),
                        usuario.getNombreLogin(),
                        usuario.getRol()
                ))
        );
    }

    private LoginResponseDto buildLoginResponse(
            UsuarioSistemaAdministrativoJpaEntity usuario,
            IssuedRefreshToken issuedRefreshToken
    ) {
        String token = jwtTokenService.generateToken(usuario.getNombreLogin(), usuario.getRol());
        return authDtoMapper.toLoginResponseDto(
                token,
                issuedRefreshToken.rawToken(),
                jwtProperties.expirationSeconds(),
                issuedRefreshToken.expiresInSeconds(),
                usuario.getId(),
                usuario.getNombreLogin(),
                usuario.getRol(),
                usuario.getEstado()
        );
    }

/**
 * Metodo de soporte interno 'normalize' para mantener cohesion en AuthApplicationService.
 * Contexto: modulo auth, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param value valor de entrada que debe validarse o transformarse en este metodo auxiliar
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
