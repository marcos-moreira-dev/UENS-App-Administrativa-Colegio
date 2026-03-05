package com.marcosmoreiradev.uensdesktop.modules.clases.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class ClaseFormViewModel {

    private final BooleanProperty visible = new SimpleBooleanProperty(false);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final StringProperty title = new SimpleStringProperty("Crear clase");
    private final StringProperty bannerMessage = new SimpleStringProperty("");
    private final ObjectProperty<Long> seccionId = new SimpleObjectProperty<>();
    private final ObjectProperty<Long> asignaturaId = new SimpleObjectProperty<>();
    private final ObjectProperty<Long> docenteId = new SimpleObjectProperty<>();
    private final StringProperty diaSemana = new SimpleStringProperty("");
    private final StringProperty horaInicio = new SimpleStringProperty("");
    private final StringProperty horaFin = new SimpleStringProperty("");
    private final BooleanBinding canSubmit = Bindings.createBooleanBinding(
            () -> !loading.get()
                    && seccionId.get() != null
                    && asignaturaId.get() != null
                    && !diaSemana.get().isBlank()
                    && !horaInicio.get().isBlank()
                    && !horaFin.get().isBlank(),
            loading, seccionId, asignaturaId, diaSemana, horaInicio, horaFin);

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

    public ObjectProperty<Long> seccionIdProperty() {
        return seccionId;
    }

    public ObjectProperty<Long> asignaturaIdProperty() {
        return asignaturaId;
    }

    public ObjectProperty<Long> docenteIdProperty() {
        return docenteId;
    }

    public StringProperty diaSemanaProperty() {
        return diaSemana;
    }

    public StringProperty horaInicioProperty() {
        return horaInicio;
    }

    public StringProperty horaFinProperty() {
        return horaFin;
    }

    public BooleanBinding canSubmitBinding() {
        return canSubmit;
    }
}
