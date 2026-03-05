package com.marcosmoreiradev.uensdesktop.modules.auditoria.viewmodel;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class AuditoriaReporteFormViewModel {

    private final BooleanProperty visible = new SimpleBooleanProperty(false);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final StringProperty bannerMessage = new SimpleStringProperty("");

    public BooleanProperty visibleProperty() {
        return visible;
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }

    public StringProperty bannerMessageProperty() {
        return bannerMessage;
    }

    public BooleanBinding canSubmitBinding() {
        return loading.not();
    }
}
