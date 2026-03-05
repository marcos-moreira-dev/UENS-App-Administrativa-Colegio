package com.marcosmoreiradev.uensdesktop.app;

import javafx.util.Callback;

public final class ControllerFactory implements Callback<Class<?>, Object> {

    private AppContext appContext;

    public ControllerFactory(AppContext appContext) {
        this.appContext = appContext;
    }

    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public Object call(Class<?> type) {
        try {
            Object controller = type.getDeclaredConstructor().newInstance();
            if (controller instanceof ContextAwareController awareController) {
                awareController.setAppContext(appContext);
            }
            return controller;
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("No se pudo crear controller: " + type.getName(), ex);
        }
    }
}
