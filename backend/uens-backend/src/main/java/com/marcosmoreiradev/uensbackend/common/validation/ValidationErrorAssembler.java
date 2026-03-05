package com.marcosmoreiradev.uensbackend.common.validation;

import com.marcosmoreiradev.uensbackend.common.api.response.ErrorDetailDto;
import jakarta.validation.ConstraintViolation;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
/**
 * Define la responsabilidad de ValidationErrorAssembler dentro del backend UENS.
 * Contexto: modulo core, capa common, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
public class ValidationErrorAssembler {

/**
 * Implementa la operacion 'fromBindingResult' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param bindingResult dato de entrada relevante para ejecutar esta operacion: 'bindingResult'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public List<ErrorDetailDto> fromBindingResult(BindingResult bindingResult) {
        List<ErrorDetailDto> details = new ArrayList<>();

        bindingResult.getFieldErrors().forEach(fe -> details.add(ErrorDetailDto.of(
                fe.getField(),
                fe.getCode(),
                fe.getDefaultMessage(),
                fe.getRejectedValue()
        )));

        bindingResult.getGlobalErrors().forEach(ge -> details.add(ErrorDetailDto.of(
                ge.getObjectName(),
                ge.getCode(),
                ge.getDefaultMessage()
        )));

        return details;
    }

/**
 * Implementa la operacion 'fromConstraintViolations' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param violations dato de entrada relevante para ejecutar esta operacion: 'violations'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public List<ErrorDetailDto> fromConstraintViolations(Set<? extends ConstraintViolation<?>> violations) {
        if (violations == null || violations.isEmpty()) {
            return List.of();
        }

        return violations.stream()
                .map(cv -> ErrorDetailDto.of(
                        cv.getPropertyPath() != null ? cv.getPropertyPath().toString() : null,
                        "ConstraintViolation",
                        cv.getMessage(),
                        cv.getInvalidValue()
                ))
                .toList();
    }
}

