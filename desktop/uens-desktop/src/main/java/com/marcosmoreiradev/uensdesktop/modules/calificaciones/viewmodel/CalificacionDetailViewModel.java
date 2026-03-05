package com.marcosmoreiradev.uensdesktop.modules.calificaciones.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class CalificacionDetailViewModel {

    private final BooleanProperty visible = new SimpleBooleanProperty(false);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final StringProperty title = new SimpleStringProperty("Detalle de la calificación");
    private final StringProperty bannerMessage = new SimpleStringProperty("");
    private final StringProperty estudiante = new SimpleStringProperty("-");
    private final StringProperty clase = new SimpleStringProperty("-");
    private final StringProperty parcial = new SimpleStringProperty("-");
    private final StringProperty nota = new SimpleStringProperty("-");
    private final StringProperty fechaRegistro = new SimpleStringProperty("-");
    private final StringProperty observacion = new SimpleStringProperty("-");

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

    public StringProperty estudianteProperty() {
        return estudiante;
    }

    public StringProperty claseProperty() {
        return clase;
    }

    public StringProperty parcialProperty() {
        return parcial;
    }

    public StringProperty notaProperty() {
        return nota;
    }

    public StringProperty fechaRegistroProperty() {
        return fechaRegistro;
    }

    public StringProperty observacionProperty() {
        return observacion;
    }
}
