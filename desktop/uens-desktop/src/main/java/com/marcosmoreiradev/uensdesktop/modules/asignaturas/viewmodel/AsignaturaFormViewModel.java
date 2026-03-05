package com.marcosmoreiradev.uensdesktop.modules.asignaturas.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class AsignaturaFormViewModel {

    private final BooleanProperty visible = new SimpleBooleanProperty(false);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final StringProperty title = new SimpleStringProperty("Crear asignatura");
    private final StringProperty bannerMessage = new SimpleStringProperty("");
    private final StringProperty nombre = new SimpleStringProperty("");
    private final StringProperty area = new SimpleStringProperty("");
    private final StringProperty descripcion = new SimpleStringProperty("");
    private final ObjectProperty<Integer> grado = new SimpleObjectProperty<>();
    private final BooleanBinding canSubmit = Bindings.createBooleanBinding(
            () -> !loading.get()
                    && !nombre.get().isBlank()
                    && !area.get().isBlank()
                    && grado.get() != null,
            loading, nombre, area, grado);

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

    public ObjectProperty<Integer> gradoProperty() {
        return grado;
    }

    public BooleanBinding canSubmitBinding() {
        return canSubmit;
    }
}
