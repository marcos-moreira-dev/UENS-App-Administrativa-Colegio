package com.marcosmoreiradev.uensdesktop.modules.clases.viewmodel;

import com.marcosmoreiradev.uensdesktop.api.modules.clases.dto.ClaseListItemDto;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class ClasesListViewModel {

    private final ObservableList<ClaseListItemDto> items = FXCollections.observableArrayList();
    private final StringProperty statusText = new SimpleStringProperty("Cargando clases...");
    private final StringProperty pageText = new SimpleStringProperty("P\u00e1gina 1 de 1");
    private final StringProperty totalText = new SimpleStringProperty("0 registros");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final BooleanProperty previousPageAvailable = new SimpleBooleanProperty(false);
    private final BooleanProperty nextPageAvailable = new SimpleBooleanProperty(false);
    private final IntegerProperty page = new SimpleIntegerProperty(0);

    public ObservableList<ClaseListItemDto> items() {
        return items;
    }

    public StringProperty statusTextProperty() {
        return statusText;
    }

    public StringProperty pageTextProperty() {
        return pageText;
    }

    public StringProperty totalTextProperty() {
        return totalText;
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }

    public BooleanProperty previousPageAvailableProperty() {
        return previousPageAvailable;
    }

    public BooleanProperty nextPageAvailableProperty() {
        return nextPageAvailable;
    }

    public IntegerProperty pageProperty() {
        return page;
    }
}
