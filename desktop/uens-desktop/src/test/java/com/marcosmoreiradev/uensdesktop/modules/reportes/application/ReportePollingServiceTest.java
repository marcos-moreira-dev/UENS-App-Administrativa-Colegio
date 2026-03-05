package com.marcosmoreiradev.uensdesktop.modules.reportes.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.marcosmoreiradev.uensdesktop.api.client.ApiClient;
import com.marcosmoreiradev.uensdesktop.api.client.ApiConfig;
import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.ReportesApi;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudResultResponseDto;
import com.marcosmoreiradev.uensdesktop.session.Role;
import com.marcosmoreiradev.uensdesktop.session.SessionState;
import com.marcosmoreiradev.uensdesktop.session.UsuarioSession;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportePollingServiceTest {

    private HttpServer server;
    private ReportePollingService pollingService;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.start();

        SessionState sessionState = new SessionState();
        sessionState.login("token-demo", new UsuarioSession(1L, "admin", Role.ADMIN, "ACTIVO"));
        ApiClient apiClient = new ApiClient(
                new ApiConfig("http://localhost:" + server.getAddress().getPort(), 2),
                sessionState);
        ReportesService reportesService = new ReportesService(new ReportesApi(apiClient));
        pollingService = new ReportePollingService(
                reportesService,
                Executors.newSingleThreadScheduledExecutor(),
                150,
                TimeUnit.MILLISECONDS);
    }

    @AfterEach
    void tearDown() {
        if (pollingService != null) {
            pollingService.close();
        }
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void startPolling_stopsAutomaticallyWhenStateBecomesCompleted() throws Exception {
        AtomicInteger requests = new AtomicInteger();
        AtomicReference<ApiResult<ReporteSolicitudResultResponseDto>> received = new AtomicReference<>();
        CountDownLatch firstCallback = new CountDownLatch(1);

        server.createContext("/api/v1/reportes/solicitudes/7/estado", exchange -> {
            requests.incrementAndGet();
            respondJson(
                    exchange,
                    """
                    {
                      "ok": true,
                      "data": {
                        "solicitudId": 7,
                        "estado": "COMPLETADA",
                        "resultadoJson": "{}",
                        "errorDetalle": null
                      }
                    }
                    """);
        });

        pollingService.startPolling(7L, result -> {
            received.set(result);
            firstCallback.countDown();
        });

        assertThat(firstCallback.await(1, TimeUnit.SECONDS)).isTrue();
        Thread.sleep(350);

        assertThat(received.get()).isNotNull();
        assertThat(received.get().isSuccess()).isTrue();
        assertThat(received.get().data().map(ReporteSolicitudResultResponseDto::estado)).contains("COMPLETADA");
        assertThat(requests.get()).isEqualTo(1);
    }

    @Test
    void stopPolling_cancelsFutureCallbacksForNonTerminalStates() throws Exception {
        AtomicInteger requests = new AtomicInteger();
        CountDownLatch firstCallback = new CountDownLatch(1);

        server.createContext("/api/v1/reportes/solicitudes/9/estado", exchange -> {
            requests.incrementAndGet();
            respondJson(
                    exchange,
                    """
                    {
                      "ok": true,
                      "data": {
                        "solicitudId": 9,
                        "estado": "PENDIENTE",
                        "resultadoJson": null,
                        "errorDetalle": null
                      }
                    }
                    """);
        });

        pollingService.startPolling(9L, result -> firstCallback.countDown());

        assertThat(firstCallback.await(1, TimeUnit.SECONDS)).isTrue();
        int requestsBeforeStop = requests.get();
        pollingService.stopPolling(9L);

        Thread.sleep(350);

        assertThat(requests.get()).isEqualTo(requestsBeforeStop);
    }

    private static void respondJson(HttpExchange exchange, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
