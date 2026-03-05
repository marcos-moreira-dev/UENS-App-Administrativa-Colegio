package com.marcosmoreiradev.uensdesktop.modules.auditoria.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Observable state container for the audit-event detail panel.
 */
public final class AuditoriaDetailViewModel {

    private final StringProperty modulo = new SimpleStringProperty("-");
    private final StringProperty accion = new SimpleStringProperty("-");
    private final StringProperty resultado = new SimpleStringProperty("-");
    private final StringProperty entidad = new SimpleStringProperty("-");
    private final StringProperty entidadId = new SimpleStringProperty("-");
    private final StringProperty actor = new SimpleStringProperty("-");
    private final StringProperty requestId = new SimpleStringProperty("-");
    private final StringProperty ipOrigen = new SimpleStringProperty("-");
    private final StringProperty fechaEvento = new SimpleStringProperty("-");

    public StringProperty moduloProperty() { return modulo; }
    public StringProperty accionProperty() { return accion; }
    public StringProperty resultadoProperty() { return resultado; }
    public StringProperty entidadProperty() { return entidad; }
    public StringProperty entidadIdProperty() { return entidadId; }
    public StringProperty actorProperty() { return actor; }
    public StringProperty requestIdProperty() { return requestId; }
    public StringProperty ipOrigenProperty() { return ipOrigen; }
    public StringProperty fechaEventoProperty() { return fechaEvento; }
}
