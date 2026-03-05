package com.marcosmoreiradev.uensbackend.modules.reporte.application.export;

import com.marcosmoreiradev.uensbackend.modules.reporte.application.export.model.ReporteDocumentModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Exportador de reportes a PDF usando un layout tabular legible.
 */
@Component
public class PdfReporteFileExporter implements ReporteFileExporter {

    private static final PDRectangle PAGE_SIZE = new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());
    private static final float MARGIN = 36f;
    private static final float LOGO_MAX_WIDTH = 118f;
    private static final float LOGO_MAX_HEIGHT = 56f;
    private static final float TITLE_FONT_SIZE = 18f;
    private static final float SUBTITLE_FONT_SIZE = 10.5f;
    private static final float SECTION_TITLE_FONT_SIZE = 10f;
    private static final float META_FONT_SIZE = 9f;
    private static final float TABLE_FONT_SIZE = 8.2f;
    private static final float ROW_HEIGHT = 18f;
    private static final float HEADER_HEIGHT = 20f;
    private static final Color BRAND_BLUE = new Color(43, 74, 107);
    private static final Color HEADER_FILL = new Color(226, 233, 242);
    private static final Color ALT_ROW_FILL = new Color(247, 249, 252);
    private static final Color BORDER_COLOR = new Color(185, 194, 207);

    private static final PDType1Font FONT_REGULAR = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDType1Font FONT_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    @Override
    public boolean soporta(String formato) {
        return "PDF".equalsIgnoreCase(formato);
    }

    @Override
    public String extension() {
        return "pdf";
    }

    @Override
    public String mimeType() {
        return "application/pdf";
    }

    @Override
    public void exportar(Path outputFile, ReporteDocumentModel documentModel) {
        try (PDDocument document = new PDDocument()) {
            List<Float> columnWidths = calculateColumnWidths(documentModel);
            List<List<String>> rows = documentModel.rows();
            int rowIndex = 0;
            boolean firstPage = true;

            while (firstPage || rowIndex < rows.size() || !documentModel.hasRows()) {
                PDPage page = new PDPage(PAGE_SIZE);
                document.addPage(page);

                try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                    float y = page.getMediaBox().getHeight() - MARGIN;
                    y = drawHeader(stream, document, documentModel, y);
                    if (firstPage) {
                        y = drawMetadata(stream, documentModel, y);
                    }

                    if (!documentModel.columns().isEmpty()) {
                        y = drawTableHeader(stream, documentModel.columns(), columnWidths, y);
                        if (documentModel.hasRows()) {
                            while (rowIndex < rows.size()) {
                                if (y - ROW_HEIGHT < MARGIN) {
                                    break;
                                }
                                drawTableRow(stream, rows.get(rowIndex), columnWidths, y, rowIndex % 2 == 0);
                                y -= ROW_HEIGHT;
                                rowIndex++;
                            }
                        } else {
                            drawEmptyState(stream, documentModel.emptyMessage(), y - 4f);
                        }
                    } else {
                        drawEmptyState(stream, documentModel.emptyMessage(), y);
                    }
                }

                firstPage = false;
                if (!documentModel.hasRows()) {
                    break;
                }
            }

            document.save(outputFile.toFile());
        } catch (Exception ex) {
            throw new IllegalStateException("No fue posible exportar el archivo PDF.", ex);
        }
    }

    private float drawHeader(
            PDPageContentStream stream,
            PDDocument document,
            ReporteDocumentModel model,
            float y
    ) throws Exception {
        ReporteLogoAsset logo = ReporteLogoSupport.readLogo();
        float consumedHeight = 0f;
        float logoBottomY = y;
        if (logo != null && logo.isValid()) {
            float scale = Math.min(LOGO_MAX_WIDTH / logo.widthPx(), LOGO_MAX_HEIGHT / logo.heightPx());
            float width = logo.widthPx() * scale;
            float height = logo.heightPx() * scale;
            PDImageXObject logoImage = PDImageXObject.createFromByteArray(document, logo.bytes(), "uens-logo");
            stream.drawImage(logoImage, MARGIN, y - height, width, height);
            consumedHeight = height;
            logoBottomY = y - height;
        }

        writeText(stream, model.organizationName(), MARGIN + LOGO_MAX_WIDTH + 12f, y - 12f, FONT_BOLD, 11f, BRAND_BLUE);
        writeText(stream, model.title(), MARGIN, Math.min(logoBottomY, y - consumedHeight) - 18f, FONT_BOLD, TITLE_FONT_SIZE, BRAND_BLUE);
        if (!model.subtitle().isBlank()) {
            writeText(stream, model.subtitle(), MARGIN, Math.min(logoBottomY, y - consumedHeight) - 34f, FONT_REGULAR, SUBTITLE_FONT_SIZE, Color.DARK_GRAY);
        }
        return Math.min(logoBottomY, y - consumedHeight) - 48f;
    }

    private float drawMetadata(PDPageContentStream stream, ReporteDocumentModel model, float y) throws Exception {
        if (model.hasSummary()) {
            y = drawMetadataSection(stream, "Resumen", model.summary(), y);
        }
        if (model.hasFilters()) {
            y = drawMetadataSection(stream, "Filtros aplicados", model.filters(), y);
        }
        return y - 8f;
    }

    private float drawMetadataSection(PDPageContentStream stream, String title, Map<String, String> section, float y) throws Exception {
        writeText(stream, title, MARGIN, y, FONT_BOLD, SECTION_TITLE_FONT_SIZE, BRAND_BLUE);
        y -= 14f;
        for (Map.Entry<String, String> entry : section.entrySet()) {
            writeText(stream, sanitize(entry.getKey() + ":"), MARGIN, y, FONT_BOLD, META_FONT_SIZE, Color.DARK_GRAY);
            writeText(stream, sanitize(entry.getValue()), MARGIN + 110f, y, FONT_REGULAR, META_FONT_SIZE, Color.BLACK);
            y -= 12f;
        }
        return y - 2f;
    }

    private float drawTableHeader(
            PDPageContentStream stream,
            List<String> columns,
            List<Float> widths,
            float y
    ) throws Exception {
        float x = MARGIN;
        for (int index = 0; index < columns.size(); index++) {
            float width = widths.get(index);
            fillRect(stream, x, y - HEADER_HEIGHT, width, HEADER_HEIGHT, HEADER_FILL);
            drawRect(stream, x, y - HEADER_HEIGHT, width, HEADER_HEIGHT, BORDER_COLOR);
            writeText(stream, truncateToWidth(columns.get(index), width - 10f, FONT_BOLD, TABLE_FONT_SIZE), x + 5f, y - 13f, FONT_BOLD, TABLE_FONT_SIZE, BRAND_BLUE);
            x += width;
        }
        return y - HEADER_HEIGHT;
    }

    private void drawTableRow(
            PDPageContentStream stream,
            List<String> row,
            List<Float> widths,
            float y,
            boolean alternateFill
    ) throws Exception {
        float x = MARGIN;
        for (int index = 0; index < widths.size(); index++) {
            float width = widths.get(index);
            if (alternateFill) {
                fillRect(stream, x, y - ROW_HEIGHT, width, ROW_HEIGHT, ALT_ROW_FILL);
            }
            drawRect(stream, x, y - ROW_HEIGHT, width, ROW_HEIGHT, BORDER_COLOR);
            String value = index < row.size() ? row.get(index) : "";
            writeText(
                    stream,
                    truncateToWidth(value, width - 10f, FONT_REGULAR, TABLE_FONT_SIZE),
                    x + 5f,
                    y - 12f,
                    FONT_REGULAR,
                    TABLE_FONT_SIZE,
                    Color.BLACK
            );
            x += width;
        }
    }

    private void drawEmptyState(PDPageContentStream stream, String message, float y) throws Exception {
        writeText(stream, sanitize(message), MARGIN, y, FONT_REGULAR, 10f, Color.DARK_GRAY);
    }

    private List<Float> calculateColumnWidths(ReporteDocumentModel documentModel) throws Exception {
        float availableWidth = PAGE_SIZE.getWidth() - (MARGIN * 2f);
        if (documentModel.columns().isEmpty()) {
            return List.of();
        }

        List<Float> weights = new ArrayList<>();
        for (int index = 0; index < documentModel.columns().size(); index++) {
            float maxWidth = textWidth(documentModel.columns().get(index), FONT_BOLD, TABLE_FONT_SIZE) + 18f;
            for (List<String> row : documentModel.rows()) {
                if (index >= row.size()) {
                    continue;
                }
                maxWidth = Math.max(maxWidth, textWidth(row.get(index), FONT_REGULAR, TABLE_FONT_SIZE) + 18f);
            }
            weights.add(Math.min(Math.max(maxWidth, 62f), 180f));
        }

        float total = 0f;
        for (float weight : weights) {
            total += weight;
        }
        if (total <= availableWidth) {
            return weights;
        }

        float scale = availableWidth / total;
        return weights.stream().map(weight -> Math.max(weight * scale, 52f)).toList();
    }

    private float textWidth(String value, PDType1Font font, float fontSize) throws Exception {
        String safeValue = sanitize(value);
        return font.getStringWidth(safeValue) / 1000f * fontSize;
    }

    private String truncateToWidth(String value, float availableWidth, PDType1Font font, float fontSize) throws Exception {
        String safeValue = sanitize(value == null ? "" : value.replace('\n', ' '));
        if (safeValue.isBlank()) {
            return "";
        }
        if (textWidth(safeValue, font, fontSize) <= availableWidth) {
            return safeValue;
        }

        String ellipsis = "...";
        StringBuilder builder = new StringBuilder();
        for (char character : safeValue.toCharArray()) {
            String candidate = builder + Character.toString(character) + ellipsis;
            if (textWidth(candidate, font, fontSize) > availableWidth) {
                break;
            }
            builder.append(character);
        }
        return builder.isEmpty() ? ellipsis : builder + ellipsis;
    }

    private void writeText(
            PDPageContentStream stream,
            String value,
            float x,
            float y,
            PDType1Font font,
            float fontSize,
            Color color
    ) throws Exception {
        stream.beginText();
        stream.setFont(font, fontSize);
        stream.setNonStrokingColor(color);
        stream.newLineAtOffset(x, y);
        stream.showText(sanitize(value));
        stream.endText();
    }

    private void fillRect(PDPageContentStream stream, float x, float y, float width, float height, Color color) throws Exception {
        stream.setNonStrokingColor(color);
        stream.addRect(x, y, width, height);
        stream.fill();
    }

    private void drawRect(PDPageContentStream stream, float x, float y, float width, float height, Color color) throws Exception {
        stream.setStrokingColor(color);
        stream.addRect(x, y, width, height);
        stream.stroke();
    }

    private String sanitize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.replace('\t', ' ').replace('\r', ' ').replace('\n', ' ');
        StringBuilder sb = new StringBuilder(normalized.length());
        for (char c : normalized.toCharArray()) {
            sb.append((c >= 32 && c <= 255) ? c : '?');
        }
        return sb.toString();
    }
}
