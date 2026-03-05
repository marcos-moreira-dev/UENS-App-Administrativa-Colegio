package com.marcosmoreiradev.uensdesktop.modules.clases.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class ClaseDetailViewModel {

    private final BooleanProperty visible = new SimpleBooleanProperty(false);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final StringProperty title = new SimpleStringProperty("Detalle de la clase");
    private final StringProperty bannerMessage = new SimpleStringProperty("");
    private final StringProperty seccion = new SimpleStringProperty("-");
    private final StringProperty asignatura = new SimpleStringProperty("-");
    private final StringProperty docente = new SimpleStringProperty("-");
    private final StringProperty diaSemana = new SimpleStringProperty("-");
    private final StringProperty horario = new SimpleStringProperty("-");
    private final StringProperty estado = new SimpleStringProperty("-");

    public BooleanProperty visibleProperty() {
        return visible;
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty bannerMessageProperty() {
        return bannerMessage;
    }

    public StringProperty seccionProperty() {
        return seccion;
    }

    public StringProperty asignaturaProperty() {
        return asignatura;
    }

    public StringProperty docenteProperty() {
        return docente;
    }

    public StringProperty diaSemanaProperty() {
        return diaSemana;
    }

    public StringProperty horarioProperty() {
        return horario;
    }

    public StringProperty estadoProperty() {
        return estado;
    }
}
