package com.marcosmoreiradev.uensdesktop.modules.reportes.application;

import com.sun.net.httpserver.HttpServer;
import com.marcosmoreiradev.uensdesktop.api.client.ApiClient;
import com.marcosmoreiradev.uensdesktop.api.client.ApiConfig;
import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.ReportesApi;
import com.marcosmoreiradev.uensdesktop.session.Role;
import com.marcosmoreiradev.uensdesktop.session.SessionState;
import com.marcosmoreiradev.uensdesktop.session.UsuarioSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ReportesServiceTest {

    @TempDir
    Path tempDir;

    private HttpServer server;
    private String previousUserHome;
    private ReportesService reportesService;

    @BeforeEach
    void setUp() throws IOException {
        previousUserHome = System.getProperty("user.home");
        System.setProperty("user.home", tempDir.toString());

        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.start();

        SessionState sessionState = new SessionState();
        sessionState.login("token-demo", new UsuarioSession(1L, "admin", Role.ADMIN, "ACTIVO"));

        ApiClient apiClient = new ApiClient(
                new ApiConfig("http://localhost:" + server.getAddress().getPort(), 2),
                sessionState
        );
        reportesService = new ReportesService(new ReportesApi(apiClient));
    }

    @AfterEach
    void tearDown() {
        if (previousUserHome != null) {
            System.setProperty("user.home", previousUserHome);
        }
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void sanitizeDownloadFileNameRemovesPathCharacters() {
        assertThat(ReportesService.sanitizeDownloadFileName("../reporte\\final?.pdf"))
                .isEqualTo(".._reporte_final_.pdf");
    }

    @Test
    void descargarArchivoStoresFileInsideDownloadsDirectoryUsingSanitizedName() throws Exception {
        server.createContext("/api/v1/reportes/solicitudes/80/archivo", exchange -> {
            byte[] body = "reporte-demo".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add(
                    "Content-Disposition",
                    "attachment; filename=\"../reporte final.pdf\"");
            exchange.getResponseHeaders().add("Content-Type", "application/pdf");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });

        ApiResult<ReporteDownloadResult> result = reportesService.descargarArchivo(80L, "LISTADO_ESTUDIANTES_POR_SECCION");

        assertThat(result.isSuccess()).isTrue();
        ReporteDownloadResult download = result.data().orElseThrow();
        Path expectedDirectory = tempDir.resolve("Downloads").resolve("UENS");
        assertThat(download.path()).startsWith(expectedDirectory);
        assertThat(download.fileName()).isEqualTo(".._reporte final.pdf");
        assertThat(Files.exists(download.path())).isTrue();
        assertThat(Files.readString(download.path())).isEqualTo("reporte-demo");
    }
}
