package com.marcosmoreiradev.uensdesktop.modules.secciones.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class SeccionDetailViewModel {

    private final BooleanProperty visible = new SimpleBooleanProperty(false);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final StringProperty title = new SimpleStringProperty("Detalle de la secci\u00f3n");
    private final StringProperty bannerMessage = new SimpleStringProperty("");
    private final StringProperty nombre = new SimpleStringProperty("-");
    private final StringProperty grado = new SimpleStringProperty("-");
    private final StringProperty paralelo = new SimpleStringProperty("-");
    private final StringProperty cupoMaximo = new SimpleStringProperty("-");
    private final StringProperty anioLectivo = new SimpleStringProperty("-");
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

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty gradoProperty() {
        return grado;
    }

    public StringProperty paraleloProperty() {
        return paralelo;
    }

    public StringProperty cupoMaximoProperty() {
        return cupoMaximo;
    }

    public StringProperty anioLectivoProperty() {
        return anioLectivo;
    }

    public StringProperty estadoProperty() {
        return estado;
    }
}
