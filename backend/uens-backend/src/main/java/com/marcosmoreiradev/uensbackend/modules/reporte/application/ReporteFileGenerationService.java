package com.marcosmoreiradev.uensbackend.modules.reporte.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.InfrastructureException;
import com.marcosmoreiradev.uensbackend.config.properties.ReportOutputProperties;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ReporteErrorCodes;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.model.StoredDocument;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.export.assembly.ReporteDocumentModelAssembler;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.export.assembly.ReporteDocumentModelAssemblerSelector;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.export.ReporteArchivoGenerado;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.export.ReporteFileExporter;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.export.ReporteFileExporterSelector;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.export.model.ReporteDocumentModel;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity.ReporteSolicitudQueueJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.port.DocumentStoragePort;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Servicio de aplicacion para materializar el payload de un reporte en archivo fisico.
 */
@Service
public class ReporteFileGenerationService {

    private static final Logger log = LoggerFactory.getLogger(ReporteFileGenerationService.class);
    private static final DateTimeFormatter FILE_TS_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final ReportOutputProperties properties;
    private final ReporteDocumentModelAssemblerSelector documentAssemblerSelector;
    private final ReporteFileExporterSelector exporterSelector;
    private final DocumentStoragePort documentStoragePort;

    /**
     * Crea el servicio con sus dependencias de configuracion y seleccion de exportador.
     *
     * @param properties propiedades de salida y limites de archivos
     * @param documentAssemblerSelector selector de presentacion por tipo de reporte
     * @param exporterSelector selector de exportador por formato
     * @param documentStoragePort repositorio documental desacoplado del proveedor concreto
     */
    public ReporteFileGenerationService(
            ReportOutputProperties properties,
            ReporteDocumentModelAssemblerSelector documentAssemblerSelector,
            ReporteFileExporterSelector exporterSelector,
            DocumentStoragePort documentStoragePort
    ) {
        this.properties = properties;
        this.documentAssemblerSelector = documentAssemblerSelector;
        this.exporterSelector = exporterSelector;
        this.documentStoragePort = documentStoragePort;
    }

    /**
     * Genera el archivo de reporte y devuelve su metadata.
     *
     * @param solicitud entidad de solicitud en cola, usada para nombrado y trazabilidad
     * @param payload contenido estructurado del reporte
     * @param formatoSalida formato solicitado (XLSX, PDF, DOCX)
     * @return metadata del archivo generado
     * @throws InfrastructureException cuando ocurre un error de exportacion o validacion tecnica
     */
    public ReporteArchivoGenerado generar(ReporteSolicitudQueueJpaEntity solicitud, Object payload, String formatoSalida) {
        try {
            ReporteDocumentModelAssembler assembler = documentAssemblerSelector.seleccionar(solicitud.getTipoReporte());
            ReporteDocumentModel documentModel = assembler.assemble(solicitud, payload);
            ReporteFileExporter exporter = exporterSelector.seleccionar(formatoSalida);

            String baseName = sanitize("reporte_" + solicitud.getTipoReporte() + "_" + solicitud.getId() + "_" + FILE_TS_FORMATTER.format(LocalDateTime.now()));
            String fileName = baseName + "." + exporter.extension();
            StoredDocument storedDocument = documentStoragePort.store(
                    "reportes",
                    fileName,
                    exporter.mimeType(),
                    outputFile -> exporter.exportar(outputFile, documentModel)
            );
            validateMaxSize(storedDocument.documentKey(), storedDocument.sizeBytes());

            return new ReporteArchivoGenerado(
                    fileName,
                    storedDocument.documentKey(),
                    exporter.mimeType(),
                    normalizeFormato(formatoSalida),
                    storedDocument.sizeBytes(),
                    LocalDateTime.now()
            );
        } catch (Exception ex) {
            throw new InfrastructureException(
                    ReporteErrorCodes.SYS_REP_02_FALLO_PROCESAMIENTO,
                    "No fue posible generar el archivo del reporte.",
                    ex
            );
        }
    }

    /**
     * Elimina el archivo generado cuando la persistencia asociada falla y se requiere compensacion.
     *
     * @param archivo metadata del archivo previamente materializado
     */
    public void eliminarSilenciosamente(ReporteArchivoGenerado archivo) {
        if (archivo == null || archivo.rutaRelativa() == null || archivo.rutaRelativa().isBlank()) {
            return;
        }

        try {
            documentStoragePort.delete(archivo.rutaRelativa());
        } catch (Exception ex) {
            log.warn("No fue posible eliminar archivo de reporte durante compensacion: {}", ex.getMessage(), ex);
        }
    }

    private void validateMaxSize(String documentKey, long sizeBytes) {
        long maxBytes = (long) properties.maxFileMb() * 1024L * 1024L;
        if (maxBytes > 0 && sizeBytes > maxBytes) {
            documentStoragePort.delete(documentKey);
            throw new InfrastructureException(
                    ReporteErrorCodes.SYS_REP_02_FALLO_PROCESAMIENTO,
                    "El archivo generado supera el tamano maximo permitido.",
                    null
            );
        }
    }

    private static String normalizeFormato(String formatoSalida) {
        if (formatoSalida == null || formatoSalida.isBlank()) {
            return "XLSX";
        }
        return formatoSalida.trim().toUpperCase(Locale.ROOT);
    }

    private static String sanitize(String value) {
        return value.replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
