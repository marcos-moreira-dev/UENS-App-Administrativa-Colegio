package com.marcosmoreiradev.uensbackend.modules.auditoria.infrastructure.persistence.repository;

import com.marcosmoreiradev.uensbackend.modules.auditoria.infrastructure.persistence.entity.AuditoriaEventoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditoriaEventoJpaRepository extends JpaRepository<AuditoriaEventoJpaEntity, Long>, JpaSpecificationExecutor<AuditoriaEventoJpaEntity> {
}

