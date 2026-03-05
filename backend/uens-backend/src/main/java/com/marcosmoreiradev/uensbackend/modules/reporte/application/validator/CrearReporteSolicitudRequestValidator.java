package com.marcosmoreiradev.uensbackend.modules.reporte.application.validator;

import com.marcosmoreiradev.uensbackend.common.exception.base.ValidationException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ApiErrorCodes;
import com.marcosmoreiradev.uensbackend.modules.reporte.api.dto.CrearReporteSolicitudRequestDto;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
/**
 * Define la responsabilidad de CrearReporteSolicitudRequestValidator dentro del backend UENS.
 * Contexto: modulo reporte, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
public class CrearReporteSolicitudRequestValidator {

/**
 * Implementa la operacion 'validar' del modulo reporte en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param request payload de entrada validado desde la API con datos del caso de uso
 */
    public void validar(CrearReporteSolicitudRequestDto request) {
        String tipo = request.tipoReporte() == null ? null : request.tipoReporte().trim().toUpperCase(Locale.ROOT);
        if ("LISTADO_ESTUDIANTES_POR_SECCION".equals(tipo) && request.seccionId() == null) {
            throw new ValidationException(ApiErrorCodes.VR_07_CAMPO_REQUERIDO, "SeccionId es obligatorio para LISTADO_ESTUDIANTES_POR_SECCION.");
        }
        if ("CALIFICACIONES_POR_SECCION_Y_PARCIAL".equals(tipo)) {
            if (request.seccionId() == null) {
                throw new ValidationException(ApiErrorCodes.VR_07_CAMPO_REQUERIDO, "SeccionId es obligatorio para CALIFICACIONES_POR_SECCION_Y_PARCIAL.");
            }
            if (request.numeroParcial() == null) {
                throw new ValidationException(ApiErrorCodes.VR_07_CAMPO_REQUERIDO, "NumeroParcial es obligatorio para CALIFICACIONES_POR_SECCION_Y_PARCIAL.");
            }
        }
        if (request.fechaDesde() != null && request.fechaHasta() != null && request.fechaDesde().isAfter(request.fechaHasta())) {
            throw new ValidationException(ApiErrorCodes.VR_02_PARAMETRO_INVALIDO, "FechaDesde no puede ser mayor que FechaHasta.");
        }
    }
}
