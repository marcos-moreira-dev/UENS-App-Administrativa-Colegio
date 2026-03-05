package com.marcosmoreiradev.uensdesktop.modules.auditoria.application;

import java.time.LocalDate;

public record AuditoriaEventosQuery(
        int page,
        int size,
        String query,
        String modulo,
        String accion,
        String resultado,
        String actorLogin,
        LocalDate fechaDesde,
        LocalDate fechaHasta) {
}
