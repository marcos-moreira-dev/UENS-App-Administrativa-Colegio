package com.marcosmoreiradev.uensdesktop.modules.auditoria.presenter;

import com.marcosmoreiradev.uensdesktop.api.modules.auditoria.dto.AuditoriaEventoListItemDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class AuditoriaPresenter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public AuditoriaEventDetailPresentation presentDetail(AuditoriaEventoListItemDto evento) {
        return new AuditoriaEventDetailPresentation(
                formatText(evento.modulo()),
                formatText(evento.accion()),
                formatText(evento.resultado()),
                formatText(evento.entidad()),
                formatText(evento.entidadId()),
                formatActor(evento.actorLogin(), evento.actorRol()),
                formatText(evento.requestId()),
                formatText(evento.ipOrigen()),
                formatDateTime(evento.fechaEvento()));
    }

    public String formatDateTime(LocalDateTime value) {
        return value == null ? "-" : value.format(DATE_TIME_FORMATTER);
    }

    public String formatText(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    public String formatActor(String actorLogin, String actorRol) {
        return formatText(actorLogin) + " (" + formatText(actorRol) + ")";
    }

    public String resultadoStyleClass(String value) {
        if (value == null) {
            return "status-en-proceso";
        }
        return switch (value) {
            case "EXITO" -> "status-completada";
            case "ERROR" -> "status-error";
            case "ADVERTENCIA" -> "status-pendiente";
            default -> "status-en-proceso";
        };
    }

    public record AuditoriaEventDetailPresentation(
            String modulo,
            String accion,
            String resultado,
            String entidad,
            String entidadId,
            String actor,
            String requestId,
            String ipOrigen,
            String fechaEvento) {
    }
}
