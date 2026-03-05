package com.marcosmoreiradev.uensbackend.modules.usuario.infrastructure.persistence.repository;

import com.marcosmoreiradev.uensbackend.modules.usuario.infrastructure.persistence.entity.UsuarioSistemaAdministrativoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio base para lectura y escritura de usuarios administrativos.
 */
@Repository
public interface UsuarioSistemaAdministrativoJpaRepository extends
        JpaRepository<UsuarioSistemaAdministrativoJpaEntity, Long>,
        JpaSpecificationExecutor<UsuarioSistemaAdministrativoJpaEntity> {

    Optional<UsuarioSistemaAdministrativoJpaEntity> findByNombreLoginIgnoreCase(String nombreLogin);

    boolean existsByNombreLoginIgnoreCase(String nombreLogin);

    boolean existsByNombreLoginIgnoreCaseAndIdNot(String nombreLogin, Long id);
}
