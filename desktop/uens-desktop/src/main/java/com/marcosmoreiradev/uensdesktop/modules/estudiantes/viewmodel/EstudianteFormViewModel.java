package com.marcosmoreiradev.uensdesktop.modules.estudiantes.viewmodel;

import com.marcosmoreiradev.uensdesktop.modules.estudiantes.model.FormMode;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.LocalDate;

public final class EstudianteFormViewModel {

    private final BooleanProperty visible = new SimpleBooleanProperty(false);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final ObjectProperty<FormMode> mode = new SimpleObjectProperty<>(FormMode.CREATE);
    private final StringProperty title = new SimpleStringProperty("Crear estudiante");
    private final StringProperty bannerMessage = new SimpleStringProperty("");
    private final StringProperty nombres = new SimpleStringProperty("");
    private final StringProperty apellidos = new SimpleStringProperty("");
    private final ObjectProperty<LocalDate> fechaNacimiento = new SimpleObjectProperty<>();
    private final StringProperty selectedRepresentanteText = new SimpleStringProperty("Selecciona un representante");
    private final StringProperty selectedSeccionText = new SimpleStringProperty("Sin sección asignada");
    private final BooleanProperty sectionSelected = new SimpleBooleanProperty(false);
    private final BooleanBinding canSubmit = Bindings.createBooleanBinding(
            () -> {
                if (loading.get()) {
                    return false;
                }
                if (mode.get() == FormMode.ASSIGN_SECTION) {
                    return sectionSelected.get();
                }
                return !nombres.get().isBlank()
                        && !apellidos.get().isBlank()
                        && fechaNacimiento.get() != null;
            },
            loading, mode, nombres, apellidos, fechaNacimiento, sectionSelected);

    public BooleanProperty visibleProperty() {
        return visible;
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }

    public ObjectProperty<FormMode> modeProperty() {
        return mode;
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

    public ObjectProperty<LocalDate> fechaNacimientoProperty() {
        return fechaNacimiento;
    }

    public StringProperty selectedRepresentanteTextProperty() {
        return selectedRepresentanteText;
    }

    public StringProperty selectedSeccionTextProperty() {
        return selectedSeccionText;
    }

    public BooleanProperty sectionSelectedProperty() {
        return sectionSelected;
    }

    public BooleanBinding canSubmitBinding() {
        return canSubmit;
    }
}
