package com.marcosmoreiradev.uensdesktop.modules.calificaciones.view;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.calificaciones.dto.CalificacionCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.calificaciones.dto.CalificacionListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.calificaciones.dto.CalificacionResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.calificaciones.dto.CalificacionUpdateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.clases.dto.ClaseListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto.EstudianteListItemDto;
import com.marcosmoreiradev.uensdesktop.app.AppContext;
import com.marcosmoreiradev.uensdesktop.app.ContextAwareController;
import com.marcosmoreiradev.uensdesktop.modules.calificaciones.application.CalificacionesListQuery;
import com.marcosmoreiradev.uensdesktop.modules.calificaciones.application.CalificacionesReferenceDataService;
import com.marcosmoreiradev.uensdesktop.modules.calificaciones.application.CalificacionesService;
import com.marcosmoreiradev.uensdesktop.modules.calificaciones.viewmodel.CalificacionDetailViewModel;
import com.marcosmoreiradev.uensdesktop.modules.calificaciones.viewmodel.CalificacionFormViewModel;
import com.marcosmoreiradev.uensdesktop.modules.calificaciones.viewmodel.CalificacionesListViewModel;
import com.marcosmoreiradev.uensdesktop.ui.fx.FxExecutors;
import com.marcosmoreiradev.uensdesktop.ui.fx.SearchableComboBoxSupport;
import com.marcosmoreiradev.uensdesktop.ui.fx.UiStyling;
import com.marcosmoreiradev.uensdesktop.ui.tooltip.TooltipSupport;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public final class CalificacionesController implements ContextAwareController {

    private static final int PAGE_SIZE = 20;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final ObservableList<Integer> PARCIALES = FXCollections.observableArrayList(1, 2);

    @FXML private ComboBox<EstudianteListItemDto> estudianteFilterCombo;
    @FXML private ComboBox<ClaseListItemDto> claseFilterCombo;
    @FXML private ComboBox<Integer> parcialFilterCombo;
    @FXML private Button buscarButton;
    @FXML private Button limpiarButton;
    @FXML private Button crearCalificacionButton;
    @FXML private Button anteriorButton;
    @FXML private Button siguienteButton;
    @FXML private Label totalLabel;
    @FXML private Label pageLabel;
    @FXML private Label statusLabel;
    @FXML private TableView<CalificacionListItemDto> calificacionesTable;
    @FXML private TableColumn<CalificacionListItemDto, Long> idColumn;
    @FXML private TableColumn<CalificacionListItemDto, String> estudianteColumn;
    @FXML private TableColumn<CalificacionListItemDto, String> claseColumn;
    @FXML private TableColumn<CalificacionListItemDto, Integer> parcialColumn;
    @FXML private TableColumn<CalificacionListItemDto, String> notaColumn;
    @FXML private TableColumn<CalificacionListItemDto, String> fechaColumn;
    @FXML private TableColumn<CalificacionListItemDto, Void> accionesColumn;
    @FXML private VBox drawerPane;
    @FXML private Label drawerTitleLabel;
    @FXML private Label drawerBannerLabel;
    @FXML private VBox detailContent;
    @FXML private VBox formContent;
    @FXML private Label detailEstudianteLabel;
    @FXML private Label detailClaseLabel;
    @FXML private Label detailParcialLabel;
    @FXML private Label detailNotaLabel;
    @FXML private Label detailFechaRegistroLabel;
    @FXML private Label detailObservacionLabel;
    @FXML private ComboBox<EstudianteListItemDto> formEstudianteCombo;
    @FXML private ComboBox<ClaseListItemDto> formClaseCombo;
    @FXML private Spinner<Integer> formParcialSpinner;
    @FXML private TextField formNotaField;
    @FXML private DatePicker formFechaRegistroPicker;
    @FXML private TextArea formObservacionArea;
    @FXML private Button detailEditarButton;
    @FXML private Button guardarButton;

    private final CalificacionesListViewModel listViewModel = new CalificacionesListViewModel();
    private final CalificacionDetailViewModel detailViewModel = new CalificacionDetailViewModel();
    private final CalificacionFormViewModel formViewModel = new CalificacionFormViewModel();
    private final ObservableList<EstudianteListItemDto> estudiantes = FXCollections.observableArrayList();
    private final ObservableList<ClaseListItemDto> clases = FXCollections.observableArrayList();
    private final Map<Long, EstudianteListItemDto> estudiantesById = new HashMap<>();
    private final Map<Long, ClaseListItemDto> clasesById = new HashMap<>();

    private AppContext appContext;
    private CalificacionesService calificacionesService;
    private CalificacionesReferenceDataService referenceDataService;
    private CalificacionResponseDto currentCalificacion;
    private boolean initialized;
    private boolean contextApplied;

    private record ReferenceDataLoad(
            ApiResult<PageResponse<EstudianteListItemDto>> estudiantesResult,
            ApiResult<PageResponse<ClaseListItemDto>> clasesResult) {
    }

    @FXML
    private void initialize() {
        configureFilters();
        configureTable();
        configureDrawer();
        bindViewModels();
        initialized = true;
        applyContextIfReady();
    }

    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
        this.calificacionesService = appContext.services().calificacionesService();
        this.referenceDataService = appContext.services().calificacionesReferenceDataService();
        applyContextIfReady();
    }

    @FXML
    private void onBuscar() {
        SearchableComboBoxSupport.commitSelection(estudianteFilterCombo);
        SearchableComboBoxSupport.commitSelection(claseFilterCombo);
        listViewModel.pageProperty().set(0);
        loadCalificaciones();
    }

    @FXML
    private void onLimpiar() {
        estudianteFilterCombo.getSelectionModel().clearSelection();
        claseFilterCombo.getSelectionModel().clearSelection();
        parcialFilterCombo.getSelectionModel().clearSelection();
        listViewModel.pageProperty().set(0);
        loadCalificaciones();
    }

    @FXML
    private void onPaginaAnterior() {
        if (listViewModel.pageProperty().get() <= 0) {
            return;
        }
        listViewModel.pageProperty().set(listViewModel.pageProperty().get() - 1);
        loadCalificaciones();
    }

    @FXML
    private void onPaginaSiguiente() {
        if (!listViewModel.nextPageAvailableProperty().get()) {
            return;
        }
        listViewModel.pageProperty().set(listViewModel.pageProperty().get() + 1);
        loadCalificaciones();
    }

    @FXML
    private void onCrearCalificacion() {
        currentCalificacion = null;
        detailViewModel.visibleProperty().set(false);
        formViewModel.visibleProperty().set(true);
        formViewModel.titleProperty().set("Registrar calificación");
        formViewModel.bannerMessageProperty().set("");
        clearForm();
        showFormPane();
    }

    @FXML
    private void onCerrarDrawer() {
        if (formViewModel.loadingProperty().get() || detailViewModel.loadingProperty().get()) {
            return;
        }
        drawerPane.setVisible(false);
        drawerPane.setManaged(false);
        detailContent.setVisible(false);
        detailContent.setManaged(false);
        formContent.setVisible(false);
        formContent.setManaged(false);
        detailViewModel.bannerMessageProperty().set("");
        formViewModel.bannerMessageProperty().set("");
    }

    @FXML
    private void onEditarDesdeDetalle() {
        if (currentCalificacion != null) {
            openEditForm(currentCalificacion);
        }
    }

    @FXML
    private void onGuardarFormulario() {
        SearchableComboBoxSupport.commitSelection(formEstudianteCombo);
        SearchableComboBoxSupport.commitSelection(formClaseCombo);
        Optional<String> validationError = validateForm();
        if (validationError.isPresent()) {
            formViewModel.bannerMessageProperty().set(validationError.get());
            return;
        }
        formViewModel.loadingProperty().set(true);
        formViewModel.bannerMessageProperty().set("");
        FxExecutors.submitIo(this::submitForm, this::applySaveResult);
    }

    private void applyContextIfReady() {
        if (!initialized || appContext == null || contextApplied) {
            return;
        }
        contextApplied = true;
        loadReferenceDataAndCalificaciones();
    }

    private void configureFilters() {
        SearchableComboBoxSupport.installLocalSearch(
                estudianteFilterCombo,
                estudiantes,
                EstudianteListItemDto::displayName);
        SearchableComboBoxSupport.installLocalSearch(
                formEstudianteCombo,
                estudiantes,
                EstudianteListItemDto::displayName);
        SearchableComboBoxSupport.installLocalSearch(
                claseFilterCombo,
                clases,
                value -> value == null ? "" : claseLabel(value.id()));
        SearchableComboBoxSupport.installLocalSearch(
                formClaseCombo,
                clases,
                value -> value == null ? "" : claseLabel(value.id()));
        parcialFilterCombo.setItems(PARCIALES);
        SpinnerValueFactory.IntegerSpinnerValueFactory parcialFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 2, 1);
        formParcialSpinner.setValueFactory(parcialFactory);
        formParcialSpinner.setEditable(false);
        formEstudianteCombo.valueProperty().addListener((observable, oldValue, newValue) ->
                formViewModel.estudianteIdProperty().set(newValue == null ? null : newValue.id()));
        formClaseCombo.valueProperty().addListener((observable, oldValue, newValue) ->
                formViewModel.claseIdProperty().set(newValue == null ? null : newValue.id()));
        parcialFactory.valueProperty().bindBidirectional(formViewModel.numeroParcialProperty());
        formNotaField.textProperty().bindBidirectional(formViewModel.notaProperty());
        formFechaRegistroPicker.valueProperty().bindBidirectional(formViewModel.fechaRegistroProperty());
        formObservacionArea.textProperty().bindBidirectional(formViewModel.observacionProperty());
    }

    private void configureTable() {
        UiStyling.configureTableToFillWidth(calificacionesTable);
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().id()));
        estudianteColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(estudianteLabel(cellData.getValue().estudianteId())));
        claseColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(claseLabel(cellData.getValue().claseId())));
        parcialColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().numeroParcial()));
        notaColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(formatNota(cellData.getValue().nota())));
        fechaColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(formatDate(cellData.getValue().fechaRegistro())));
        accionesColumn.setCellFactory(column -> new TableCell<>() {
            private final Button verButton = new Button("Ver");
            private final Button editarButton = new Button("Editar");
            private final HBox container = new HBox(6, verButton, editarButton);

            {
                UiStyling.addStyleClasses(verButton, "table-action-button", "action-view-button");
                UiStyling.addStyleClasses(editarButton, "table-action-button", "action-edit-button");
                TooltipSupport.refresh(verButton, editarButton);
                verButton.setOnAction(event -> openFromRow(CalificacionesController.this::showDetailDrawer));
                editarButton.setOnAction(event -> openFromRow(CalificacionesController.this::openEditForm));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || getCurrentItem() == null ? null : container);
            }

            private void openFromRow(Consumer<CalificacionResponseDto> consumer) {
                CalificacionListItemDto currentItem = getCurrentItem();
                if (currentItem != null) {
                    loadCalificacion(currentItem.id(), consumer);
                }
            }

            private CalificacionListItemDto getCurrentItem() {
                if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    return null;
                }
                return getTableView().getItems().get(getIndex());
            }
        });
        calificacionesTable.setItems(listViewModel.items());
        calificacionesTable.setPlaceholder(new Label("Sin resultados para los filtros actuales."));
        calificacionesTable.setRowFactory(table -> {
            TableRow<CalificacionListItemDto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    loadCalificacion(row.getItem().id(), this::showDetailDrawer);
                }
            });
            return row;
        });
    }

    private void configureDrawer() {
        drawerPane.setVisible(false);
        drawerPane.setManaged(false);
        detailContent.setVisible(false);
        detailContent.setManaged(false);
        formContent.setVisible(false);
        formContent.setManaged(false);
        detailEditarButton.setVisible(false);
        guardarButton.setVisible(false);
    }

    private void bindViewModels() {
        statusLabel.textProperty().bind(listViewModel.statusTextProperty());
        totalLabel.textProperty().bind(listViewModel.totalTextProperty());
        pageLabel.textProperty().bind(listViewModel.pageTextProperty());
        buscarButton.disableProperty().bind(listViewModel.loadingProperty());
        limpiarButton.disableProperty().bind(listViewModel.loadingProperty());
        crearCalificacionButton.disableProperty().bind(listViewModel.loadingProperty());
        anteriorButton.disableProperty().bind(listViewModel.loadingProperty().or(listViewModel.previousPageAvailableProperty().not()));
        siguienteButton.disableProperty().bind(listViewModel.loadingProperty().or(listViewModel.nextPageAvailableProperty().not()));
        drawerTitleLabel.textProperty().bind(Bindings.when(detailViewModel.visibleProperty()).then(detailViewModel.titleProperty()).otherwise(formViewModel.titleProperty()));
        drawerBannerLabel.textProperty().bind(Bindings.when(detailViewModel.visibleProperty()).then(detailViewModel.bannerMessageProperty()).otherwise(formViewModel.bannerMessageProperty()));
        UiStyling.bindVisibleWhenTextPresent(drawerBannerLabel);
        UiStyling.installAutoHideBanner(detailViewModel.bannerMessageProperty());
        UiStyling.installAutoHideBanner(formViewModel.bannerMessageProperty());
        detailEstudianteLabel.textProperty().bind(detailViewModel.estudianteProperty());
        detailClaseLabel.textProperty().bind(detailViewModel.claseProperty());
        detailParcialLabel.textProperty().bind(detailViewModel.parcialProperty());
        detailNotaLabel.textProperty().bind(detailViewModel.notaProperty());
        detailFechaRegistroLabel.textProperty().bind(detailViewModel.fechaRegistroProperty());
        detailObservacionLabel.textProperty().bind(detailViewModel.observacionProperty());
        guardarButton.disableProperty().bind(formViewModel.canSubmitBinding().not());
        detailEditarButton.managedProperty().bind(detailEditarButton.visibleProperty());
        guardarButton.managedProperty().bind(guardarButton.visibleProperty());
    }

    private void loadReferenceDataAndCalificaciones() {
        listViewModel.loadingProperty().set(true);
        listViewModel.statusTextProperty().set("Cargando referencias de calificaciones...");
        FxExecutors.submitIo(
                () -> new ReferenceDataLoad(
                        referenceDataService.listarEstudiantes(),
                        referenceDataService.listarClases()),
                result -> applyReferenceData(result.estudiantesResult(), result.clasesResult()));
    }

    private void applyReferenceData(
            ApiResult<PageResponse<EstudianteListItemDto>> estudiantesResult,
            ApiResult<PageResponse<ClaseListItemDto>> clasesResult) {
        if (estudiantesResult.isSuccess()) {
            estudiantes.setAll(estudiantesResult.data().orElseThrow().getItems());
            estudiantesById.clear();
            estudiantes.forEach(item -> estudiantesById.put(item.id(), item));
            estudianteFilterCombo.setItems(estudiantes);
            formEstudianteCombo.setItems(estudiantes);
        }
        if (clasesResult.isSuccess()) {
            clases.setAll(clasesResult.data().orElseThrow().getItems());
            clasesById.clear();
            clases.forEach(item -> clasesById.put(item.id(), item));
            claseFilterCombo.setItems(clases);
            formClaseCombo.setItems(clases);
        }
        loadCalificaciones();
    }

    private void loadCalificaciones() {
        listViewModel.loadingProperty().set(true);
        listViewModel.statusTextProperty().set("Consultando calificaciones...");
        calificacionesTable.setDisable(true);
        FxExecutors.submitIo(() -> calificacionesService.listar(buildListQuery()), this::applyListResult);
    }

    private void applyListResult(ApiResult<PageResponse<CalificacionListItemDto>> result) {
        listViewModel.loadingProperty().set(false);
        calificacionesTable.setDisable(false);
        if (!result.isSuccess()) {
            listViewModel.statusTextProperty().set(result.error().map(error -> error.message()).orElse("No se pudo cargar calificaciones."));
            listViewModel.items().clear();
            listViewModel.totalTextProperty().set("0 registros");
            listViewModel.pageTextProperty().set("P\u00e1gina 1 de 1");
            listViewModel.previousPageAvailableProperty().set(false);
            listViewModel.nextPageAvailableProperty().set(false);
            return;
        }
        PageResponse<CalificacionListItemDto> page = result.data().orElseThrow();
        listViewModel.items().setAll(page.getItems());
        listViewModel.pageProperty().set(page.getPage());
        listViewModel.totalTextProperty().set(page.getTotalElements() + " calificaciones registradas");
        listViewModel.pageTextProperty().set("P\u00e1gina " + (page.getPage() + 1) + " de " + Math.max(page.getTotalPages(), 1));
        listViewModel.previousPageAvailableProperty().set(!page.isFirst());
        listViewModel.nextPageAvailableProperty().set(!page.isLast());
        listViewModel.statusTextProperty().set("Mostrando " + page.getNumberOfElements() + " de " + page.getTotalElements() + " calificaciones.");
    }

    private void loadCalificacion(long calificacionId, Consumer<CalificacionResponseDto> onSuccess) {
        detailViewModel.loadingProperty().set(true);
        detailViewModel.bannerMessageProperty().set("");
        FxExecutors.submitIo(() -> calificacionesService.obtenerPorId(calificacionId), result -> {
            detailViewModel.loadingProperty().set(false);
            if (!result.isSuccess()) {
                detailViewModel.bannerMessageProperty().set(result.error().map(error -> error.message()).orElse("No se pudo cargar la calificación."));
                detailViewModel.visibleProperty().set(true);
                showDetailPane();
                return;
            }
            onSuccess.accept(result.data().orElseThrow());
        });
    }

    private void showDetailDrawer(CalificacionResponseDto calificacion) {
        currentCalificacion = calificacion;
        detailViewModel.visibleProperty().set(true);
        detailViewModel.titleProperty().set("Detalle de la calificación");
        detailViewModel.bannerMessageProperty().set("");
        detailViewModel.estudianteProperty().set(estudianteLabel(calificacion.estudianteId()));
        detailViewModel.claseProperty().set(claseLabel(calificacion.claseId()));
        detailViewModel.parcialProperty().set(String.valueOf(calificacion.numeroParcial()));
        detailViewModel.notaProperty().set(formatNota(calificacion.nota()));
        detailViewModel.fechaRegistroProperty().set(formatDate(calificacion.fechaRegistro()));
        detailViewModel.observacionProperty().set(safeText(calificacion.observacion()));
        formViewModel.visibleProperty().set(false);
        showDetailPane();
    }

    private void openEditForm(CalificacionResponseDto calificacion) {
        currentCalificacion = calificacion;
        detailViewModel.visibleProperty().set(false);
        formViewModel.visibleProperty().set(true);
        formViewModel.titleProperty().set("Editar calificación");
        formViewModel.bannerMessageProperty().set("");
        formEstudianteCombo.setValue(estudiantesById.get(calificacion.estudianteId()));
        formClaseCombo.setValue(clasesById.get(calificacion.claseId()));
        formParcialSpinner.getValueFactory().setValue(calificacion.numeroParcial());
        formNotaField.setText(formatNota(calificacion.nota()));
        formFechaRegistroPicker.setValue(calificacion.fechaRegistro());
        formObservacionArea.setText(calificacion.observacion() == null ? "" : calificacion.observacion());
        showFormPane();
    }

    private void showDetailPane() {
        drawerPane.setVisible(true);
        drawerPane.setManaged(true);
        detailContent.setVisible(true);
        detailContent.setManaged(true);
        formContent.setVisible(false);
        formContent.setManaged(false);
        detailEditarButton.setVisible(true);
        guardarButton.setVisible(false);
    }

    private void showFormPane() {
        drawerPane.setVisible(true);
        drawerPane.setManaged(true);
        detailContent.setVisible(false);
        detailContent.setManaged(false);
        formContent.setVisible(true);
        formContent.setManaged(true);
        detailEditarButton.setVisible(false);
        guardarButton.setVisible(true);
    }

    private ApiResult<CalificacionResponseDto> submitForm() {
        BigDecimal nota = parseNota(formNotaField.getText().trim());
        LocalDate fechaRegistro = formFechaRegistroPicker.getValue();
        String observacion = emptyToNull(formObservacionArea.getText());
        if (currentCalificacion == null) {
            return calificacionesService.crear(new CalificacionCreateRequestDto(
                    formParcialSpinner.getValue(),
                    nota,
                    fechaRegistro,
                    observacion,
                    formEstudianteCombo.getValue().id(),
                    formClaseCombo.getValue().id()));
        }
        return calificacionesService.actualizar(currentCalificacion.id(), new CalificacionUpdateRequestDto(
                formParcialSpinner.getValue(),
                nota,
                fechaRegistro,
                observacion,
                formEstudianteCombo.getValue().id(),
                formClaseCombo.getValue().id()));
    }

    private void applySaveResult(ApiResult<CalificacionResponseDto> result) {
        formViewModel.loadingProperty().set(false);
        if (!result.isSuccess()) {
            formViewModel.bannerMessageProperty().set(result.error().map(error -> error.message()).orElse("No se pudo guardar la calificación."));
            return;
        }
        CalificacionResponseDto saved = result.data().orElseThrow();
        currentCalificacion = saved;
        showDetailDrawer(saved);
        loadCalificaciones();
    }

    private Optional<String> validateForm() {
        if (formEstudianteCombo.getValue() == null) {
            return Optional.of("Selecciona un estudiante.");
        }
        if (formClaseCombo.getValue() == null) {
            return Optional.of("Selecciona una clase.");
        }
        if (formParcialSpinner.getValue() == null) {
            return Optional.of("Selecciona un parcial.");
        }
        if (formParcialSpinner.getValue() < 1 || formParcialSpinner.getValue() > 2) {
            return Optional.of("El parcial debe ser 1 o 2.");
        }
        BigDecimal nota;
        try {
            nota = parseNota(formNotaField.getText().trim());
        } catch (NumberFormatException ex) {
            return Optional.of("La nota debe ser numérica.");
        }
        if (nota.compareTo(BigDecimal.ZERO) < 0 || nota.compareTo(new BigDecimal("10.00")) > 0) {
            return Optional.of("La nota debe estar entre 0 y 10.");
        }
        String observacion = formObservacionArea.getText().trim();
        if (observacion.length() > 500) {
            return Optional.of("La observación no debe exceder 500 caracteres.");
        }
        return Optional.empty();
    }

    private void clearForm() {
        formEstudianteCombo.getSelectionModel().clearSelection();
        formClaseCombo.getSelectionModel().clearSelection();
        formParcialSpinner.getValueFactory().setValue(1);
        formNotaField.clear();
        formFechaRegistroPicker.setValue(null);
        formObservacionArea.clear();
    }

    private Long selectedEstudianteId(ComboBox<EstudianteListItemDto> comboBox) {
        return comboBox.getValue() == null ? null : comboBox.getValue().id();
    }

    private Long selectedClaseId(ComboBox<ClaseListItemDto> comboBox) {
        return comboBox.getValue() == null ? null : comboBox.getValue().id();
    }

    private CalificacionesListQuery buildListQuery() {
        return new CalificacionesListQuery(
                listViewModel.pageProperty().get(),
                PAGE_SIZE,
                selectedEstudianteId(estudianteFilterCombo),
                selectedClaseId(claseFilterCombo),
                parcialFilterCombo.getValue());
    }

    private String estudianteLabel(Long id) {
        if (id == null) {
            return "-";
        }
        EstudianteListItemDto value = estudiantesById.get(id);
        return value == null ? "Estudiante #" + id : value.displayName();
    }

    private String claseLabel(Long id) {
        if (id == null) {
            return "-";
        }
        ClaseListItemDto value = clasesById.get(id);
        if (value == null) {
            return "Clase #" + id;
        }
        return value.diaSemana() + " " + value.horaInicio() + "-" + value.horaFin() + " | Clase #" + value.id();
    }

    private String formatNota(BigDecimal value) {
        return value == null ? "-" : value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String formatDate(LocalDate value) {
        return value == null ? "-" : value.format(DATE_FORMATTER);
    }

    private BigDecimal parseNota(String value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
    }

    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String safeText(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

}
