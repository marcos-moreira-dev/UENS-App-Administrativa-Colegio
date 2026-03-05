package com.marcosmoreiradev.uensdesktop.modules.reportes.presenter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReportesPresenterTest {

    private final ReportesPresenter presenter = new ReportesPresenter();

    @Test
    void formatJsonBlockReturnsDashWhenBlank() {
        assertThat(presenter.formatJsonBlock("   ")).isEqualTo("-");
    }

    @Test
    void formatJsonBlockFormatsReportResultIntoReadableSections() {
        String rawJson = """
                {
                  "archivo": {
                    "nombreArchivo": "reporte_estudiantes.pdf",
                    "formato": "PDF",
                    "mimeType": "application/pdf",
                    "tamanoBytes": 42010,
                    "rutaRelativa": "reportes/reporte_estudiantes.pdf"
                  },
                  "payload": {
                    "tipoReporte": "LISTADO_ESTUDIANTES_POR_SECCION",
                    "seccion": "1 A - 2026-2027",
                    "totalEstudiantes": 25,
                    "items": [
                      { "id": 1, "nombres": "Ana", "apellidos": "Loor", "estado": "ACTIVO" },
                      { "id": 2, "nombres": "Luis", "apellidos": "Bravo", "estado": "ACTIVO" }
                    ]
                  }
                }
                """;

        String formatted = presenter.formatJsonBlock(rawJson);

        assertThat(formatted).contains("Archivo generado");
        assertThat(formatted).contains("Nombre: reporte_estudiantes.pdf");
        assertThat(formatted).contains("Resumen del reporte");
        assertThat(formatted).contains("Filas incluidas: 2");
        assertThat(formatted).doesNotContain("{");
    }

    @Test
    void formatJsonBlockFormatsGenericJsonWithoutReturningRawBraces() {
        String rawJson = """
                {
                  "formatoSalida": "XLSX",
                  "seccionId": 5,
                  "incluirDetalle": true
                }
                """;

        String formatted = presenter.formatJsonBlock(rawJson);

        assertThat(formatted).contains("Formato Salida: XLSX");
        assertThat(formatted).contains("Seccion Id: 5");
        assertThat(formatted).contains("Incluir Detalle: true");
        assertThat(formatted).doesNotContain("{");
    }
}
