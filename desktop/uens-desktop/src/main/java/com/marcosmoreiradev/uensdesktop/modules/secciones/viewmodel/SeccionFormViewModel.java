package com.marcosmoreiradev.uensdesktop.modules.secciones.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class SeccionFormViewModel {

    private final BooleanProperty visible = new SimpleBooleanProperty(false);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final StringProperty title = new SimpleStringProperty("Crear secci\u00f3n");
    private final StringProperty bannerMessage = new SimpleStringProperty("");
    private final ObjectProperty<Integer> grado = new SimpleObjectProperty<>();
    private final StringProperty paralelo = new SimpleStringProperty("");
    private final ObjectProperty<Integer> cupoMaximo = new SimpleObjectProperty<>();
    private final StringProperty anioLectivo = new SimpleStringProperty("");
    private final BooleanBinding canSubmit = Bindings.createBooleanBinding(
            () -> !loading.get()
                    && grado.get() != null
                    && !paralelo.get().isBlank()
                    && cupoMaximo.get() != null
                    && !anioLectivo.get().isBlank(),
            loading, grado, paralelo, cupoMaximo, anioLectivo);

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

    public ObjectProperty<Integer> gradoProperty() {
        return grado;
    }

    public StringProperty paraleloProperty() {
        return paralelo;
    }

    public ObjectProperty<Integer> cupoMaximoProperty() {
        return cupoMaximo;
    }

    public StringProperty anioLectivoProperty() {
        return anioLectivo;
    }

    public BooleanBinding canSubmitBinding() {
        return canSubmit;
    }
}
