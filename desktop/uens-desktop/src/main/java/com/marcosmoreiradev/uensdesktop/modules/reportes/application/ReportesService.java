package com.marcosmoreiradev.uensdesktop.modules.reportes.application;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.client.BinaryPayload;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.ReportesApi;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudCreatedResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudDetailResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudResultResponseDto;
import com.marcosmoreiradev.uensdesktop.common.error.ErrorInfo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ReportesService {

    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final ReportesApi reportesApi;

    public ReportesService(ReportesApi reportesApi) {
        this.reportesApi = reportesApi;
    }

    public ApiResult<ReporteSolicitudCreatedResponseDto> crear(ReporteSolicitudCreateRequestDto request) {
        return reportesApi.crear(request);
    }

    public ApiResult<PageResponse<ReporteSolicitudListItemDto>> listar(
            int page,
            int size,
            String query,
            String tipoReporte,
            String estado) {
        return reportesApi.listar(page, size, query, tipoReporte, estado);
    }

    public ApiResult<PageResponse<ReporteSolicitudListItemDto>> listar(ReportesListQuery query) {
        return listar(
                query.page(),
                query.size(),
                query.query(),
                query.tipoReporte(),
                query.estado());
    }

    public ApiResult<ReporteSolicitudDetailResponseDto> obtenerDetalle(long solicitudId) {
        return reportesApi.obtenerDetalle(solicitudId);
    }

    public ApiResult<ReporteSolicitudResultResponseDto> obtenerEstado(long solicitudId) {
        return reportesApi.obtenerEstado(solicitudId);
    }

    public ApiResult<ReporteSolicitudResultResponseDto> obtenerResultado(long solicitudId) {
        return reportesApi.obtenerResultado(solicitudId);
    }

    public ApiResult<ReporteSolicitudCreatedResponseDto> reintentar(long solicitudId) {
        return reportesApi.reintentar(solicitudId);
    }

    public ApiResult<ReporteDownloadResult> descargarArchivo(long solicitudId, String tipoReporte) {
        ApiResult<BinaryPayload> result = reportesApi.descargarArchivo(solicitudId);
        if (!result.isSuccess()) {
            return ApiResult.failure(result.error().orElseThrow());
        }

        BinaryPayload payload = result.data().orElseThrow();
        try {
            Path outputDirectory = resolveDownloadsDirectory();
            Files.createDirectories(outputDirectory);

            String fileName = sanitizeDownloadFileName(payload.fileName());
            if (fileName == null || fileName.isBlank()) {
                fileName = buildFallbackFileName(tipoReporte, solicitudId, extensionFromContentType(payload.contentType()));
            }

            Path outputPath = outputDirectory.resolve(fileName);
            Files.write(outputPath, payload.bytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            return ApiResult.success(new ReporteDownloadResult(outputPath, fileName, payload.contentType()));
        } catch (IOException ex) {
            return ApiResult.failure(new ErrorInfo(
                    com.marcosmoreiradev.uensdesktop.common.error.ErrorCategory.UNKNOWN,
                    "No se pudo guardar el archivo descargado.",
                    null,
                    null));
        }
    }

    private Path resolveDownloadsDirectory() {
        return Path.of(System.getProperty("user.home"), "Downloads", "UENS");
    }

    static String sanitizeDownloadFileName(String fileName) {
        if (fileName == null) {
            return null;
        }
        String sanitized = fileName.trim()
                .replace("\\", "_")
                .replace("/", "_")
                .replace(":", "_")
                .replace("*", "_")
                .replace("?", "_")
                .replace("\"", "_")
                .replace("<", "_")
                .replace(">", "_")
                .replace("|", "_")
                .replace("\r", "_")
                .replace("\n", "_");
        return sanitized.isBlank() ? null : sanitized;
    }

    private String buildFallbackFileName(String tipoReporte, long solicitudId, String extension) {
        String sanitizedType = tipoReporte == null || tipoReporte.isBlank() ? "REPORTE" : tipoReporte.trim();
        return sanitizedType + "_" + solicitudId + "_" + LocalDateTime.now().format(FILE_TIMESTAMP) + extension;
    }

    private String extensionFromContentType(String contentType) {
        if (contentType == null) {
            return ".bin";
        }
        if (contentType.contains("pdf")) {
            return ".pdf";
        }
        if (contentType.contains("spreadsheetml") || contentType.contains("sheet")) {
            return ".xlsx";
        }
        if (contentType.contains("wordprocessingml") || contentType.contains("docx")) {
            return ".docx";
        }
        return ".bin";
    }
}
