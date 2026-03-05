package com.marcosmoreiradev.uensdesktop.modules.calificaciones.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class CalificacionFormViewModel {

    private final BooleanProperty visible = new SimpleBooleanProperty(false);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final StringProperty title = new SimpleStringProperty("Registrar calificación");
    private final StringProperty bannerMessage = new SimpleStringProperty("");
    private final ObjectProperty<Long> estudianteId = new SimpleObjectProperty<>();
    private final ObjectProperty<Long> claseId = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> numeroParcial = new SimpleObjectProperty<>();
    private final StringProperty nota = new SimpleStringProperty("");
    private final ObjectProperty<java.time.LocalDate> fechaRegistro = new SimpleObjectProperty<>();
    private final StringProperty observacion = new SimpleStringProperty("");
    private final BooleanBinding canSubmit = Bindings.createBooleanBinding(
            () -> !loading.get()
                    && estudianteId.get() != null
                    && claseId.get() != null
                    && numeroParcial.get() != null
                    && !nota.get().isBlank(),
            loading, estudianteId, claseId, numeroParcial, nota);

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

    public ObjectProperty<Long> estudianteIdProperty() {
        return estudianteId;
    }

    public ObjectProperty<Long> claseIdProperty() {
        return claseId;
    }

    public ObjectProperty<Integer> numeroParcialProperty() {
        return numeroParcial;
    }

    public StringProperty notaProperty() {
        return nota;
    }

    public ObjectProperty<java.time.LocalDate> fechaRegistroProperty() {
        return fechaRegistro;
    }

    public StringProperty observacionProperty() {
        return observacion;
    }

    public BooleanBinding canSubmitBinding() {
        return canSubmit;
    }
}
