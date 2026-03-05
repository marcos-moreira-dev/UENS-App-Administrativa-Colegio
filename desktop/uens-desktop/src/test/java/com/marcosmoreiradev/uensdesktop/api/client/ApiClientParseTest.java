package com.marcosmoreiradev.uensdesktop.api.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto.EstudianteListItemDto;
import com.marcosmoreiradev.uensdesktop.common.error.ErrorCategory;
import com.marcosmoreiradev.uensdesktop.session.Role;
import com.marcosmoreiradev.uensdesktop.session.SessionState;
import com.marcosmoreiradev.uensdesktop.session.UsuarioSession;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApiClientParseTest {

    private HttpServer server;
    private SessionState sessionState;
    private ApiClient apiClient;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.start();

        sessionState = new SessionState();
        sessionState.login("token-demo", new UsuarioSession(1L, "admin", Role.ADMIN, "ACTIVO"));
        apiClient = new ApiClient(new ApiConfig("http://localhost:" + server.getAddress().getPort(), 2), sessionState);
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void get_parsesApiResponseDataAndSendsAuthorizationHeader() throws Exception {
        AtomicReference<String> authorizationHeader = new AtomicReference<>();
        server.createContext("/api/test/estudiante", exchange -> {
            authorizationHeader.set(exchange.getRequestHeaders().getFirst("Authorization"));
            respondJson(
                    exchange,
                    """
                    {
                      "ok": true,
                      "message": "ok",
                      "data": {
                        "id": 7,
                        "nombres": "Ana",
                        "apellidos": "Bravo",
                        "fechaNacimiento": "2017-05-10",
                        "estado": "ACTIVO",
                        "representanteLegalId": 12,
                        "seccionId": 4
                      }
                    }
                    """);
        });

        ApiResult<EstudianteListItemDto> result =
                apiClient.get("/api/test/estudiante", EstudianteListItemDto.class, true);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).hasValueSatisfying(data -> {
            assertThat(data.id()).isEqualTo(7L);
            assertThat(data.nombres()).isEqualTo("Ana");
            assertThat(data.apellidos()).isEqualTo("Bravo");
            assertThat(data.fechaNacimiento()).isEqualTo(LocalDate.of(2017, 5, 10));
        });
        assertThat(authorizationHeader.get()).isEqualTo("Bearer token-demo");
    }

    @Test
    void getPage_parsesItemsAndPaginationMetadata() throws Exception {
        server.createContext("/api/test/estudiantes", exchange -> respondJson(
                exchange,
                """
                {
                  "ok": true,
                  "data": {
                    "items": [
                      {
                        "id": 9,
                        "nombres": "Luis",
                        "apellidos": "Mora",
                        "fechaNacimiento": "2016-09-01",
                        "estado": "ACTIVO",
                        "representanteLegalId": 3,
                        "seccionId": 2
                      }
                    ],
                    "page": 0,
                    "size": 20,
                    "totalElements": 1,
                    "totalPages": 1,
                    "numberOfElements": 1,
                    "first": true,
                    "last": true,
                    "sort": "apellidos,asc"
                  }
                }
                """));

        ApiResult<PageResponse<EstudianteListItemDto>> result =
                apiClient.getPage("/api/test/estudiantes?page=0&size=20", EstudianteListItemDto.class, true);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).hasValueSatisfying(page -> {
            assertThat(page.getItems()).hasSize(1);
            assertThat(page.getItems().getFirst().displayName()).isEqualTo("Mora, Luis");
            assertThat(page.getPage()).isZero();
            assertThat(page.getTotalElements()).isEqualTo(1);
            assertThat(page.isLast()).isTrue();
        });
    }

    @Test
    void getBinary_extractsUtf8FileNameAndContentType() throws Exception {
        AtomicReference<String> acceptHeader = new AtomicReference<>();
        server.createContext("/api/test/reporte", exchange -> {
            acceptHeader.set(exchange.getRequestHeaders().getFirst("Accept"));
            byte[] body = "reporte-demo".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add(
                    "Content-Disposition",
                    "attachment; filename*=UTF-8''Reporte%20Seccion%201.pdf");
            exchange.getResponseHeaders().add("Content-Type", "application/pdf");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });

        ApiResult<BinaryPayload> result = apiClient.getBinary("/api/test/reporte", true);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).hasValueSatisfying(payload -> {
            assertThat(new String(payload.bytes(), StandardCharsets.UTF_8)).isEqualTo("reporte-demo");
            assertThat(payload.fileName()).isEqualTo("Reporte Seccion 1.pdf");
            assertThat(payload.contentType()).isEqualTo("application/pdf");
        });
        assertThat(acceptHeader.get()).isEqualTo("*/*");
    }

    @Test
    void unauthorizedResponse_logsOutSessionAndMapsAuthError() throws Exception {
        server.createContext("/api/test/unauthorized", exchange -> respondJson(
                exchange,
                401,
                """
                {
                  "ok": false,
                  "errorCode": "AUTH-401",
                  "message": "Token expirado",
                  "requestId": "req-401"
                }
                """));

        ApiResult<EstudianteListItemDto> result =
                apiClient.get("/api/test/unauthorized", EstudianteListItemDto.class, true);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.error()).hasValueSatisfying(error -> {
            assertThat(error.category()).isEqualTo(ErrorCategory.AUTH);
            assertThat(error.message()).isEqualTo("Token expirado");
            assertThat(error.errorCode()).isEqualTo("AUTH-401");
            assertThat(error.requestId()).isEqualTo("req-401");
        });
        assertThat(sessionState.token()).isEmpty();
        assertThat(sessionState.usuario()).isEmpty();
    }

    private static void respondJson(HttpExchange exchange, String body) throws IOException {
        respondJson(exchange, 200, body);
    }

    private static void respondJson(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
