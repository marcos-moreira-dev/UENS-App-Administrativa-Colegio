package com.marcosmoreiradev.uensdesktop.app;

import com.marcosmoreiradev.uensdesktop.common.constants.AppConstants;
import com.marcosmoreiradev.uensdesktop.ui.theme.TypographyManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javafx.stage.Stage;

final class FrontendLogReportExporter {

    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final DateTimeFormatter HUMAN_TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Path LOG_DIRECTORY = Paths.get("logs");
    private static final Path APPLICATION_LOG = LOG_DIRECTORY.resolve("uens-desktop.log");
    private static final int LOG_TAIL_LINES = 120;

    private FrontendLogReportExporter() {
    }

    static Path export(AppContext appContext, Stage stage, Path targetPath) {
        LocalDateTime now = LocalDateTime.now();
        Path reportPath = normalizeTargetPath(targetPath, defaultFileName(now));
        try {
            if (reportPath.getParent() != null) {
                Files.createDirectories(reportPath.getParent());
            }
            Files.writeString(reportPath, buildReport(appContext, stage, now), StandardCharsets.UTF_8);
            return reportPath;
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo exportar el reporte frontend", ex);
        }
    }

    static Path defaultExportDirectory() {
        Path desktop = Paths.get(System.getProperty("user.home"), "Desktop");
        if (Files.isDirectory(desktop)) {
            return desktop;
        }
        return Paths.get(System.getProperty("user.home"));
    }

    static String defaultFileName() {
        return defaultFileName(LocalDateTime.now());
    }

    private static String buildReport(AppContext appContext, Stage stage, LocalDateTime generatedAt) {
        String currentView = appContext.navigator().currentViewProperty().get() == null
                ? "DESCONOCIDA"
                : appContext.navigator().currentViewProperty().get().name();
        String sessionLogin = appContext.sessionState().usuario()
                .map(usuario -> usuario.login() + " (" + usuario.rol().name() + ")")
                .orElse("SIN SESI\u00d3N");

        StringBuilder report = new StringBuilder();
        report.append("# Reporte de logs del frontend").append(System.lineSeparator()).append(System.lineSeparator());
        report.append("- Aplicaci\u00f3n: ").append(AppConstants.APP_NAME).append(System.lineSeparator());
        report.append("- Generado: ").append(HUMAN_TIMESTAMP.format(generatedAt)).append(System.lineSeparator());
        report.append("- Vista activa: ").append(currentView).append(System.lineSeparator());
        report.append("- Sesi\u00f3n: ").append(sessionLogin).append(System.lineSeparator());
        report.append("- Backend baseUrl: ").append(appContext.apiConfig().baseUrl()).append(System.lineSeparator());
        report.append("- Tipograf\u00eda UI: ").append(TypographyManager.activeUiFontFamily()).append(System.lineSeparator());
        report.append("- Tipograf\u00eda mono: ").append(TypographyManager.activeMonoFontFamily()).append(System.lineSeparator());
        report.append("- Perfil tipogr\u00e1fico: ").append(TypographyManager.activeProfile().name()).append(System.lineSeparator());
        report.append("- Ventana: ")
                .append((int) stage.getWidth()).append("x").append((int) stage.getHeight())
                .append(stage.isFullScreen() ? " (pantalla completa)" : "")
                .append(System.lineSeparator());
        report.append("- Sistema: ").append(System.getProperty("os.name")).append(" ").append(System.getProperty("os.version"))
                .append(System.lineSeparator());
        report.append("- Java: ").append(System.getProperty("java.version")).append(System.lineSeparator()).append(System.lineSeparator());

        report.append("## Observaci\u00f3n").append(System.lineSeparator()).append(System.lineSeparator());
        report.append("Reporte generado manualmente desde la UI con la combinaci\u00f3n `Ctrl + Shift + L` para soporte, diagn\u00f3stico o auditor\u00eda interna.")
                .append(System.lineSeparator()).append(System.lineSeparator());

        report.append("## \u00daltimas l\u00edneas del log").append(System.lineSeparator()).append(System.lineSeparator());
        report.append("```text").append(System.lineSeparator());
        report.append(readLogTail());
        if (!report.toString().endsWith(System.lineSeparator())) {
            report.append(System.lineSeparator());
        }
        report.append("```").append(System.lineSeparator());
        return report.toString();
    }

    private static String defaultFileName(LocalDateTime now) {
        return "frontend-log-report-" + FILE_TIMESTAMP.format(now) + ".md";
    }

    private static Path normalizeTargetPath(Path targetPath, String fallbackFileName) {
        if (targetPath == null) {
            return defaultExportDirectory().resolve(fallbackFileName);
        }
        String fileName = targetPath.getFileName() == null ? "" : targetPath.getFileName().toString().trim();
        if (fileName.isBlank()) {
            return targetPath.resolve(fallbackFileName);
        }
        if (!fileName.contains(".")) {
            return targetPath.resolveSibling(fileName + ".md");
        }
        return targetPath;
    }

    private static String readLogTail() {
        if (!Files.exists(APPLICATION_LOG)) {
            return "No existe a\u00fan el archivo logs/uens-desktop.log";
        }
        try {
            List<String> lines = Files.readAllLines(APPLICATION_LOG, StandardCharsets.UTF_8);
            return lines.stream()
                    .skip(Math.max(0, lines.size() - LOG_TAIL_LINES))
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException ex) {
            return "No se pudo leer logs/uens-desktop.log: " + ex.getMessage();
        }
    }
}
