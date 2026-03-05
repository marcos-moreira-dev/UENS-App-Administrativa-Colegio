package com.marcosmoreiradev.uensdesktop.modules.reportes.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class ReporteSolicitudDetailViewModel {

    private final BooleanProperty visible = new SimpleBooleanProperty(false);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final StringProperty title = new SimpleStringProperty("Detalle del reporte");
    private final StringProperty bannerMessage = new SimpleStringProperty("");
    private final StringProperty solicitudId = new SimpleStringProperty("-");
    private final StringProperty tipoReporte = new SimpleStringProperty("-");
    private final StringProperty estado = new SimpleStringProperty("-");
    private final StringProperty intentos = new SimpleStringProperty("-");
    private final StringProperty fechaSolicitud = new SimpleStringProperty("-");
    private final StringProperty fechaActualizacion = new SimpleStringProperty("-");
    private final StringProperty parametrosJson = new SimpleStringProperty("-");
    private final StringProperty resultadoJson = new SimpleStringProperty("-");
    private final StringProperty errorDetalle = new SimpleStringProperty("-");
    private final StringProperty downloadPath = new SimpleStringProperty("");

    public BooleanProperty visibleProperty() {
        return visible;
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty bannerMessageProperty() {
        return bannerMessage;
    }

    public StringProperty solicitudIdProperty() {
        return solicitudId;
    }

    public StringProperty tipoReporteProperty() {
        return tipoReporte;
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public StringProperty intentosProperty() {
        return intentos;
    }

    public StringProperty fechaSolicitudProperty() {
        return fechaSolicitud;
    }

    public StringProperty fechaActualizacionProperty() {
        return fechaActualizacion;
    }

    public StringProperty parametrosJsonProperty() {
        return parametrosJson;
    }

    public StringProperty resultadoJsonProperty() {
        return resultadoJson;
    }

    public StringProperty errorDetalleProperty() {
        return errorDetalle;
    }

    public StringProperty downloadPathProperty() {
        return downloadPath;
    }
}
