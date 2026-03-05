package com.marcosmoreiradev.uensbackend.modules.reporte.application.export.assembly;

import com.marcosmoreiradev.uensbackend.modules.reporte.application.export.model.ReporteDocumentModel;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity.ReporteSolicitudQueueJpaEntity;

/**
 * Strategy que transforma el payload de un tipo de reporte en un modelo de
 * documento tabular y legible para PDF, Word y Excel.
 */
public interface ReporteDocumentModelAssembler {

    /**
     * Indica si esta implementacion soporta el tipo de reporte solicitado.
     *
     * @param tipoReporte tipo funcional del reporte
     * @return {@code true} cuando el assembler puede construir el documento
     */
    boolean soporta(String tipoReporte);

    /**
     * Construye el modelo intermedio de documento a partir del payload ya
     * procesado por la capa application.
     *
     * @param solicitud solicitud que origino el documento
     * @param payload payload procesado y listo para presentacion
     * @return modelo de documento normalizado para exportadores
     */
    ReporteDocumentModel assemble(ReporteSolicitudQueueJpaEntity solicitud, Object payload);
}
