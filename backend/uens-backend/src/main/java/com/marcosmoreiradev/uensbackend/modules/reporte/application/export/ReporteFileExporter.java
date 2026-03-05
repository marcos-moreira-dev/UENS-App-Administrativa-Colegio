package com.marcosmoreiradev.uensbackend.modules.reporte.application.export;

import com.marcosmoreiradev.uensbackend.modules.reporte.application.export.model.ReporteDocumentModel;

import java.nio.file.Path;

/**
 * Contrato para exportadores de archivo en distintos formatos de salida.
 */
public interface ReporteFileExporter {

    /**
     * Indica si el exportador soporta el formato solicitado.
     *
     * @param formato formato de salida normalizado (por ejemplo, XLSX, PDF, DOCX)
     * @return {@code true} si el exportador puede procesar el formato
     */
    boolean soporta(String formato);

    /**
     * Devuelve la extension de archivo asociada al exportador.
     *
     * @return extension sin punto, por ejemplo {@code xlsx}
     */
    String extension();

    /**
     * Devuelve el tipo MIME de respuesta para el formato soportado.
     *
     * @return MIME type del archivo generado
     */
    String mimeType();

    /**
     * Escribe el contenido del reporte en un archivo fisico.
     *
     * @param outputFile ruta absoluta del archivo de salida
     * @param documentModel modelo de documento listo para renderizar
     * @throws IllegalStateException cuando no es posible generar el archivo
     */
    void exportar(Path outputFile, ReporteDocumentModel documentModel);
}
