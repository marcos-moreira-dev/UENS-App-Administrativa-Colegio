package com.marcosmoreiradev.uensbackend.modules.reporte.application.export;

import com.marcosmoreiradev.uensbackend.modules.reporte.application.export.model.ReporteDocumentModel;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Exportador XLSX con hoja de resumen y hoja tabular para el detalle.
 */
@Component
public class ExcelReporteFileExporter implements ReporteFileExporter {

    private static final short BRAND_BLUE = IndexedColors.DARK_BLUE.getIndex();
    private static final short HEADER_FILL = IndexedColors.GREY_25_PERCENT.getIndex();
    private static final short ALT_FILL = IndexedColors.GREY_25_PERCENT.getIndex();
    private static final int SUMMARY_CONTENT_ROW = 6;

    @Override
    public boolean soporta(String formato) {
        return "XLSX".equalsIgnoreCase(formato);
    }

    @Override
    public String extension() {
        return "xlsx";
    }

    @Override
    public String mimeType() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    @Override
    public void exportar(Path outputFile, ReporteDocumentModel documentModel) {
        try (Workbook workbook = new XSSFWorkbook();
             OutputStream outputStream = Files.newOutputStream(outputFile)) {

            ExcelStyles styles = new ExcelStyles(workbook);
            writeSummarySheet(workbook, documentModel, styles);
            writeDataSheet(workbook, documentModel, styles);
            workbook.setActiveSheet(0);
            workbook.write(outputStream);
        } catch (Exception ex) {
            throw new IllegalStateException("No fue posible exportar el archivo XLSX.", ex);
        }
    }

    private void writeSummarySheet(Workbook workbook, ReporteDocumentModel documentModel, ExcelStyles styles) {
        Sheet sheet = workbook.createSheet("Resumen");
        sheet.setDisplayGridlines(false);

        int rowIndex = insertLogo(workbook, sheet) ? SUMMARY_CONTENT_ROW : 0;
        rowIndex = writeTitleBlock(sheet, rowIndex, documentModel, styles);

        if (documentModel.hasSummary()) {
            rowIndex = writeKeyValueSection(sheet, rowIndex, "Resumen", documentModel.summary(), styles);
        }
        if (documentModel.hasFilters()) {
            rowIndex = writeKeyValueSection(sheet, rowIndex, "Filtros aplicados", documentModel.filters(), styles);
        }
        if (!documentModel.hasSummary() && !documentModel.hasFilters()) {
            Row row = createRow(sheet, rowIndex++);
            writeCell(row, 0, "Sin metadatos adicionales para este reporte.", styles.bodyMuted());
        }

        configureSummaryColumns(sheet);
    }

    private void writeDataSheet(Workbook workbook, ReporteDocumentModel documentModel, ExcelStyles styles) {
        Sheet sheet = workbook.createSheet("Detalle");
        sheet.createFreezePane(0, 4);

        int rowIndex = 0;
        rowIndex = writeTitleBlock(sheet, rowIndex, documentModel, styles);

        Row helperRow = createRow(sheet, rowIndex++);
        writeCell(helperRow, 0, "Registros", styles.sectionLabel());
        writeCell(helperRow, 1, Integer.toString(documentModel.rows().size()), styles.bodyValue());

        rowIndex++;

        if (!documentModel.hasRows() || documentModel.columns().isEmpty()) {
            Row row = createRow(sheet, rowIndex);
            writeCell(row, 0, documentModel.emptyMessage(), styles.bodyMuted());
            sheet.setColumnWidth(0, 9000);
            return;
        }

        Row headerRow = createRow(sheet, rowIndex++);
        for (int index = 0; index < documentModel.columns().size(); index++) {
            writeCell(headerRow, index, documentModel.columns().get(index), styles.tableHeader());
        }

        int dataStartRow = rowIndex - 1;
        int currentRow = rowIndex;
        for (List<String> rowData : documentModel.rows()) {
            Row row = createRow(sheet, currentRow);
            CellStyle style = (currentRow % 2 == 0) ? styles.tableBody() : styles.tableBodyAlternate();
            for (int index = 0; index < documentModel.columns().size(); index++) {
                String value = index < rowData.size() ? rowData.get(index) : "";
                writeCell(row, index, value, style);
            }
            currentRow++;
        }

        sheet.setAutoFilter(new CellRangeAddress(dataStartRow, currentRow - 1, 0, documentModel.columns().size() - 1));
        autoSizeColumns(sheet, documentModel.columns().size());
    }

    private int writeTitleBlock(
            Sheet sheet,
            int rowIndex,
            ReporteDocumentModel documentModel,
            ExcelStyles styles
    ) {
        Row orgRow = createRow(sheet, rowIndex++);
        writeMergedCell(sheet, orgRow, 0, 5, documentModel.organizationName(), styles.organizationTitle());

        Row titleRow = createRow(sheet, rowIndex++);
        writeMergedCell(sheet, titleRow, 0, 5, documentModel.title(), styles.mainTitle());

        if (!documentModel.subtitle().isBlank()) {
            Row subtitleRow = createRow(sheet, rowIndex++);
            writeMergedCell(sheet, subtitleRow, 0, 5, documentModel.subtitle(), styles.subtitle());
        }

        return rowIndex + 1;
    }

    private int writeKeyValueSection(
            Sheet sheet,
            int rowIndex,
            String sectionTitle,
            Map<String, String> values,
            ExcelStyles styles
    ) {
        if (values.isEmpty()) {
            return rowIndex;
        }

        Row titleRow = createRow(sheet, rowIndex++);
        writeMergedCell(sheet, titleRow, 0, 3, sectionTitle, styles.sectionTitle());

        for (Map.Entry<String, String> entry : values.entrySet()) {
            Row row = createRow(sheet, rowIndex++);
            writeCell(row, 0, entry.getKey(), styles.sectionLabel());
            writeCell(row, 1, entry.getValue(), styles.bodyValue());
        }
        return rowIndex + 1;
    }

