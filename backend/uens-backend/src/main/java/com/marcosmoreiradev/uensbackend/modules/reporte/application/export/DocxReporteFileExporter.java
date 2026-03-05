package com.marcosmoreiradev.uensbackend.modules.reporte.application.export;

import com.marcosmoreiradev.uensbackend.modules.reporte.application.export.model.ReporteDocumentModel;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Component;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Exportador de reportes a DOCX con bloques de resumen y tabla principal.
 */
@Component
public class DocxReporteFileExporter implements ReporteFileExporter {

    private static final String BRAND_COLOR = "2B4A6B";
    private static final String HEADER_FILL = "DCE6F2";

    @Override
    public boolean soporta(String formato) {
        return "DOCX".equalsIgnoreCase(formato);
    }

    @Override
    public String extension() {
        return "docx";
    }

    @Override
    public String mimeType() {
        return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    }

    @Override
    public void exportar(Path outputFile, ReporteDocumentModel documentModel) {
        try (XWPFDocument document = new XWPFDocument();
             OutputStream outputStream = Files.newOutputStream(outputFile)) {

            configureLandscape(document);
            appendLogo(document);
            appendTitleBlock(document, documentModel);

            if (documentModel.hasSummary()) {
                appendLabeledTable(document, "Resumen", documentModel.summary());
            }
            if (documentModel.hasFilters()) {
                appendLabeledTable(document, "Filtros aplicados", documentModel.filters());
            }

            appendDataTable(document, documentModel);
            document.write(outputStream);
        } catch (Exception ex) {
            throw new IllegalStateException("No fue posible exportar el archivo DOCX.", ex);
        }
    }

    private void configureLandscape(XWPFDocument document) {
        CTSectPr section = document.getDocument().getBody().isSetSectPr()
                ? document.getDocument().getBody().getSectPr()
                : document.getDocument().getBody().addNewSectPr();
        CTPageSz pageSize = section.isSetPgSz() ? section.getPgSz() : section.addNewPgSz();
        pageSize.setOrient(STPageOrientation.LANDSCAPE);
        pageSize.setW(BigInteger.valueOf(16840));
        pageSize.setH(BigInteger.valueOf(11900));
    }

    private void appendLogo(XWPFDocument document) throws Exception {
        ReporteLogoAsset logo = ReporteLogoSupport.readLogo();
        if (logo == null || !logo.isValid()) {
            return;
        }
        double scale = Math.min(170d / logo.widthPx(), 72d / logo.heightPx());
        int width = (int) Math.round(logo.widthPx() * scale);
        int height = (int) Math.round(logo.heightPx() * scale);

        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        try (ByteArrayInputStream logoStream = new ByteArrayInputStream(logo.bytes())) {
            run.addPicture(
                    logoStream,
                    XWPFDocument.PICTURE_TYPE_PNG,
                    "logo.png",
                    Units.pixelToEMU(width),
                    Units.pixelToEMU(height)
            );
        }
        paragraph.setSpacingAfter(100);
    }

    private void appendTitleBlock(XWPFDocument document, ReporteDocumentModel model) {
        XWPFParagraph institution = document.createParagraph();
        institution.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun institutionRun = institution.createRun();
        institutionRun.setBold(true);
        institutionRun.setFontSize(11);
        institutionRun.setColor(BRAND_COLOR);
        institutionRun.setText(model.organizationName());

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        title.setSpacingAfter(60);
        XWPFRun titleRun = title.createRun();
        titleRun.setBold(true);
        titleRun.setFontSize(17);
        titleRun.setColor(BRAND_COLOR);
        titleRun.setText(model.title());

        if (!model.subtitle().isBlank()) {
            XWPFParagraph subtitle = document.createParagraph();
            subtitle.setAlignment(ParagraphAlignment.CENTER);
            subtitle.setSpacingAfter(180);
            XWPFRun subtitleRun = subtitle.createRun();
            subtitleRun.setFontSize(10);
            subtitleRun.setText(model.subtitle());
        }
    }

    private void appendLabeledTable(XWPFDocument document, String title, Map<String, String> section) {
        if (section.isEmpty()) {
            return;
        }

        XWPFParagraph sectionTitle = document.createParagraph();
        sectionTitle.setSpacingBefore(120);
        sectionTitle.setSpacingAfter(60);
        XWPFRun titleRun = sectionTitle.createRun();
        titleRun.setBold(true);
        titleRun.setFontSize(11);
        titleRun.setColor(BRAND_COLOR);
        titleRun.setText(title);

        XWPFTable table = document.createTable(section.size(), 2);
        styleTable(table);
        int rowIndex = 0;
        for (Map.Entry<String, String> entry : section.entrySet()) {
            XWPFTableRow row = table.getRow(rowIndex++);
            writeCell(row.getCell(0), entry.getKey(), true, HEADER_FILL, "000000");
            writeCell(row.getCell(1), entry.getValue(), false, null, "000000");
        }
    }

    private void appendDataTable(XWPFDocument document, ReporteDocumentModel model) {
        XWPFParagraph sectionTitle = document.createParagraph();
        sectionTitle.setSpacingBefore(120);
        sectionTitle.setSpacingAfter(60);
        XWPFRun titleRun = sectionTitle.createRun();
        titleRun.setBold(true);
        titleRun.setFontSize(11);
        titleRun.setColor(BRAND_COLOR);
        titleRun.setText("Detalle");

        if (!model.hasRows()) {
            XWPFParagraph empty = document.createParagraph();
            XWPFRun run = empty.createRun();
            run.setItalic(true);
            run.setText(model.emptyMessage());
            return;
        }

        XWPFTable table = document.createTable(model.rows().size() + 1, model.columns().size());
        styleTable(table);
        XWPFTableRow headerRow = table.getRow(0);
        for (int columnIndex = 0; columnIndex < model.columns().size(); columnIndex++) {
            writeCell(headerRow.getCell(columnIndex), model.columns().get(columnIndex), true, BRAND_COLOR, "FFFFFF");
        }

        int rowIndex = 1;
        for (List<String> rowData : model.rows()) {
            XWPFTableRow row = table.getRow(rowIndex++);
            for (int columnIndex = 0; columnIndex < model.columns().size(); columnIndex++) {
                String value = columnIndex < rowData.size() ? rowData.get(columnIndex) : "";
                writeCell(row.getCell(columnIndex), value, false, null, "000000");
            }
        }
    }

    private void styleTable(XWPFTable table) {
        table.setWidth("100%");
        table.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, "B9C2CF");
        table.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, "B9C2CF");
        table.setBottomBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, "B9C2CF");
        table.setTopBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, "B9C2CF");
        table.setLeftBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, "B9C2CF");
        table.setRightBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, "B9C2CF");
    }

    private void writeCell(XWPFTableCell cell, String text, boolean bold, String fillColor, String textColor) {
        if (fillColor != null) {
            cell.setColor(fillColor);
        }
        XWPFParagraph paragraph = cell.getParagraphs().isEmpty() ? cell.addParagraph() : cell.getParagraphs().getFirst();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        paragraph.setVerticalAlignment(TextAlignment.CENTER);
        paragraph.setSpacingAfter(0);
        paragraph.setSpacingBefore(0);
        XWPFRun run = paragraph.createRun();
        run.setBold(bold);
        run.setFontSize(9);
        if (textColor != null) {
            run.setColor(textColor);
        }
        run.setText(text == null || text.isBlank() ? "-" : text);
    }
}
