package com.marcosmoreiradev.uensdesktop.modules.asignaturas.viewmodel;

import com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.dto.AsignaturaListItemDto;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class AsignaturasListViewModel {

    private final ObservableList<AsignaturaListItemDto> items = FXCollections.observableArrayList();
    private final StringProperty statusText = new SimpleStringProperty("Cargando asignaturas...");
    private final StringProperty pageText = new SimpleStringProperty("P\u00e1gina 1 de 1");
    private final StringProperty totalText = new SimpleStringProperty("0 registros");
    private final StringProperty queryText = new SimpleStringProperty("");
    private final StringProperty areaFilter = new SimpleStringProperty("");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final BooleanProperty previousPageAvailable = new SimpleBooleanProperty(false);
    private final BooleanProperty nextPageAvailable = new SimpleBooleanProperty(false);
    private final IntegerProperty page = new SimpleIntegerProperty(0);

    public ObservableList<AsignaturaListItemDto> items() {
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

    public StringProperty queryTextProperty() {
        return queryText;
    }

    public StringProperty areaFilterProperty() {
        return areaFilter;
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
