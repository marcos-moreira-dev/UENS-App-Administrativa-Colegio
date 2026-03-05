package com.marcosmoreiradev.uensbackend.modules.auditoria.application.mapper;

import com.marcosmoreiradev.uensbackend.modules.auditoria.api.dto.AuditoriaEventoListItemDto;
import com.marcosmoreiradev.uensbackend.modules.auditoria.infrastructure.persistence.entity.AuditoriaEventoJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaDtoMapper {

    public AuditoriaEventoListItemDto toListItemDto(AuditoriaEventoJpaEntity entity) {
        return new AuditoriaEventoListItemDto(
                entity.getId(),
                entity.getModulo(),
                entity.getAccion(),
                entity.getEntidad(),
                entity.getEntidadId(),
                entity.getResultado(),
                entity.getActorLogin(),
                entity.getActorRol(),
                entity.getRequestId(),
                entity.getIpOrigen(),
                entity.getFechaEvento()
        );
    }
}

