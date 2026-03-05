package com.marcosmoreiradev.uensbackend.modules.reporte.application.port;

import java.nio.file.Path;

/**
 * Estrategia de escritura usada por el almacenamiento documental para delegar
 * la materializacion concreta del contenido al generador correspondiente.
 */
@FunctionalInterface
public interface DocumentContentWriter {

    /**
     * Escribe el documento en la ubicacion fisica temporal o definitiva
     * seleccionada por el adapter de almacenamiento.
     *
     * @param targetPath archivo donde debe escribirse el contenido
     * @throws Exception cuando la generacion o escritura falla
     */
    void writeTo(Path targetPath) throws Exception;
}
