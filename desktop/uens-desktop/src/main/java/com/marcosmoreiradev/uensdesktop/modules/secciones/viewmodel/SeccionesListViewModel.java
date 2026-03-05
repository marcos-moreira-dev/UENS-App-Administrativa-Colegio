package com.marcosmoreiradev.uensdesktop.modules.secciones.viewmodel;

import com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto.SeccionListItemDto;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Observable state container for the sections listing screen.
 *
 * <p>The view model keeps the current table items, filter text, pagination state and loading flags
 * so the controller can bind the UI without duplicating derived state.
 */
public final class SeccionesListViewModel {

    private final ObservableList<SeccionListItemDto> items = FXCollections.observableArrayList();
    private final StringProperty statusText = new SimpleStringProperty("Cargando secciones...");
    private final StringProperty pageText = new SimpleStringProperty("P\u00e1gina 1 de 1");
    private final StringProperty totalText = new SimpleStringProperty("0 registros");
    private final StringProperty queryText = new SimpleStringProperty("");
    private final StringProperty paraleloFilter = new SimpleStringProperty("");
    private final StringProperty anioLectivoFilter = new SimpleStringProperty("");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final BooleanProperty previousPageAvailable = new SimpleBooleanProperty(false);
    private final BooleanProperty nextPageAvailable = new SimpleBooleanProperty(false);
    private final IntegerProperty page = new SimpleIntegerProperty(0);

    public ObservableList<SeccionListItemDto> items() {
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

    public StringProperty paraleloFilterProperty() {
        return paraleloFilter;
    }

    public StringProperty anioLectivoFilterProperty() {
        return anioLectivoFilter;
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
