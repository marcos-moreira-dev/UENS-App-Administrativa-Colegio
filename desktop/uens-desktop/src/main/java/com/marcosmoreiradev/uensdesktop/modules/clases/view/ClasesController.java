package com.marcosmoreiradev.uensdesktop.modules.clases.view;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.dto.AsignaturaListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.clases.dto.ClaseCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.clases.dto.ClaseListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.clases.dto.ClaseResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.clases.dto.ClaseUpdateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.docentes.dto.DocenteListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto.SeccionListItemDto;
import com.marcosmoreiradev.uensdesktop.app.AppContext;
import com.marcosmoreiradev.uensdesktop.app.ContextAwareController;
import com.marcosmoreiradev.uensdesktop.modules.clases.application.ClasesListQuery;
import com.marcosmoreiradev.uensdesktop.modules.clases.application.ClasesReferenceDataService;
import com.marcosmoreiradev.uensdesktop.modules.clases.application.ClasesService;
import com.marcosmoreiradev.uensdesktop.modules.clases.viewmodel.ClaseDetailViewModel;
import com.marcosmoreiradev.uensdesktop.modules.clases.viewmodel.ClaseFormViewModel;
import com.marcosmoreiradev.uensdesktop.modules.clases.viewmodel.ClasesListViewModel;
import com.marcosmoreiradev.uensdesktop.session.Role;
import com.marcosmoreiradev.uensdesktop.ui.command.UiCommand;
import com.marcosmoreiradev.uensdesktop.ui.command.UiCommands;
import com.marcosmoreiradev.uensdesktop.ui.drawer.DrawerCoordinator;
import com.marcosmoreiradev.uensdesktop.ui.fx.SearchableComboBoxSupport;
import com.marcosmoreiradev.uensdesktop.ui.fx.UiStyling;
import com.marcosmoreiradev.uensdesktop.ui.tooltip.TooltipSupport;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

public final class ClasesController implements ContextAwareController {

    private static final int PAGE_SIZE = 20;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FLEXIBLE_TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");
    private static final ObservableList<String> DIAS_SEMANA = FXCollections.observableArrayList(
            "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO");

    @FXML private ComboBox<String> estadoFilter;
    @FXML private ComboBox<SeccionListItemDto> seccionFilterCombo;
    @FXML private ComboBox<AsignaturaListItemDto> asignaturaFilterCombo;
    @FXML private ComboBox<DocenteListItemDto> docenteFilterCombo;
    @FXML private ComboBox<String> diaSemanaFilterCombo;
    @FXML private Button buscarButton;
    @FXML private Button limpiarButton;
    @FXML private Button crearClaseButton;
    @FXML private Button anteriorButton;
    @FXML private Button siguienteButton;
    @FXML private Label totalLabel;
    @FXML private Label pageLabel;
    @FXML private Label statusLabel;
    @FXML private TableView<ClaseListItemDto> clasesTable;
    @FXML private TableColumn<ClaseListItemDto, Long> idColumn;
    @FXML private TableColumn<ClaseListItemDto, String> seccionColumn;
    @FXML private TableColumn<ClaseListItemDto, String> asignaturaColumn;
    @FXML private TableColumn<ClaseListItemDto, String> docenteColumn;
    @FXML private TableColumn<ClaseListItemDto, String> diaSemanaColumn;
    @FXML private TableColumn<ClaseListItemDto, String> horarioColumn;
    @FXML private TableColumn<ClaseListItemDto, String> estadoColumn;
    @FXML private TableColumn<ClaseListItemDto, Void> accionesColumn;
    @FXML private VBox drawerPane;
    @FXML private Label drawerTitleLabel;
    @FXML private Label drawerBannerLabel;
    @FXML private VBox detailContent;
    @FXML private VBox formContent;
    @FXML private Label detailSeccionLabel;
    @FXML private Label detailAsignaturaLabel;
    @FXML private Label detailDocenteLabel;
    @FXML private Label detailDiaSemanaLabel;
    @FXML private Label detailHorarioLabel;
    @FXML private Label detailEstadoLabel;
    @FXML private ComboBox<SeccionListItemDto> formSeccionCombo;
    @FXML private ComboBox<AsignaturaListItemDto> formAsignaturaCombo;
    @FXML private ComboBox<DocenteListItemDto> formDocenteCombo;
    @FXML private ComboBox<String> formDiaSemanaCombo;
    @FXML private TextField formHoraInicioField;
    @FXML private TextField formHoraFinField;
    @FXML private Button detailEditarButton;
    @FXML private Button detailEstadoButton;
    @FXML private Button guardarButton;

