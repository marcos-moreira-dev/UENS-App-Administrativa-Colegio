package com.marcosmoreiradev.uensdesktop.modules.asignaturas.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class AsignaturaDetailViewModel {

    private final BooleanProperty visible = new SimpleBooleanProperty(false);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final StringProperty title = new SimpleStringProperty("Detalle de la asignatura");
    private final StringProperty bannerMessage = new SimpleStringProperty("");
    private final StringProperty nombre = new SimpleStringProperty("-");
    private final StringProperty area = new SimpleStringProperty("-");
    private final StringProperty descripcion = new SimpleStringProperty("-");
    private final StringProperty grado = new SimpleStringProperty("-");
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

    public StringProperty areaProperty() {
        return area;
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }

    public StringProperty gradoProperty() {
        return grado;
    }

    public StringProperty estadoProperty() {
        return estado;
    }
}
