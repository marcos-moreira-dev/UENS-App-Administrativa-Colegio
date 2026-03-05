package com.marcosmoreiradev.uensbackend.modules.auditoria.api.dto;

import java.time.LocalDateTime;

public record AuditoriaEventoListItemDto(
        Long eventoId,
        String modulo,
        String accion,
        String entidad,
        String entidadId,
        String resultado,
        String actorLogin,
        String actorRol,
        String requestId,
        String ipOrigen,
        LocalDateTime fechaEvento
) {
}

