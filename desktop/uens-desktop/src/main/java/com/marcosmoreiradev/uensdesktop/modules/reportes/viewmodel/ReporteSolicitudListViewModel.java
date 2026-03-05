package com.marcosmoreiradev.uensdesktop.modules.reportes.viewmodel;

import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudListItemDto;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class ReporteSolicitudListViewModel {

    private final ObservableList<ReporteSolicitudListItemDto> items = FXCollections.observableArrayList();
    private final StringProperty totalText = new SimpleStringProperty("0 solicitudes");
    private final StringProperty pageText = new SimpleStringProperty("P\u00e1gina 1 de 1");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final BooleanProperty previousPageAvailable = new SimpleBooleanProperty(false);
    private final BooleanProperty nextPageAvailable = new SimpleBooleanProperty(false);
    private final IntegerProperty page = new SimpleIntegerProperty(0);

    public ObservableList<ReporteSolicitudListItemDto> items() {
        return items;
    }

    public StringProperty totalTextProperty() {
        return totalText;
    }

    public StringProperty pageTextProperty() {
        return pageText;
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
