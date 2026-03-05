package com.marcosmoreiradev.uensdesktop.modules.reportes.viewmodel;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class CrearReporteSolicitudFormViewModel {

    private final StringProperty bannerMessage = new SimpleStringProperty("");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final BooleanProperty seccionVisible = new SimpleBooleanProperty(true);
    private final BooleanProperty parcialVisible = new SimpleBooleanProperty(false);

    public StringProperty bannerMessageProperty() {
        return bannerMessage;
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }

    public BooleanProperty seccionVisibleProperty() {
        return seccionVisible;
    }

    public BooleanProperty parcialVisibleProperty() {
        return parcialVisible;
    }

    public BooleanBinding canSubmitBinding() {
        return loading.not();
    }
}
