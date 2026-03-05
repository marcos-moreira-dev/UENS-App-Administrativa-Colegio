package com.marcosmoreiradev.uensdesktop.modules.estudiantes.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class EstudianteDetailViewModel {

    private final BooleanProperty visible = new SimpleBooleanProperty(false);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final StringProperty title = new SimpleStringProperty("Detalle del estudiante");
    private final StringProperty bannerMessage = new SimpleStringProperty("");
    private final StringProperty nombres = new SimpleStringProperty("-");
    private final StringProperty apellidos = new SimpleStringProperty("-");
    private final StringProperty fechaNacimiento = new SimpleStringProperty("-");
    private final StringProperty estado = new SimpleStringProperty("-");
    private final StringProperty representante = new SimpleStringProperty("-");
    private final StringProperty seccion = new SimpleStringProperty("Sin sección vigente");

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

    public StringProperty nombresProperty() {
        return nombres;
    }

    public StringProperty apellidosProperty() {
        return apellidos;
    }

    public StringProperty fechaNacimientoProperty() {
        return fechaNacimiento;
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public StringProperty representanteProperty() {
        return representante;
    }

    public StringProperty seccionProperty() {
        return seccion;
    }
}
