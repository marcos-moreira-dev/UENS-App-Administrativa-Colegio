package com.marcosmoreiradev.uensbackend.modules.reporte.application.export.assembly;

import com.marcosmoreiradev.uensbackend.common.exception.base.ApplicationException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ReporteErrorCodes;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Resuelve el assembler de presentacion adecuado segun el tipo de reporte.
 */
@Component
public class ReporteDocumentModelAssemblerSelector {

    private final List<ReporteDocumentModelAssembler> assemblers;

    public ReporteDocumentModelAssemblerSelector(List<ReporteDocumentModelAssembler> assemblers) {
        this.assemblers = assemblers;
    }

    public ReporteDocumentModelAssembler seleccionar(String tipoReporte) {
        return assemblers.stream()
                .filter(assembler -> assembler.soporta(tipoReporte))
                .findFirst()
                .orElseThrow(() -> new ApplicationException(
                        ReporteErrorCodes.RN_REP_01_TIPO_NO_HABILITADO_V1,
                        "No existe un ensamblador de documento para el tipo de reporte solicitado."
                ));
    }
}
