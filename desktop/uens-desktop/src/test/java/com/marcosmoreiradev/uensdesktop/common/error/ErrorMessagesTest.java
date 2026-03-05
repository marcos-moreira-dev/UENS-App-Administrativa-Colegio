package com.marcosmoreiradev.uensdesktop.common.error;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ErrorMessagesTest {

    @Test
    void userFacingMessageReturnsFallbackWhenErrorInfoIsMissing() {
        assertThat(ErrorMessages.userFacingMessage(null, "Mensaje genérico"))
                .isEqualTo("Mensaje genérico");
    }

    @Test
    void userFacingMessageAppendsRequestIdForServerSideCategories() {
        ErrorInfo errorInfo = new ErrorInfo(
                ErrorCategory.SERVER,
                "Falló la generación del reporte",
                "REPORT_ERROR",
                "req-77");

        assertThat(ErrorMessages.userFacingMessage(errorInfo, "Mensaje genérico"))
                .isEqualTo("Falló la generación del reporte RequestId: req-77");
    }

    @Test
    void userFacingMessageDoesNotAppendRequestIdForValidationErrors() {
        ErrorInfo errorInfo = new ErrorInfo(
                ErrorCategory.VALIDATION,
                "La nota es obligatoria",
                "VALIDATION_ERROR",
                "req-11");

        assertThat(ErrorMessages.userFacingMessage(errorInfo, "Mensaje genérico"))
                .isEqualTo("La nota es obligatoria");
    }

    @Test
    void userFacingMessageUsesFallbackWhenBackendMessageIsBlank() {
        ErrorInfo errorInfo = new ErrorInfo(
                ErrorCategory.CONFLICT,
                " ",
                "CONFLICT",
                "req-19");

        assertThat(ErrorMessages.userFacingMessage(errorInfo, "No se pudo guardar"))
                .isEqualTo("No se pudo guardar RequestId: req-19");
    }
}
