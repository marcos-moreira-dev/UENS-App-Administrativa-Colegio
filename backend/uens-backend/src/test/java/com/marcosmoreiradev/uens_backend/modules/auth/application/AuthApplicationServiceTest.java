package com.marcosmoreiradev.uens_backend.modules.auth.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.AuthException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.AuthErrorCodes;
import com.marcosmoreiradev.uensbackend.config.properties.JwtProperties;
import com.marcosmoreiradev.uensbackend.modules.auth.api.dto.LoginRequestDto;
import com.marcosmoreiradev.uensbackend.modules.auth.api.dto.LoginResponseDto;
import com.marcosmoreiradev.uensbackend.modules.auth.application.AuthApplicationService;
import com.marcosmoreiradev.uensbackend.modules.auth.application.mapper.AuthDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.auth.application.model.IssuedRefreshToken;
import com.marcosmoreiradev.uensbackend.modules.auth.application.model.RefreshTokenSubject;
import com.marcosmoreiradev.uensbackend.modules.auth.application.support.LoginProtectionService;
import com.marcosmoreiradev.uensbackend.modules.auth.application.support.RefreshTokenService;
import com.marcosmoreiradev.uensbackend.modules.usuario.application.port.UsuarioPasswordService;
import com.marcosmoreiradev.uensbackend.modules.usuario.infrastructure.persistence.entity.UsuarioSistemaAdministrativoJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.usuario.infrastructure.persistence.repository.UsuarioSistemaAdministrativoJpaRepository;
import com.marcosmoreiradev.uensbackend.security.jwt.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthApplicationServiceTest {

    @Mock
    private UsuarioSistemaAdministrativoJpaRepository usuarioRepository;
    @Mock
    private JwtTokenService jwtTokenService;
    @Mock
    private UsuarioPasswordService usuarioPasswordService;
    @Mock
    private LoginProtectionService loginProtectionService;
    @Mock
    private RefreshTokenService refreshTokenService;

    private AuthApplicationService service;

    @BeforeEach
    void setUp() {
        service = new AuthApplicationService(
                usuarioRepository,
                new AuthDtoMapper(),
                jwtTokenService,
                usuarioPasswordService,
                new JwtProperties("secret", 3600L, "uens-backend"),
                loginProtectionService,
                refreshTokenService
        );
    }

    @Test
    void loginRegistersFailureWhenCredentialsAreInvalid() {
        when(usuarioRepository.findByNombreLoginIgnoreCase("admin")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(new LoginRequestDto("admin", "secreto"), "127.0.0.1"))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCodes.AUTH_01_CREDENCIALES_INVALIDAS);

        verify(loginProtectionService).assertLoginAllowed("admin", "127.0.0.1");
        verify(loginProtectionService).registerFailure("admin", "127.0.0.1");
        verify(loginProtectionService, never()).registerSuccess(anyString(), anyString());
    }

    @Test
    void loginClearsFailureStateWhenCredentialsAreValid() {
        UsuarioSistemaAdministrativoJpaEntity usuario = UsuarioSistemaAdministrativoJpaEntity.crear(
                "admin",
                "hash",
                "ADMIN",
                "ACTIVO"
        );
        ReflectionTestUtils.setField(usuario, "id", 9L);

        when(usuarioRepository.findByNombreLoginIgnoreCase("admin")).thenReturn(Optional.of(usuario));
        when(usuarioPasswordService.matches("secreto", "hash")).thenReturn(true);
        when(jwtTokenService.generateToken("admin", "ADMIN")).thenReturn("jwt-token");
        when(refreshTokenService.issue(anyRefreshSubject())).thenReturn(
                new IssuedRefreshToken(
                        new RefreshTokenSubject(9L, "admin", "ADMIN"),
                        "refresh-token",
                        java.time.Instant.parse("2026-03-10T12:00:00Z"),
                        604800L
                )
        );

        LoginResponseDto response = service.login(new LoginRequestDto("admin", "secreto"), "127.0.0.1");

        assertThat(response.getAccessToken()).isEqualTo("jwt-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getUsuario().getId()).isEqualTo(9L);
        verify(loginProtectionService).assertLoginAllowed("admin", "127.0.0.1");
        verify(loginProtectionService).registerSuccess("admin", "127.0.0.1");
        verify(loginProtectionService, never()).registerFailure(eq("admin"), eq("127.0.0.1"));
    }

    @Test
    void refreshIssuesNewAccessTokenForActiveUser() {
        UsuarioSistemaAdministrativoJpaEntity usuario = UsuarioSistemaAdministrativoJpaEntity.crear(
                "admin",
                "hash",
                "ADMIN",
                "ACTIVO"
        );
        ReflectionTestUtils.setField(usuario, "id", 9L);

        when(refreshTokenService.rotate("refresh-token")).thenReturn(
                new IssuedRefreshToken(
                        new RefreshTokenSubject(9L, "admin", "ADMIN"),
                        "refresh-token-2",
                        java.time.Instant.parse("2026-03-10T12:00:00Z"),
                        604800L
                )
        );
        when(usuarioRepository.findByNombreLoginIgnoreCase("admin")).thenReturn(Optional.of(usuario));
        when(jwtTokenService.generateToken("admin", "ADMIN")).thenReturn("jwt-token-2");

        LoginResponseDto response = service.refresh("refresh-token");

        assertThat(response.getAccessToken()).isEqualTo("jwt-token-2");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token-2");
        assertThat(response.getUsuario().getLogin()).isEqualTo("admin");
    }

    @Test
    void logoutRevokesRefreshToken() {
        service.logout("refresh-token");

        verify(refreshTokenService).revoke("refresh-token");
    }

    private static RefreshTokenSubject anyRefreshSubject() {
        return org.mockito.ArgumentMatchers.argThat(subject ->
                subject != null
                        && subject.userId() != null
                        && subject.login() != null
                        && subject.role() != null
        );
    }
}
