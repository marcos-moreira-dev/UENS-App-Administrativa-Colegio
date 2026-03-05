package com.marcosmoreiradev.uensdesktop.modules.dashboard.view;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.modules.dashboard.dto.DashboardResumenDto;
import com.marcosmoreiradev.uensdesktop.app.AppContext;
import com.marcosmoreiradev.uensdesktop.app.ContextAwareController;
import com.marcosmoreiradev.uensdesktop.modules.dashboard.application.DashboardService;
import com.marcosmoreiradev.uensdesktop.modules.dashboard.viewmodel.DashboardViewModel;
import com.marcosmoreiradev.uensdesktop.ui.fx.FxExecutors;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

public final class DashboardController implements ContextAwareController {

    private static final Locale DASHBOARD_LOCALE = Locale.forLanguageTag("es-EC");
    private static final NumberFormat INTEGER_FORMAT = NumberFormat.getIntegerInstance(DASHBOARD_LOCALE);
    private static final NumberFormat DECIMAL_FORMAT = buildDecimalFormat();
    private static final NumberFormat PERCENT_FORMAT = buildPercentFormat();

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label heroMetricValueLabel;

    @FXML
    private Label heroMetricTitleLabel;

    @FXML
    private Label heroMetricDescriptionLabel;

    @FXML
    private Label ratioEstudiantesSeccionLabel;

    @FXML
    private Label ratioAsignaturasSeccionLabel;

    @FXML
    private Label ratioClasesAsignaturaLabel;

    @FXML
    private Label totalEstudiantesLabel;

    @FXML
    private Label totalDocentesLabel;

    @FXML
    private Label totalSeccionesLabel;

    @FXML
    private Label totalAsignaturasLabel;

    @FXML
    private Label totalClasesLabel;

    @FXML
    private Label totalCalificacionesLabel;

    @FXML
    private Label mixEstudiantesLabel;

    @FXML
    private Label mixDocentesLabel;

    @FXML
    private Label mixSeccionesLabel;

    @FXML
    private Label mixAsignaturasLabel;

    @FXML
    private Label mixClasesLabel;

    @FXML
    private Label insightCalificacionesEstudianteLabel;

    @FXML
    private Label insightDocentesSeccionLabel;

    @FXML
    private Label insightClasesDocenteLabel;

    @FXML
    private PieChart entityMixChart;

    private final DashboardViewModel viewModel = new DashboardViewModel();
    private final PieChart.Data mixEstudiantesSlice = new PieChart.Data("Estudiantes", 0);
    private final PieChart.Data mixDocentesSlice = new PieChart.Data("Docentes", 0);
    private final PieChart.Data mixSeccionesSlice = new PieChart.Data("Secciones", 0);
    private final PieChart.Data mixAsignaturasSlice = new PieChart.Data("Asignaturas", 0);
    private final PieChart.Data mixClasesSlice = new PieChart.Data("Clases", 0);
    private DashboardService dashboardService;

    @FXML
    private void initialize() {
        welcomeLabel.textProperty().bind(viewModel.welcomeTextProperty());
        statusLabel.textProperty().bind(viewModel.statusTextProperty());
        heroMetricValueLabel.textProperty().bind(viewModel.heroMetricValueProperty());
        heroMetricTitleLabel.textProperty().bind(viewModel.heroMetricTitleProperty());
        heroMetricDescriptionLabel.textProperty().bind(viewModel.heroMetricDescriptionProperty());
        ratioEstudiantesSeccionLabel.textProperty().bind(viewModel.ratioEstudiantesSeccionProperty());
        ratioAsignaturasSeccionLabel.textProperty().bind(viewModel.ratioAsignaturasSeccionProperty());
        ratioClasesAsignaturaLabel.textProperty().bind(viewModel.ratioClasesAsignaturaProperty());
        totalEstudiantesLabel.textProperty().bind(viewModel.totalEstudiantesProperty());
        totalDocentesLabel.textProperty().bind(viewModel.totalDocentesProperty());
        totalSeccionesLabel.textProperty().bind(viewModel.totalSeccionesProperty());
        totalAsignaturasLabel.textProperty().bind(viewModel.totalAsignaturasProperty());
        totalClasesLabel.textProperty().bind(viewModel.totalClasesProperty());
        totalCalificacionesLabel.textProperty().bind(viewModel.totalCalificacionesProperty());
        mixEstudiantesLabel.textProperty().bind(viewModel.mixEstudiantesProperty());
        mixDocentesLabel.textProperty().bind(viewModel.mixDocentesProperty());
        mixSeccionesLabel.textProperty().bind(viewModel.mixSeccionesProperty());
        mixAsignaturasLabel.textProperty().bind(viewModel.mixAsignaturasProperty());
        mixClasesLabel.textProperty().bind(viewModel.mixClasesProperty());
        insightCalificacionesEstudianteLabel.textProperty().bind(viewModel.insightCalificacionesEstudianteProperty());
        insightDocentesSeccionLabel.textProperty().bind(viewModel.insightDocentesSeccionProperty());
        insightClasesDocenteLabel.textProperty().bind(viewModel.insightClasesDocenteProperty());

        entityMixChart.setAnimated(false);
        entityMixChart.setClockwise(true);
        entityMixChart.setStartAngle(90);
        entityMixChart.setData(FXCollections.observableArrayList(
                mixEstudiantesSlice,
                mixDocentesSlice,
                mixSeccionesSlice,
                mixAsignaturasSlice,
                mixClasesSlice));
    }

    @Override
    public void setAppContext(AppContext appContext) {
        String login = appContext.sessionState().usuario()
                .map(usuario -> usuario.login())
                .orElse("usuario");
        viewModel.welcomeTextProperty().set("Bienvenido, " + login);
        dashboardService = appContext.services().dashboardService();
        loadResumen();
    }

