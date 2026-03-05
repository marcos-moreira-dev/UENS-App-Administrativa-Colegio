package com.marcosmoreiradev.uens_backend.modules.auth.api;

import com.marcosmoreiradev.uensbackend.common.api.response.ApiResponse;
import com.marcosmoreiradev.uensbackend.common.api.util.ClientIpResolver;
import com.marcosmoreiradev.uensbackend.modules.auth.api.AuthController;
import com.marcosmoreiradev.uensbackend.modules.auth.api.dto.AuthUsuarioResumenDto;
import com.marcosmoreiradev.uensbackend.modules.auth.api.dto.LoginRequestDto;
import com.marcosmoreiradev.uensbackend.modules.auth.api.dto.LoginResponseDto;
import com.marcosmoreiradev.uensbackend.modules.auth.api.dto.LogoutRequestDto;
import com.marcosmoreiradev.uensbackend.modules.auth.api.dto.RefreshTokenRequestDto;
import com.marcosmoreiradev.uensbackend.modules.auth.application.AuthApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
/**
 * Define la responsabilidad de AuthControllerTest dentro del backend UENS.
 * Contexto: modulo auth, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
class AuthControllerTest {

    @Mock
    private AuthApplicationService authApplicationService;
    @Mock
    private ClientIpResolver clientIpResolver;

    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController(authApplicationService, clientIpResolver);
    }

    @Test
    void loginReturnsTokenPayload() throws Exception {
        LoginResponseDto response = new LoginResponseDto(
                "token-123",
                "refresh-123",
                "Bearer",
                3600L,
                604800L,
                new AuthUsuarioResumenDto(1L, "admin", "ADMIN", "ACTIVO")
        );
        when(clientIpResolver.resolve(any())).thenReturn("127.0.0.1");
        when(authApplicationService.login(any(LoginRequestDto.class), eq("127.0.0.1"))).thenReturn(response);

        ResponseEntity<ApiResponse<LoginResponseDto>> entity =
                controller.login(new LoginRequestDto("admin", "secreto"), new MockHttpServletRequest());

        assertEquals(200, entity.getStatusCode().value());
        assertTrue(entity.getBody().isOk());
        assertEquals("token-123", entity.getBody().getData().getAccessToken());
        assertEquals("admin", entity.getBody().getData().getUsuario().getLogin());
    }

    @Test
    void refreshReturnsRotatedTokenPayload() {
        LoginResponseDto response = new LoginResponseDto(
                "token-456",
                "refresh-456",
                "Bearer",
                3600L,
                604800L,
                new AuthUsuarioResumenDto(1L, "admin", "ADMIN", "ACTIVO")
        );
        when(authApplicationService.refresh("refresh-123")).thenReturn(response);

        ResponseEntity<ApiResponse<LoginResponseDto>> entity =
                controller.refresh(new RefreshTokenRequestDto("refresh-123"));

        assertEquals(200, entity.getStatusCode().value());
        assertTrue(entity.getBody().isOk());
        assertEquals("token-456", entity.getBody().getData().getAccessToken());
        assertEquals("refresh-456", entity.getBody().getData().getRefreshToken());
    }

    @Test
    void logoutDelegatesRefreshTokenRevocation() {
        ResponseEntity<ApiResponse<Void>> entity = controller.logout(new LogoutRequestDto("refresh-123"));

        assertEquals(200, entity.getStatusCode().value());
        assertTrue(entity.getBody().isOk());
    }

    @Test
    void meReturnsAuthenticatedUser() throws Exception {
        when(authApplicationService.me("admin"))
                .thenReturn(new AuthUsuarioResumenDto(1L, "admin", "ADMIN", "ACTIVO"));

        TestingAuthenticationToken auth = new TestingAuthenticationToken("admin", "n/a");
        auth.setAuthenticated(true);

        ResponseEntity<ApiResponse<AuthUsuarioResumenDto>> entity = controller.me(auth);

        assertEquals(200, entity.getStatusCode().value());
        assertTrue(entity.getBody().isOk());
        assertEquals("admin", entity.getBody().getData().getLogin());
        assertEquals("ADMIN", entity.getBody().getData().getRol());
    }
}
