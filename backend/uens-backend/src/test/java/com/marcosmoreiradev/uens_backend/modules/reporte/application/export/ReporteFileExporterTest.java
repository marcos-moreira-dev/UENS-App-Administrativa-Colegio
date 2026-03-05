package com.marcosmoreiradev.uens_backend.modules.reporte.application.export;

import com.marcosmoreiradev.uensbackend.modules.reporte.application.export.DocxReporteFileExporter;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.export.ExcelReporteFileExporter;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.export.PdfReporteFileExporter;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.export.model.ReporteDocumentModel;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReporteFileExporterTest {

    @TempDir
    Path tempDir;

    @Test
    void pdfExporterWritesReadableTitleAndRows() throws Exception {
        Path file = tempDir.resolve("reporte.pdf");

        new PdfReporteFileExporter().exportar(file, sampleDocument());

        assertThat(Files.size(file)).isPositive();
        try (PDDocument document = Loader.loadPDF(file.toFile())) {
            String text = new PDFTextStripper().getText(document);
            assertThat(text).contains("Reporte de estudiantes");
            assertThat(text).contains("Ana Loor");
            assertThat(text).doesNotContain("\"items\"");
        }
    }

    @Test
    void docxExporterWritesTitleSummaryAndTableCells() throws Exception {
        Path file = tempDir.resolve("reporte.docx");

        new DocxReporteFileExporter().exportar(file, sampleDocument());

        assertThat(Files.size(file)).isPositive();
        try (InputStream inputStream = Files.newInputStream(file);
             XWPFDocument document = new XWPFDocument(inputStream)) {
            String paragraphText = document.getParagraphs().stream()
                    .map(paragraph -> paragraph.getText())
                    .reduce("", (left, right) -> left + "\n" + right);
            String tableText = document.getTables().stream()
                    .flatMap(table -> table.getRows().stream())
                    .flatMap(row -> row.getTableCells().stream())
                    .map(cell -> cell.getText())
                    .reduce("", (left, right) -> left + "\n" + right);

            assertThat(paragraphText).contains("Reporte de estudiantes");
            assertThat(tableText).contains("Total estudiantes");
            assertThat(tableText).contains("Ana");
            assertThat(tableText).doesNotContain("\"payload\"");
        }
    }

    @Test
    void excelExporterWritesSummaryAndDataSheets() throws Exception {
        Path file = tempDir.resolve("reporte.xlsx");

        new ExcelReporteFileExporter().exportar(file, sampleDocument());

        assertThat(Files.size(file)).isPositive();
        try (InputStream inputStream = Files.newInputStream(file);
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            assertThat(workbook.getSheet("Resumen")).isNotNull();
            assertThat(workbook.getSheet("Detalle")).isNotNull();
            assertThat(sheetContainsValue(workbook.getSheet("Resumen"), "Resumen")).isTrue();
            assertThat(sheetContainsValue(workbook.getSheet("Detalle"), "ID")).isTrue();
            assertThat(sheetContainsValue(workbook.getSheet("Detalle"), "Ana")).isTrue();
        }
    }

    private boolean sheetContainsValue(Sheet sheet, String expected) {
        for (int rowIndex = sheet.getFirstRowNum(); rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            var row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            for (int columnIndex = row.getFirstCellNum(); columnIndex < row.getLastCellNum(); columnIndex++) {
                if (columnIndex < 0) {
                    continue;
                }
                var cell = row.getCell(columnIndex);
                if (cell != null && expected.equals(cell.getStringCellValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    private ReporteDocumentModel sampleDocument() {
        Map<String, String> summary = new LinkedHashMap<>();
        summary.put("Seccion", "1 A - 2026-2027");
        summary.put("Total estudiantes", "2");

        Map<String, String> filters = new LinkedHashMap<>();
        filters.put("Estado", "ACTIVO");

        return new ReporteDocumentModel(
                "Unidad Educativa Ninitos Sonadores",
                "Reporte de estudiantes",
                "Listado vigente por seccion",
                summary,
                filters,
                List.of("ID", "Nombres", "Apellidos", "Estado"),
                List.of(
                        List.of("1", "Ana", "Loor", "ACTIVO"),
                        List.of("2", "Luis", "Bravo", "ACTIVO")
                ),
                "Sin registros"
        );
    }
}
