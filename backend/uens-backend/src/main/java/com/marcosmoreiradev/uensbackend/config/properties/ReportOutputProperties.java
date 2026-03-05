package com.marcosmoreiradev.uensbackend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Propiedades de configuracion para la salida de archivos de reportes.
 *
 * @param dir directorio base donde se escriben los archivos generados
 * @param publicBaseUrl URL publica opcional para construir enlaces externos
 * @param fileTtlDays dias de retencion sugeridos para limpieza de archivos
 * @param maxFileMb tamano maximo permitido por archivo en megabytes
 */
@ConfigurationProperties(prefix = "app.report.output")
public record ReportOutputProperties(
        @DefaultValue("reportes-output") String dir,
        @DefaultValue("") String publicBaseUrl,
        @DefaultValue("30") int fileTtlDays,
        @DefaultValue("20") int maxFileMb
) {
}
