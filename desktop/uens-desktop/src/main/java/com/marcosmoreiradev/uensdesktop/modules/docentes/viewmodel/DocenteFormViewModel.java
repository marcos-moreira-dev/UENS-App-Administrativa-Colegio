package com.marcosmoreiradev.uensdesktop.modules.docentes.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class DocenteFormViewModel {

    private final BooleanProperty visible = new SimpleBooleanProperty(false);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final StringProperty title = new SimpleStringProperty("Crear docente");
    private final StringProperty bannerMessage = new SimpleStringProperty("");
    private final StringProperty nombres = new SimpleStringProperty("");
    private final StringProperty apellidos = new SimpleStringProperty("");
    private final StringProperty telefono = new SimpleStringProperty("");
    private final StringProperty correo = new SimpleStringProperty("");
    private final BooleanBinding canSubmit = Bindings.createBooleanBinding(
            () -> !loading.get() && !nombres.get().isBlank() && !apellidos.get().isBlank(),
            loading, nombres, apellidos);

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

    public StringProperty telefonoProperty() {
        return telefono;
    }

    public StringProperty correoProperty() {
        return correo;
    }

    public BooleanBinding canSubmitBinding() {
        return canSubmit;
    }
}
