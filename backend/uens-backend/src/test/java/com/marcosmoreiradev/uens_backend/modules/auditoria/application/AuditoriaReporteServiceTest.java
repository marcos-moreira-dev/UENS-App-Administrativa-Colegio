package com.marcosmoreiradev.uens_backend.modules.auditoria.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.AuthException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.AuthErrorCodes;
import com.marcosmoreiradev.uensbackend.modules.auditoria.api.dto.CrearAuditoriaReporteRequestDto;
import com.marcosmoreiradev.uensbackend.modules.auditoria.application.AuditoriaEventService;
import com.marcosmoreiradev.uensbackend.modules.auditoria.application.AuditoriaReporteService;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.ReporteSolicitudCommandService;
import com.marcosmoreiradev.uensbackend.security.user.CurrentAuthenticatedUserService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class AuditoriaReporteServiceTest {

    @Test
    void solicitarReporteRejectsNonAdminAtServiceLevel() {
        ReporteSolicitudCommandService reporteSolicitudCommandService = mock(ReporteSolicitudCommandService.class);
        CurrentAuthenticatedUserService currentAuthenticatedUserService = mock(CurrentAuthenticatedUserService.class);
        doThrow(new AuthException(AuthErrorCodes.AUTH_04_SIN_PERMISOS, "Solo ADMIN puede solicitar reportes de auditoria."))
                .when(currentAuthenticatedUserService)
                .ensureAdmin("Solo ADMIN puede solicitar reportes de auditoria.");

        AuditoriaReporteService service = new AuditoriaReporteService(
                reporteSolicitudCommandService,
                mock(AuditoriaEventService.class),
                currentAuthenticatedUserService
        );

        assertThatThrownBy(() -> service.solicitarReporte(new CrearAuditoriaReporteRequestDto(
                "PDF",
                null,
                null,
                null,
                null,
                null,
                null,
                true
        )))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCodes.AUTH_04_SIN_PERMISOS);

        verifyNoInteractions(reporteSolicitudCommandService);
    }
}