    private void configureSummaryColumns(Sheet sheet) {
        sheet.setColumnWidth(0, 5200);
        sheet.setColumnWidth(1, 15000);
        sheet.setColumnWidth(2, 4200);
        sheet.setColumnWidth(3, 4200);
        sheet.setColumnWidth(4, 4200);
        sheet.setColumnWidth(5, 4200);
    }

    private void autoSizeColumns(Sheet sheet, int columns) {
        for (int index = 0; index < columns; index++) {
            sheet.autoSizeColumn(index);
            int currentWidth = sheet.getColumnWidth(index);
            int boundedWidth = Math.max(3500, Math.min(currentWidth + 512, 14000));
            sheet.setColumnWidth(index, boundedWidth);
        }
    }

    private boolean insertLogo(Workbook workbook, Sheet sheet) {
        ReporteLogoAsset logo = ReporteLogoSupport.readLogo();
        if (logo == null || !logo.isValid()) {
            return false;
        }

        int pictureIndex = workbook.addPicture(logo.bytes(), Workbook.PICTURE_TYPE_PNG);
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
        anchor.setCol1(0);
        anchor.setRow1(0);

        Picture picture = drawing.createPicture(anchor, pictureIndex);
        double scale = Math.min(170d / logo.widthPx(), 70d / logo.heightPx());
        picture.resize(scale);
        return true;
    }

    private void writeMergedCell(
            Sheet sheet,
            Row row,
            int fromColumn,
            int toColumn,
            String value,
            CellStyle style
    ) {
        Cell cell = row.createCell(fromColumn);
        cell.setCellValue(safe(value));
        cell.setCellStyle(style);
        if (toColumn > fromColumn) {
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), fromColumn, toColumn));
            for (int index = fromColumn + 1; index <= toColumn; index++) {
                Cell mergedCell = row.createCell(index);
                mergedCell.setCellStyle(style);
            }
        }
    }

    private void writeCell(Row row, int columnIndex, String value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(safe(value));
        cell.setCellStyle(style);
    }

    private Row createRow(Sheet sheet, int rowIndex) {
        Row row = sheet.getRow(rowIndex);
        return row != null ? row : sheet.createRow(rowIndex);
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private record ExcelStyles(
            CellStyle organizationTitle,
            CellStyle mainTitle,
            CellStyle subtitle,
            CellStyle sectionTitle,
            CellStyle sectionLabel,
            CellStyle bodyValue,
            CellStyle bodyMuted,
            CellStyle tableHeader,
            CellStyle tableBody,
            CellStyle tableBodyAlternate
    ) {
        private ExcelStyles(Workbook workbook) {
            this(
                    createOrganizationTitle(workbook),
                    createMainTitle(workbook),
                    createSubtitle(workbook),
                    createSectionTitle(workbook),
                    createSectionLabel(workbook),
                    createBodyValue(workbook),
                    createBodyMuted(workbook),
                    createTableHeader(workbook),
                    createTableBody(workbook, false),
                    createTableBody(workbook, true)
            );
        }

        private static CellStyle createOrganizationTitle(Workbook workbook) {
            CellStyle style = baseTextStyle(workbook);
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(BRAND_BLUE);
            font.setFontHeightInPoints((short) 11);
            style.setFont(font);
            return style;
        }

        private static CellStyle createMainTitle(Workbook workbook) {
            CellStyle style = baseTextStyle(workbook);
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(BRAND_BLUE);
            font.setFontHeightInPoints((short) 15);
            style.setFont(font);
            return style;
        }

        private static CellStyle createSubtitle(Workbook workbook) {
            CellStyle style = baseTextStyle(workbook);
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 10);
            style.setFont(font);
            style.setWrapText(true);
            return style;
        }

        private static CellStyle createSectionTitle(Workbook workbook) {
            CellStyle style = baseTextStyle(workbook);
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setFontHeightInPoints((short) 10);
            style.setFont(font);
            style.setFillForegroundColor(BRAND_BLUE);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            applyBorders(style);
            return style;
        }

        private static CellStyle createSectionLabel(Workbook workbook) {
            CellStyle style = baseTextStyle(workbook);
            Font font = workbook.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 10);
            style.setFont(font);
            style.setFillForegroundColor(HEADER_FILL);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            applyBorders(style);
            return style;
        }

        private static CellStyle createBodyValue(Workbook workbook) {
            CellStyle style = baseTextStyle(workbook);
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 10);
            style.setFont(font);
            applyBorders(style);
            return style;
        }

        private static CellStyle createBodyMuted(Workbook workbook) {
            CellStyle style = baseTextStyle(workbook);
            Font font = workbook.createFont();
            font.setItalic(true);
            font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            font.setFontHeightInPoints((short) 10);
            style.setFont(font);
            return style;
        }

        private static CellStyle createTableHeader(Workbook workbook) {
            CellStyle style = baseTextStyle(workbook);
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setFontHeightInPoints((short) 10);
            style.setFont(font);
            style.setFillForegroundColor(BRAND_BLUE);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            applyBorders(style);
            return style;
        }

        private static CellStyle createTableBody(Workbook workbook, boolean alternate) {
            CellStyle style = baseTextStyle(workbook);
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 10);
            style.setFont(font);
            if (alternate) {
                style.setFillForegroundColor(ALT_FILL);
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            applyBorders(style);
            return style;
        }

        private static CellStyle baseTextStyle(Workbook workbook) {
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(HorizontalAlignment.LEFT);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setWrapText(true);
            return style;
        }

        private static void applyBorders(CellStyle style) {
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        }
    }
}