    private void loadResumen() {
        viewModel.loadingProperty().set(true);
        viewModel.statusTextProperty().set("Consultando resumen del backend...");
        FxExecutors.submitIo(dashboardService::obtenerResumen, this::applyResumen);
    }

    private void applyResumen(ApiResult<DashboardResumenDto> result) {
        viewModel.loadingProperty().set(false);
        if (!result.isSuccess()) {
            viewModel.statusTextProperty().set(
                    result.error().map(error -> error.message()).orElse("No se pudo cargar el dashboard."));
            return;
        }

        DashboardResumenDto resumen = result.data().orElseThrow();
        long totalEstudiantes = resumen.totalEstudiantes();
        long totalDocentes = resumen.totalDocentes();
        long totalSecciones = resumen.totalSecciones();
        long totalAsignaturas = resumen.totalAsignaturas();
        long totalClases = resumen.totalClases();
        long totalCalificaciones = resumen.totalCalificaciones();

        viewModel.totalEstudiantesProperty().set(formatInteger(totalEstudiantes));
        viewModel.totalDocentesProperty().set(formatInteger(totalDocentes));
        viewModel.totalSeccionesProperty().set(formatInteger(totalSecciones));
        viewModel.totalAsignaturasProperty().set(formatInteger(totalAsignaturas));
        viewModel.totalClasesProperty().set(formatInteger(totalClases));
        viewModel.totalCalificacionesProperty().set(formatInteger(totalCalificaciones));

        double estudiantesPorSeccion = ratio(totalEstudiantes, totalSecciones);
        double asignaturasPorSeccion = ratio(totalAsignaturas, totalSecciones);
        double clasesPorAsignatura = ratio(totalClases, totalAsignaturas);
        double calificacionesPorEstudiante = ratio(totalCalificaciones, totalEstudiantes);
        double docentesPorSeccion = ratio(totalDocentes, totalSecciones);
        double clasesPorDocente = ratio(totalClases, totalDocentes);

        viewModel.heroMetricValueProperty().set(formatDecimal(estudiantesPorSeccion));
        viewModel.heroMetricTitleProperty().set("Estudiantes por seccion");
        viewModel.heroMetricDescriptionProperty().set(
                formatInteger(totalEstudiantes) + " estudiantes se reparten en "
                        + formatInteger(totalSecciones) + " secciones, con "
                        + formatInteger(totalAsignaturas) + " asignaturas y "
                        + formatInteger(totalClases) + " clases que sostienen el flujo academico.");
        viewModel.ratioEstudiantesSeccionProperty().set(formatDecimal(estudiantesPorSeccion));
        viewModel.ratioAsignaturasSeccionProperty().set(formatDecimal(asignaturasPorSeccion));
        viewModel.ratioClasesAsignaturaProperty().set(formatDecimal(clasesPorAsignatura));

        viewModel.insightCalificacionesEstudianteProperty().set(formatDecimal(calificacionesPorEstudiante));
        viewModel.insightDocentesSeccionProperty().set(formatDecimal(docentesPorSeccion));
        viewModel.insightClasesDocenteProperty().set(formatDecimal(clasesPorDocente));

        updateMixChart(totalEstudiantes, totalDocentes, totalSecciones, totalAsignaturas, totalClases);
        viewModel.statusTextProperty().set(
                "Resumen cargado: "
                        + formatInteger(totalClases)
                        + " clases activan "
                        + formatInteger(totalCalificaciones)
                        + " registros evaluativos en el backend.");
    }

    private void updateMixChart(long estudiantes, long docentes, long secciones, long asignaturas, long clases) {
        mixEstudiantesSlice.setPieValue(estudiantes);
        mixDocentesSlice.setPieValue(docentes);
        mixSeccionesSlice.setPieValue(secciones);
        mixAsignaturasSlice.setPieValue(asignaturas);
        mixClasesSlice.setPieValue(clases);

        long totalBase = estudiantes + docentes + secciones + asignaturas + clases;
        viewModel.mixEstudiantesProperty().set(formatMixLegend(estudiantes, totalBase));
        viewModel.mixDocentesProperty().set(formatMixLegend(docentes, totalBase));
        viewModel.mixSeccionesProperty().set(formatMixLegend(secciones, totalBase));
        viewModel.mixAsignaturasProperty().set(formatMixLegend(asignaturas, totalBase));
        viewModel.mixClasesProperty().set(formatMixLegend(clases, totalBase));
    }

    private static double ratio(long numerator, long denominator) {
        if (denominator <= 0) {
            return 0.0;
        }
        return (double) numerator / (double) denominator;
    }

    private static String formatMixLegend(long value, long total) {
        return formatInteger(value) + " · " + formatPercent(ratio(value, total));
    }

    private static String formatInteger(long value) {
        return INTEGER_FORMAT.format(value);
    }

    private static String formatDecimal(double value) {
        return DECIMAL_FORMAT.format(value);
    }

    private static String formatPercent(double value) {
        return PERCENT_FORMAT.format(value);
    }

    private static NumberFormat buildDecimalFormat() {
        NumberFormat format = NumberFormat.getNumberInstance(DASHBOARD_LOCALE);
        format.setMinimumFractionDigits(1);
        format.setMaximumFractionDigits(1);
        return format;
    }

    private static NumberFormat buildPercentFormat() {
        NumberFormat format = NumberFormat.getPercentInstance(DASHBOARD_LOCALE);
        format.setMinimumFractionDigits(1);
        format.setMaximumFractionDigits(1);
        return format;
    }
}
