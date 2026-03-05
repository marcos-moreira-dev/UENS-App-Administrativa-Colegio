package com.marcosmoreiradev.uensbackend.modules.reporte.application.processor;

import com.marcosmoreiradev.uensbackend.common.exception.base.ApplicationException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ReporteErrorCodes;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
/**
 * Define la responsabilidad de ReporteDataProcessorSelector dentro del backend UENS.
 * Contexto: modulo reporte, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
public class ReporteDataProcessorSelector {

    private final List<ReporteDataProcessor> processors;
/**
 * Construye la instancia de ReporteDataProcessorSelector para operar en el modulo reporte.
 * Contexto: capa application con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param processors dato de entrada relevante para ejecutar esta operacion: 'processors'
 */

    public ReporteDataProcessorSelector(List<ReporteDataProcessor> processors) {
        this.processors = processors;
    }

/**
 * Implementa la operacion 'seleccionar' del modulo reporte en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param tipoReporte tipo funcional de reporte solicitado para procesamiento asincrono
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ReporteDataProcessor seleccionar(String tipoReporte) {
        return processors.stream()
                .filter(p -> p.soporta(tipoReporte))
                .findFirst()
                .orElseThrow(() -> new ApplicationException(
                        ReporteErrorCodes.RN_REP_01_TIPO_NO_HABILITADO_V1,
                        "El tipo de reporte solicitado aun no esta habilitado en V1."
                ));
    }
}
