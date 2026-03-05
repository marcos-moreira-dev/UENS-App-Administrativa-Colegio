package com.marcosmoreiradev.uens_backend.modules.usuario.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.BusinessRuleException;
import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.UsuarioErrorCodes;
import com.marcosmoreiradev.uensbackend.modules.usuario.api.dto.UsuarioSistemaAdministrativoCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.usuario.api.dto.UsuarioSistemaAdministrativoPatchEstadoRequestDto;
import com.marcosmoreiradev.uensbackend.modules.usuario.api.dto.UsuarioSistemaAdministrativoResponseDto;
import com.marcosmoreiradev.uensbackend.modules.usuario.api.dto.UsuarioSistemaAdministrativoUpdateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.usuario.application.UsuarioCommandService;
import com.marcosmoreiradev.uensbackend.modules.usuario.application.mapper.UsuarioSistemaAdministrativoDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.usuario.application.port.UsuarioPasswordService;
import com.marcosmoreiradev.uensbackend.modules.usuario.application.validator.UsuarioSistemaAdministrativoRequestValidator;
import com.marcosmoreiradev.uensbackend.modules.usuario.infrastructure.persistence.entity.UsuarioSistemaAdministrativoJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.usuario.infrastructure.persistence.repository.UsuarioSistemaAdministrativoJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioCommandServiceTest {

    @Mock
    private UsuarioSistemaAdministrativoJpaRepository repository;
    @Mock
    private UsuarioPasswordService usuarioPasswordService;

    private UsuarioCommandService service;

    @BeforeEach
    void setUp() {
        service = new UsuarioCommandService(
                repository,
                new UsuarioSistemaAdministrativoDtoMapper(),
                new UsuarioSistemaAdministrativoRequestValidator(),
                usuarioPasswordService
        );
    }

    @Test
    void crearFailsWhenLoginDuplicado() {
        UsuarioSistemaAdministrativoCreateRequestDto request =
                new UsuarioSistemaAdministrativoCreateRequestDto(" admin ", "Clave1234", "ADMIN", "ACTIVO");
        when(repository.existsByNombreLoginIgnoreCase("admin")).thenReturn(true);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(request));

        assertEquals(UsuarioErrorCodes.RN_USR_01_LOGIN_DUPLICADO, ex.getErrorCode());
    }

    @Test
    void crearHashesPasswordAndNormalizesUserData() {
        UsuarioSistemaAdministrativoCreateRequestDto request =
                new UsuarioSistemaAdministrativoCreateRequestDto(" admin ", "Clave1234", "secretaria", "activo");
        when(repository.existsByNombreLoginIgnoreCase("admin")).thenReturn(false);
        when(usuarioPasswordService.hash("Clave1234")).thenReturn("hashed-password");
        when(repository.save(any(UsuarioSistemaAdministrativoJpaEntity.class))).thenAnswer(invocation -> {
            UsuarioSistemaAdministrativoJpaEntity entity = invocation.getArgument(0);
            ReflectionTestUtils.setField(entity, "id", 7L);
            return entity;
        });

        UsuarioSistemaAdministrativoResponseDto result = service.crear(request);

        ArgumentCaptor<UsuarioSistemaAdministrativoJpaEntity> captor =
                ArgumentCaptor.forClass(UsuarioSistemaAdministrativoJpaEntity.class);
        verify(repository).save(captor.capture());

        UsuarioSistemaAdministrativoJpaEntity saved = captor.getValue();
        assertEquals("admin", saved.getNombreLogin());
        assertEquals("hashed-password", saved.getPasswordHash());
        assertEquals("SECRETARIA", saved.getRol());
        assertEquals("ACTIVO", saved.getEstado());
        assertEquals(7L, result.id());
        assertEquals("admin", result.nombreLogin());
        assertEquals("SECRETARIA", result.rol());
    }

    @Test
    void actualizarAppliesIdentityStateAndPassword() {
        UsuarioSistemaAdministrativoJpaEntity entity =
                UsuarioSistemaAdministrativoJpaEntity.crear("legacy", "old-hash", "ADMIN", "ACTIVO");
        ReflectionTestUtils.setField(entity, "id", 15L);

        UsuarioSistemaAdministrativoUpdateRequestDto request =
                new UsuarioSistemaAdministrativoUpdateRequestDto(" secretaria ", "NuevaClave123", "secretaria", "inactivo");

        when(repository.findById(15L)).thenReturn(Optional.of(entity));
        when(repository.existsByNombreLoginIgnoreCaseAndIdNot("secretaria", 15L)).thenReturn(false);
        when(usuarioPasswordService.hash("NuevaClave123")).thenReturn("new-hash");
        when(repository.save(any(UsuarioSistemaAdministrativoJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioSistemaAdministrativoResponseDto result = service.actualizar(15L, request);

        assertEquals("secretaria", entity.getNombreLogin());
        assertEquals("new-hash", entity.getPasswordHash());
        assertEquals("SECRETARIA", entity.getRol());
        assertEquals("INACTIVO", entity.getEstado());
        assertEquals(15L, result.id());
        assertEquals("INACTIVO", result.estado());
    }

    @Test
    void cambiarEstadoFailsWhenUsuarioNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.cambiarEstado(99L, new UsuarioSistemaAdministrativoPatchEstadoRequestDto("ACTIVO"))
        );
    }
}
