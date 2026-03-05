package com.marcosmoreiradev.uensdesktop.modules.dashboard.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class DashboardViewModel {

    private final StringProperty title = new SimpleStringProperty("Dashboard");
    private final StringProperty welcomeText = new SimpleStringProperty("Bienvenido");
    private final StringProperty statusText = new SimpleStringProperty("Cargando resumen...");
    private final StringProperty heroMetricValue = new SimpleStringProperty("-");
    private final StringProperty heroMetricTitle = new SimpleStringProperty("Estudiantes por secci\u00f3n");
    private final StringProperty heroMetricDescription = new SimpleStringProperty("Preparando lectura operativa...");
    private final StringProperty ratioEstudiantesSeccion = new SimpleStringProperty("-");
    private final StringProperty ratioAsignaturasSeccion = new SimpleStringProperty("-");
    private final StringProperty ratioClasesAsignatura = new SimpleStringProperty("-");
    private final StringProperty totalEstudiantes = new SimpleStringProperty("-");
    private final StringProperty totalDocentes = new SimpleStringProperty("-");
    private final StringProperty totalSecciones = new SimpleStringProperty("-");
    private final StringProperty totalAsignaturas = new SimpleStringProperty("-");
    private final StringProperty totalClases = new SimpleStringProperty("-");
    private final StringProperty totalCalificaciones = new SimpleStringProperty("-");
    private final StringProperty mixEstudiantes = new SimpleStringProperty("-");
    private final StringProperty mixDocentes = new SimpleStringProperty("-");
    private final StringProperty mixSecciones = new SimpleStringProperty("-");
    private final StringProperty mixAsignaturas = new SimpleStringProperty("-");
    private final StringProperty mixClases = new SimpleStringProperty("-");
    private final StringProperty insightCalificacionesEstudiante = new SimpleStringProperty("-");
    private final StringProperty insightDocentesSeccion = new SimpleStringProperty("-");
    private final StringProperty insightClasesDocente = new SimpleStringProperty("-");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty welcomeTextProperty() {
        return welcomeText;
    }

    public StringProperty statusTextProperty() {
        return statusText;
    }

    public StringProperty heroMetricValueProperty() {
        return heroMetricValue;
    }

    public StringProperty heroMetricTitleProperty() {
        return heroMetricTitle;
    }

    public StringProperty heroMetricDescriptionProperty() {
        return heroMetricDescription;
    }

    public StringProperty ratioEstudiantesSeccionProperty() {
        return ratioEstudiantesSeccion;
    }

    public StringProperty ratioAsignaturasSeccionProperty() {
        return ratioAsignaturasSeccion;
    }

    public StringProperty ratioClasesAsignaturaProperty() {
        return ratioClasesAsignatura;
    }

    public StringProperty totalEstudiantesProperty() {
        return totalEstudiantes;
    }

    public StringProperty totalDocentesProperty() {
        return totalDocentes;
    }

    public StringProperty totalSeccionesProperty() {
        return totalSecciones;
    }

    public StringProperty totalAsignaturasProperty() {
        return totalAsignaturas;
    }

    public StringProperty totalClasesProperty() {
        return totalClases;
    }

    public StringProperty totalCalificacionesProperty() {
        return totalCalificaciones;
    }

    public StringProperty mixEstudiantesProperty() {
        return mixEstudiantes;
    }

    public StringProperty mixDocentesProperty() {
        return mixDocentes;
    }

    public StringProperty mixSeccionesProperty() {
        return mixSecciones;
    }

    public StringProperty mixAsignaturasProperty() {
        return mixAsignaturas;
    }

    public StringProperty mixClasesProperty() {
        return mixClases;
    }

    public StringProperty insightCalificacionesEstudianteProperty() {
        return insightCalificacionesEstudiante;
    }

    public StringProperty insightDocentesSeccionProperty() {
        return insightDocentesSeccion;
    }

    public StringProperty insightClasesDocenteProperty() {
        return insightClasesDocente;
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }
}