    private final ClasesListViewModel listViewModel = new ClasesListViewModel();
    private final ClaseDetailViewModel detailViewModel = new ClaseDetailViewModel();
    private final ClaseFormViewModel formViewModel = new ClaseFormViewModel();
    private final ObservableList<SeccionListItemDto> secciones = FXCollections.observableArrayList();
    private final ObservableList<AsignaturaListItemDto> asignaturas = FXCollections.observableArrayList();
    private final ObservableList<DocenteListItemDto> docentes = FXCollections.observableArrayList();
    private final Map<Long, SeccionListItemDto> seccionesById = new HashMap<>();
    private final Map<Long, AsignaturaListItemDto> asignaturasById = new HashMap<>();
    private final Map<Long, DocenteListItemDto> docentesById = new HashMap<>();

    private AppContext appContext;
    private ClasesService clasesService;
    private ClasesReferenceDataService referenceDataService;
    private ClaseResponseDto currentClase;
    private DrawerCoordinator drawerCoordinator;
    private UiCommand loadReferenceDataCommand;
    private UiCommand loadClasesCommand;
    private UiCommand submitFormCommand;
    private boolean isAdmin;
    private boolean initialized;
    private boolean contextApplied;

    private record ReferenceDataLoad(
            ApiResult<PageResponse<SeccionListItemDto>> seccionesResult,
            ApiResult<PageResponse<AsignaturaListItemDto>> asignaturasResult,
            ApiResult<PageResponse<DocenteListItemDto>> docentesResult) {
    }

