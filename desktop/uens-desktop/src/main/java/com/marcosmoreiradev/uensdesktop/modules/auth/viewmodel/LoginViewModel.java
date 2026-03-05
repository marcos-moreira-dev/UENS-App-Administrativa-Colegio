package com.marcosmoreiradev.uensdesktop.modules.auth.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public final class LoginViewModel {

    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    public BooleanProperty loadingProperty() {
        return loading;
    }
}
