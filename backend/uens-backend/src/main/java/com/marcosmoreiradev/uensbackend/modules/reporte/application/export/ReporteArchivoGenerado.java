package com.marcosmoreiradev.uensbackend.modules.reporte.application.export;

import java.time.LocalDateTime;

/**
 * Resultado de la generacion de un archivo de reporte.
 *
 * @param nombreArchivo nombre de archivo visible para descarga
 * @param rutaRelativa ruta relativa del archivo dentro del directorio configurado
 * @param mimeType tipo MIME del archivo generado
 * @param formato formato de salida solicitado (XLSX, PDF, DOCX)
 * @param tamanoBytes tamano final del archivo en bytes
 * @param generadoEn fecha y hora de generacion del archivo
 */
public record ReporteArchivoGenerado(
        String nombreArchivo,
        String rutaRelativa,
        String mimeType,
        String formato,
        long tamanoBytes,
        LocalDateTime generadoEn
) {
}
