package com.marcosmoreiradev.uensdesktop.modules.reportes.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class ReportesViewModel {

    private final StringProperty statusText = new SimpleStringProperty("Cargando reportes...");
    private final StringProperty pollingText = new SimpleStringProperty("");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    public StringProperty statusTextProperty() {
        return statusText;
    }

    public StringProperty pollingTextProperty() {
        return pollingText;
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }
}