    @FXML
    private void initialize() {
        configureFilters();
        configureTable();
        configureDrawer();
        configureFormState();
        bindViewModels();
        initialized = true;
        applyContextIfReady();
    }

    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
        this.clasesService = appContext.services().clasesService();
        this.referenceDataService = appContext.services().clasesReferenceDataService();
        this.isAdmin = appContext.sessionState().role().filter(role -> role == Role.ADMIN).isPresent();
        initializeCommands();
        applyContextIfReady();
    }

    @FXML
    private void onBuscar() {
        SearchableComboBoxSupport.commitSelection(seccionFilterCombo);
        SearchableComboBoxSupport.commitSelection(asignaturaFilterCombo);
        SearchableComboBoxSupport.commitSelection(docenteFilterCombo);
        listViewModel.pageProperty().set(0);
        loadClases();
    }

    @FXML
    private void onLimpiar() {
        estadoFilter.getSelectionModel().selectFirst();
        seccionFilterCombo.getSelectionModel().clearSelection();
        asignaturaFilterCombo.getSelectionModel().clearSelection();
        docenteFilterCombo.getSelectionModel().clearSelection();
        diaSemanaFilterCombo.getSelectionModel().clearSelection();
        listViewModel.pageProperty().set(0);
        loadClases();
    }

    @FXML
    private void onPaginaAnterior() {
        if (listViewModel.pageProperty().get() <= 0) {
            return;
        }
        listViewModel.pageProperty().set(listViewModel.pageProperty().get() - 1);
        loadClases();
    }

    @FXML
    private void onPaginaSiguiente() {
        if (!listViewModel.nextPageAvailableProperty().get()) {
            return;
        }
        listViewModel.pageProperty().set(listViewModel.pageProperty().get() + 1);
        loadClases();
    }

    @FXML
    private void onCrearClase() {
        currentClase = null;
        detailViewModel.visibleProperty().set(false);
        formViewModel.visibleProperty().set(true);
        formViewModel.titleProperty().set("Crear clase");
        formViewModel.bannerMessageProperty().set("");
        clearForm();
        showFormPane();
    }

    @FXML
    private void onCerrarDrawer() {
        if (formViewModel.loadingProperty().get() || detailViewModel.loadingProperty().get()) {
            return;
        }
        drawerCoordinator.hideAll();
        detailViewModel.bannerMessageProperty().set("");
        formViewModel.bannerMessageProperty().set("");
    }

    @FXML
    private void onEditarDesdeDetalle() {
        if (currentClase != null) {
            openEditForm(currentClase);
        }
    }

    @FXML
    private void onCambiarEstadoActual() {
        if (!isAdmin || currentClase == null) {
            return;
        }
        String nextEstado = "ACTIVO".equalsIgnoreCase(currentClase.estado()) ? "INACTIVO" : "ACTIVO";
        boolean confirmed = appContext.feedback().confirm(
                currentWindow(),
                "Confirmar cambio de estado",
                "Cambiar estado de la clase",
                "Se cambiará el estado a " + nextEstado + ".",
                "Confirmar",
                "Cancelar");
        if (!confirmed) {
            return;
        }
        detailViewModel.loadingProperty().set(true);
        detailViewModel.bannerMessageProperty().set("");
        UiCommands.io(() -> clasesService.cambiarEstado(currentClase.id(), nextEstado), this::applyEstadoResult).execute();
    }

    @FXML
    private void onGuardarFormulario() {
        SearchableComboBoxSupport.commitSelection(formSeccionCombo);
        SearchableComboBoxSupport.commitSelection(formAsignaturaCombo);
        SearchableComboBoxSupport.commitSelection(formDocenteCombo);
        Optional<String> validationError = validateForm();
        if (validationError.isPresent()) {
            formViewModel.bannerMessageProperty().set(validationError.get());
            return;
        }
        formViewModel.loadingProperty().set(true);
        formViewModel.bannerMessageProperty().set("");
        submitFormCommand.execute();
    }

    private void applyContextIfReady() {
        if (!initialized || appContext == null || contextApplied) {
            return;
        }
        contextApplied = true;
        refreshRoleControls();
        loadReferenceDataAndClases();
    }

    private void configureFilters() {
        estadoFilter.setItems(FXCollections.observableArrayList("TODOS", "ACTIVO", "INACTIVO"));
        estadoFilter.getSelectionModel().selectFirst();
        diaSemanaFilterCombo.setItems(DIAS_SEMANA);
        formDiaSemanaCombo.setItems(DIAS_SEMANA);
        SearchableComboBoxSupport.installLocalSearch(
                seccionFilterCombo,
                secciones,
                SeccionListItemDto::displayName);
        SearchableComboBoxSupport.installLocalSearch(
                formSeccionCombo,
                secciones,
                SeccionListItemDto::displayName);
        SearchableComboBoxSupport.installLocalSearch(
                asignaturaFilterCombo,
                asignaturas,
                AsignaturaListItemDto::displayName);
        SearchableComboBoxSupport.installLocalSearch(
                formAsignaturaCombo,
                asignaturas,
                AsignaturaListItemDto::displayName);
        SearchableComboBoxSupport.installLocalSearch(
                docenteFilterCombo,
                docentes,
                DocenteListItemDto::displayName);
        SearchableComboBoxSupport.installLocalSearch(
                formDocenteCombo,
                docentes,
                DocenteListItemDto::displayName);
    }

    private void refreshRoleControls() {
        crearClaseButton.setVisible(isAdmin);
        crearClaseButton.setManaged(isAdmin);
        detailEditarButton.setVisible(isAdmin);
        detailEstadoButton.setVisible(isAdmin);
    }

    private void configureTable() {
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().id()));
        seccionColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(seccionLabel(cellData.getValue().seccionId())));
        asignaturaColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(asignaturaLabel(cellData.getValue().asignaturaId())));
        docenteColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(docenteLabel(cellData.getValue().docenteId())));
        diaSemanaColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().diaSemana()));
        horarioColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(formatHorario(cellData.getValue().horaInicio(), cellData.getValue().horaFin())));
        estadoColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().estado()));
        estadoColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().removeAll("status-activo", "status-inactivo");
                    return;
                }
                setText(item);
                getStyleClass().removeAll("status-activo", "status-inactivo");
                getStyleClass().add("ACTIVO".equalsIgnoreCase(item) ? "status-activo" : "status-inactivo");
            }
        });
        accionesColumn.setCellFactory(column -> new TableCell<>() {
            private final Button verButton = new Button("Ver");
            private final Button editarButton = new Button("Editar");
            private final Button estadoButton = new Button("Estado");
            private final HBox container = new HBox(6, verButton, editarButton, estadoButton);

            {
                UiStyling.addStyleClasses(verButton, "table-action-button", "action-view-button");
                UiStyling.addStyleClasses(editarButton, "table-action-button", "action-edit-button");
                UiStyling.addStyleClasses(estadoButton, "table-action-button", "action-state-button");
                TooltipSupport.refresh(verButton, editarButton, estadoButton);
                verButton.setOnAction(event -> openFromRow(ClasesController.this::showDetailDrawer));
                editarButton.setOnAction(event -> openFromRow(ClasesController.this::openEditForm));
                estadoButton.setOnAction(event -> openFromRow(clase -> {
                    currentClase = clase;
                    onCambiarEstadoActual();
                }));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getCurrentItem() == null) {
                    setGraphic(null);
                    return;
                }
                editarButton.setVisible(isAdmin);
                editarButton.setManaged(isAdmin);
                estadoButton.setVisible(isAdmin);
                estadoButton.setManaged(isAdmin);
                estadoButton.setText("ACTIVO".equalsIgnoreCase(getCurrentItem().estado()) ? "Inactivar" : "Activar");
                setGraphic(container);
            }

            private void openFromRow(Consumer<ClaseResponseDto> consumer) {
                ClaseListItemDto currentItem = getCurrentItem();
                if (currentItem != null) {
                    loadClase(currentItem.id(), consumer);
                }
            }

            private ClaseListItemDto getCurrentItem() {
                if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    return null;
                }
                return getTableView().getItems().get(getIndex());
            }
        });
        clasesTable.setItems(listViewModel.items());
        clasesTable.setPlaceholder(new Label("Sin resultados para los filtros actuales."));
        clasesTable.setRowFactory(table -> {
            TableRow<ClaseListItemDto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    loadClase(row.getItem().id(), this::showDetailDrawer);
                }
            });
            return row;
        });
    }

    private void configureDrawer() {
        drawerCoordinator = new DrawerCoordinator(drawerPane, detailContent, formContent);
        drawerCoordinator.hideAll();
        detailEditarButton.setVisible(false);
        detailEstadoButton.setVisible(false);
        guardarButton.setVisible(false);
    }

    private void configureFormState() {
        formSeccionCombo.valueProperty().addListener((observable, oldValue, newValue) ->
                formViewModel.seccionIdProperty().set(newValue == null ? null : newValue.id()));
        formAsignaturaCombo.valueProperty().addListener((observable, oldValue, newValue) ->
                formViewModel.asignaturaIdProperty().set(newValue == null ? null : newValue.id()));
        formDocenteCombo.valueProperty().addListener((observable, oldValue, newValue) ->
                formViewModel.docenteIdProperty().set(newValue == null ? null : newValue.id()));
        formDiaSemanaCombo.valueProperty().addListener((observable, oldValue, newValue) ->
                formViewModel.diaSemanaProperty().set(newValue == null ? "" : newValue));
        formHoraInicioField.textProperty().addListener((observable, oldValue, newValue) ->
                formViewModel.horaInicioProperty().set(newValue == null ? "" : newValue.trim()));
        formHoraFinField.textProperty().addListener((observable, oldValue, newValue) ->
                formViewModel.horaFinProperty().set(newValue == null ? "" : newValue.trim()));
        syncFormViewModel();
    }

    private void bindViewModels() {
        statusLabel.textProperty().bind(listViewModel.statusTextProperty());
        totalLabel.textProperty().bind(listViewModel.totalTextProperty());
        pageLabel.textProperty().bind(listViewModel.pageTextProperty());
        buscarButton.disableProperty().bind(listViewModel.loadingProperty());
        limpiarButton.disableProperty().bind(listViewModel.loadingProperty());
        crearClaseButton.disableProperty().bind(listViewModel.loadingProperty());
        anteriorButton.disableProperty().bind(listViewModel.loadingProperty().or(listViewModel.previousPageAvailableProperty().not()));
        siguienteButton.disableProperty().bind(listViewModel.loadingProperty().or(listViewModel.nextPageAvailableProperty().not()));
        drawerTitleLabel.textProperty().bind(Bindings.when(detailViewModel.visibleProperty()).then(detailViewModel.titleProperty()).otherwise(formViewModel.titleProperty()));
        drawerBannerLabel.textProperty().bind(Bindings.when(detailViewModel.visibleProperty()).then(detailViewModel.bannerMessageProperty()).otherwise(formViewModel.bannerMessageProperty()));
        UiStyling.bindVisibleWhenTextPresent(drawerBannerLabel);
        UiStyling.installAutoHideBanner(detailViewModel.bannerMessageProperty());
        UiStyling.installAutoHideBanner(formViewModel.bannerMessageProperty());
        detailSeccionLabel.textProperty().bind(detailViewModel.seccionProperty());
        detailAsignaturaLabel.textProperty().bind(detailViewModel.asignaturaProperty());
        detailDocenteLabel.textProperty().bind(detailViewModel.docenteProperty());
        detailDiaSemanaLabel.textProperty().bind(detailViewModel.diaSemanaProperty());
        detailHorarioLabel.textProperty().bind(detailViewModel.horarioProperty());
        detailEstadoLabel.textProperty().bind(detailViewModel.estadoProperty());
        guardarButton.disableProperty().bind(formViewModel.canSubmitBinding().not());
        detailEditarButton.managedProperty().bind(detailEditarButton.visibleProperty());
        detailEstadoButton.managedProperty().bind(detailEstadoButton.visibleProperty());
        guardarButton.managedProperty().bind(guardarButton.visibleProperty());
    }

    private void loadReferenceDataAndClases() {
        listViewModel.loadingProperty().set(true);
        listViewModel.statusTextProperty().set("Cargando cat\u00e1logos de clases...");
        loadReferenceDataCommand.execute();
    }

    private void applyReferenceData(
            ApiResult<PageResponse<SeccionListItemDto>> seccionesResult,
            ApiResult<PageResponse<AsignaturaListItemDto>> asignaturasResult,
            ApiResult<PageResponse<DocenteListItemDto>> docentesResult) {
        if (seccionesResult.isSuccess()) {
            secciones.setAll(seccionesResult.data().orElseThrow().getItems());
            seccionesById.clear();
            secciones.forEach(item -> seccionesById.put(item.id(), item));
        }
        if (asignaturasResult.isSuccess()) {
            asignaturas.setAll(asignaturasResult.data().orElseThrow().getItems());
            asignaturasById.clear();
            asignaturas.forEach(item -> asignaturasById.put(item.id(), item));
        }
        if (docentesResult.isSuccess()) {
            docentes.setAll(docentesResult.data().orElseThrow().getItems());
            docentesById.clear();
            docentes.forEach(item -> docentesById.put(item.id(), item));
        }
        loadClases();
    }

    private void loadClases() {
        listViewModel.loadingProperty().set(true);
        listViewModel.statusTextProperty().set("Consultando clases...");
        clasesTable.setDisable(true);
        loadClasesCommand.execute();
    }

    private void applyListResult(ApiResult<PageResponse<ClaseListItemDto>> result) {
        listViewModel.loadingProperty().set(false);
        clasesTable.setDisable(false);
        if (!result.isSuccess()) {
            listViewModel.statusTextProperty().set(result.error().map(error -> error.message()).orElse("No se pudo cargar clases."));
            listViewModel.items().clear();
            listViewModel.totalTextProperty().set("0 registros");
            listViewModel.pageTextProperty().set("P\u00e1gina 1 de 1");
            listViewModel.previousPageAvailableProperty().set(false);
            listViewModel.nextPageAvailableProperty().set(false);
            return;
        }
        PageResponse<ClaseListItemDto> page = result.data().orElseThrow();
        listViewModel.items().setAll(page.getItems());
        listViewModel.pageProperty().set(page.getPage());
        listViewModel.totalTextProperty().set(page.getTotalElements() + " clases registradas");
        listViewModel.pageTextProperty().set("P\u00e1gina " + (page.getPage() + 1) + " de " + Math.max(page.getTotalPages(), 1));
        listViewModel.previousPageAvailableProperty().set(!page.isFirst());
        listViewModel.nextPageAvailableProperty().set(!page.isLast());
        listViewModel.statusTextProperty().set("Mostrando " + page.getNumberOfElements() + " de " + page.getTotalElements() + " clases.");
    }

    private void loadClase(long claseId, Consumer<ClaseResponseDto> onSuccess) {
        detailViewModel.loadingProperty().set(true);
        detailViewModel.bannerMessageProperty().set("");
        UiCommands.io(() -> clasesService.obtenerPorId(claseId), result -> {
            detailViewModel.loadingProperty().set(false);
            if (!result.isSuccess()) {
                detailViewModel.bannerMessageProperty().set(result.error().map(error -> error.message()).orElse("No se pudo cargar la clase."));
                detailViewModel.visibleProperty().set(true);
                showDetailPane();
                return;
            }
            onSuccess.accept(result.data().orElseThrow());
        }).execute();
    }

    private void showDetailDrawer(ClaseResponseDto clase) {
        currentClase = clase;
        detailViewModel.visibleProperty().set(true);
        detailViewModel.titleProperty().set("Detalle de la clase");
        detailViewModel.bannerMessageProperty().set("");
        detailViewModel.seccionProperty().set(seccionLabel(clase.seccionId()));
        detailViewModel.asignaturaProperty().set(asignaturaLabel(clase.asignaturaId()));
        detailViewModel.docenteProperty().set(docenteLabel(clase.docenteId()));
        detailViewModel.diaSemanaProperty().set(clase.diaSemana());
        detailViewModel.horarioProperty().set(formatHorario(clase.horaInicio(), clase.horaFin()));
        detailViewModel.estadoProperty().set(clase.estado());
        detailEstadoButton.setText("ACTIVO".equalsIgnoreCase(clase.estado()) ? "Inactivar" : "Activar");
        formViewModel.visibleProperty().set(false);
        showDetailPane();
    }

    private void openEditForm(ClaseResponseDto clase) {
        currentClase = clase;
        detailViewModel.visibleProperty().set(false);
        formViewModel.visibleProperty().set(true);
        formViewModel.titleProperty().set("Editar clase");
        formViewModel.bannerMessageProperty().set("");
        formSeccionCombo.setValue(seccionesById.get(clase.seccionId()));
        formAsignaturaCombo.setValue(asignaturasById.get(clase.asignaturaId()));
        formDocenteCombo.setValue(clase.docenteId() == null ? null : docentesById.get(clase.docenteId()));
        formDiaSemanaCombo.setValue(clase.diaSemana());
        formHoraInicioField.setText(formatTime(clase.horaInicio()));
        formHoraFinField.setText(formatTime(clase.horaFin()));
        syncFormViewModel();
        showFormPane();
    }

    private void showDetailPane() {
        drawerCoordinator.showOnly(detailContent);
        detailEditarButton.setVisible(isAdmin);
        detailEstadoButton.setVisible(isAdmin);
        guardarButton.setVisible(false);
    }

    private void showFormPane() {
        drawerCoordinator.showOnly(formContent);
        detailEditarButton.setVisible(false);
        detailEstadoButton.setVisible(false);
        guardarButton.setVisible(true);
    }

    private ApiResult<ClaseResponseDto> submitForm() {
        Long docenteId = formDocenteCombo.getValue() == null ? null : formDocenteCombo.getValue().id();
        LocalTime horaInicio = parseTime(formHoraInicioField.getText().trim());
        LocalTime horaFin = parseTime(formHoraFinField.getText().trim());
        if (currentClase == null) {
            return clasesService.crear(new ClaseCreateRequestDto(
                    formSeccionCombo.getValue().id(),
                    formAsignaturaCombo.getValue().id(),
                    docenteId,
                    formDiaSemanaCombo.getValue(),
                    horaInicio,
                    horaFin));
        }
        return clasesService.actualizar(currentClase.id(), new ClaseUpdateRequestDto(
                formSeccionCombo.getValue().id(),
                formAsignaturaCombo.getValue().id(),
                docenteId,
                formDiaSemanaCombo.getValue(),
                horaInicio,
                horaFin,
                currentClase.estado()));
    }

    private void applySaveResult(ApiResult<ClaseResponseDto> result) {
        formViewModel.loadingProperty().set(false);
        if (!result.isSuccess()) {
            formViewModel.bannerMessageProperty().set(result.error().map(error -> error.message()).orElse("No se pudo guardar la clase."));
            return;
        }
        ClaseResponseDto saved = result.data().orElseThrow();
        currentClase = saved;
        showDetailDrawer(saved);
        loadClases();
    }

    private void applyEstadoResult(ApiResult<ClaseResponseDto> result) {
        detailViewModel.loadingProperty().set(false);
        if (!result.isSuccess()) {
            detailViewModel.bannerMessageProperty().set(result.error().map(error -> error.message()).orElse("No se pudo cambiar el estado de la clase."));
            return;
        }
        ClaseResponseDto updated = result.data().orElseThrow();
        currentClase = updated;
        showDetailDrawer(updated);
        loadClases();
    }

    private Optional<String> validateForm() {
        if (formSeccionCombo.getValue() == null) {
            return Optional.of("Selecciona una secci\u00f3n.");
        }
        if (formAsignaturaCombo.getValue() == null) {
            return Optional.of("Selecciona una asignatura.");
        }
        if (formDiaSemanaCombo.getValue() == null || formDiaSemanaCombo.getValue().isBlank()) {
            return Optional.of("Selecciona un d\u00eda de la semana.");
        }
        LocalTime horaInicio;
        LocalTime horaFin;
        try {
            horaInicio = parseTime(formHoraInicioField.getText().trim());
            horaFin = parseTime(formHoraFinField.getText().trim());
        } catch (DateTimeParseException ex) {
            return Optional.of("Las horas deben tener formato HH:mm o H:mm.");
        }
        if (!horaFin.isAfter(horaInicio)) {
            return Optional.of("La hora de fin debe ser mayor que la hora de inicio.");
        }
        formHoraInicioField.setText(formatTime(horaInicio));
        formHoraFinField.setText(formatTime(horaFin));
        syncFormViewModel();
        return Optional.empty();
    }

    private void clearForm() {
        formSeccionCombo.getSelectionModel().clearSelection();
        formAsignaturaCombo.getSelectionModel().clearSelection();
        formDocenteCombo.getSelectionModel().clearSelection();
        formDiaSemanaCombo.getSelectionModel().clearSelection();
        formHoraInicioField.clear();
        formHoraFinField.clear();
        syncFormViewModel();
    }

    private String normalizeEstadoFilter() {
        String value = estadoFilter.getValue();
        if (value == null || "TODOS".equalsIgnoreCase(value)) {
            return null;
        }
        return value;
    }

    private ClasesListQuery buildListQuery() {
        return new ClasesListQuery(
                listViewModel.pageProperty().get(),
                PAGE_SIZE,
                normalizeEstadoFilter(),
                selectedSeccionId(seccionFilterCombo),
                selectedAsignaturaId(asignaturaFilterCombo),
                selectedDocenteId(docenteFilterCombo),
                diaSemanaFilterCombo.getValue());
    }

    private Long selectedSeccionId(ComboBox<SeccionListItemDto> comboBox) {
        return comboBox.getValue() == null ? null : comboBox.getValue().id();
    }

    private Long selectedAsignaturaId(ComboBox<AsignaturaListItemDto> comboBox) {
        return comboBox.getValue() == null ? null : comboBox.getValue().id();
    }

    private Long selectedDocenteId(ComboBox<DocenteListItemDto> comboBox) {
        return comboBox.getValue() == null ? null : comboBox.getValue().id();
    }

    private String seccionLabel(Long id) {
        if (id == null) {
            return "-";
        }
        SeccionListItemDto value = seccionesById.get(id);
        return value == null ? "Secci\u00f3n #" + id : value.displayName();
    }

    private String asignaturaLabel(Long id) {
        if (id == null) {
            return "-";
        }
        AsignaturaListItemDto value = asignaturasById.get(id);
        return value == null ? "Asignatura #" + id : value.displayName();
    }

    private String docenteLabel(Long id) {
        if (id == null) {
            return "Sin docente";
        }
        DocenteListItemDto value = docentesById.get(id);
        return value == null ? "Docente #" + id : value.displayName();
    }

    private String formatHorario(LocalTime inicio, LocalTime fin) {
        return formatTime(inicio) + " - " + formatTime(fin);
    }

    private String formatTime(LocalTime value) {
        return value == null ? "--:--" : value.format(TIME_FORMATTER);
    }

    private LocalTime parseTime(String value) {
        try {
            return LocalTime.parse(value, TIME_FORMATTER);
        } catch (DateTimeParseException ex) {
            return LocalTime.parse(value, FLEXIBLE_TIME_FORMATTER);
        }
    }

    private void syncFormViewModel() {
        formViewModel.seccionIdProperty().set(selectedSeccionId(formSeccionCombo));
        formViewModel.asignaturaIdProperty().set(selectedAsignaturaId(formAsignaturaCombo));
        formViewModel.docenteIdProperty().set(selectedDocenteId(formDocenteCombo));
        formViewModel.diaSemanaProperty().set(formDiaSemanaCombo.getValue() == null ? "" : formDiaSemanaCombo.getValue());
        formViewModel.horaInicioProperty().set(formHoraInicioField.getText() == null ? "" : formHoraInicioField.getText().trim());
        formViewModel.horaFinProperty().set(formHoraFinField.getText() == null ? "" : formHoraFinField.getText().trim());
    }

    private void initializeCommands() {
        loadReferenceDataCommand = UiCommands.io(
                () -> new ReferenceDataLoad(
                        referenceDataService.listarSecciones(),
                        referenceDataService.listarAsignaturas(),
                        referenceDataService.listarDocentes()),
                result -> applyReferenceData(
                        result.seccionesResult(),
                        result.asignaturasResult(),
                        result.docentesResult()));
        loadClasesCommand = UiCommands.io(() -> clasesService.listar(buildListQuery()), this::applyListResult);
        submitFormCommand = UiCommands.io(this::submitForm, this::applySaveResult);
    }

    private Window currentWindow() {
        return drawerPane.getScene() == null ? null : drawerPane.getScene().getWindow();
    }

}
