package com.marcosmoreiradev.uens_backend.modules.auditoria.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.AuthException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.AuthErrorCodes;
import com.marcosmoreiradev.uensbackend.common.pagination.PageableFactory;
import com.marcosmoreiradev.uensbackend.modules.auditoria.application.AuditoriaQueryService;
import com.marcosmoreiradev.uensbackend.modules.auditoria.application.mapper.AuditoriaDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.auditoria.infrastructure.persistence.repository.AuditoriaEventoJpaRepository;
import com.marcosmoreiradev.uensbackend.security.user.CurrentAuthenticatedUserService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class AuditoriaQueryServiceTest {

    @Test
    void listarRejectsNonAdminAtServiceLevel() {
        AuditoriaEventoJpaRepository repository = mock(AuditoriaEventoJpaRepository.class);
        CurrentAuthenticatedUserService currentAuthenticatedUserService = mock(CurrentAuthenticatedUserService.class);
        doThrow(new AuthException(AuthErrorCodes.AUTH_04_SIN_PERMISOS, "Solo ADMIN puede consultar eventos de auditoria."))
                .when(currentAuthenticatedUserService)
                .ensureAdmin("Solo ADMIN puede consultar eventos de auditoria.");

        AuditoriaQueryService service = new AuditoriaQueryService(
                repository,
                new AuditoriaDtoMapper(),
                mock(PageableFactory.class),
                currentAuthenticatedUserService
        );

        assertThatThrownBy(() -> service.listar(null, null, null, null, null, null, null, 0, 20, null))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCodes.AUTH_04_SIN_PERMISOS);

        verifyNoInteractions(repository);
    }
}
