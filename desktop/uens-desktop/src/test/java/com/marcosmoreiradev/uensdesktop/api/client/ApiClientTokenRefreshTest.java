package com.marcosmoreiradev.uensdesktop.api.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.marcosmoreiradev.uensdesktop.session.Role;
import com.marcosmoreiradev.uensdesktop.session.SessionState;
import com.marcosmoreiradev.uensdesktop.session.UsuarioSession;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApiClientTokenRefreshTest {

    private HttpServer server;
    private SessionState sessionState;
    private ApiClient apiClient;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.start();

        sessionState = new SessionState();
        sessionState.login(
                "token-viejo",
                Instant.now().plusSeconds(30),
                "refresh-viejo",
                Instant.now().plusSeconds(600),
                new UsuarioSession(1L, "admin", Role.ADMIN, "ACTIVO")
        );
        apiClient = new ApiClient(new ApiConfig("http://localhost:" + server.getAddress().getPort(), 2), sessionState);
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void authenticatedRequestRefreshesExpiringTokenBeforeCallingProtectedEndpoint() throws Exception {
        AtomicInteger refreshCalls = new AtomicInteger();
        AtomicReference<String> refreshBody = new AtomicReference<>();
        AtomicReference<String> authorizationHeader = new AtomicReference<>();

        server.createContext("/api/v1/auth/refresh", exchange -> {
            refreshCalls.incrementAndGet();
            refreshBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            respondJson(
                    exchange,
                    """
                    {
                      "ok": true,
                      "data": {
                        "accessToken": "token-nuevo",
                        "refreshToken": "refresh-nuevo",
                        "tokenType": "Bearer",
                        "expiresInSeconds": 3600,
                        "refreshExpiresInSeconds": 7200,
                        "usuario": {
                          "id": 1,
                          "login": "admin",
                          "rol": "ADMIN",
                          "estado": "ACTIVO"
                        }
                      }
                    }
                    """
            );
        });

        server.createContext("/api/test/seguro", exchange -> {
            authorizationHeader.set(exchange.getRequestHeaders().getFirst("Authorization"));
            respondJson(
                    exchange,
                    """
                    {
                      "ok": true,
                      "data": {
                        "id": 1,
                        "nombres": "Ana",
                        "apellidos": "Bravo",
                        "fechaNacimiento": "2017-05-10",
                        "estado": "ACTIVO",
                        "representanteLegalId": 12,
                        "seccionId": 4
                      }
                    }
                    """
            );
        });

        ApiResult<com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto.EstudianteListItemDto> result =
                apiClient.get(
                        "/api/test/seguro",
                        com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto.EstudianteListItemDto.class,
                        true
                );

        assertThat(result.isSuccess()).isTrue();
        assertThat(refreshCalls.get()).isEqualTo(1);
        assertThat(refreshBody.get()).contains("refresh-viejo");
        assertThat(authorizationHeader.get()).isEqualTo("Bearer token-nuevo");
        assertThat(sessionState.token()).contains("token-nuevo");
        assertThat(sessionState.refreshToken()).contains("refresh-nuevo");
    }

    private static void respondJson(HttpExchange exchange, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
