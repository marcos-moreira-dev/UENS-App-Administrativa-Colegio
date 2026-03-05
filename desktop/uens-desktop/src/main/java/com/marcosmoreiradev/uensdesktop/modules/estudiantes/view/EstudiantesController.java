package com.marcosmoreiradev.uensdesktop.modules.estudiantes.view;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto.EstudianteCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto.EstudianteListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto.EstudianteResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto.EstudianteUpdateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.representantes.dto.RepresentanteLegalListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto.SeccionListItemDto;
import com.marcosmoreiradev.uensdesktop.app.AppContext;
import com.marcosmoreiradev.uensdesktop.app.ContextAwareController;
import com.marcosmoreiradev.uensdesktop.modules.estudiantes.application.EstudiantesReferenceDataService;
import com.marcosmoreiradev.uensdesktop.modules.estudiantes.application.EstudiantesService;
import com.marcosmoreiradev.uensdesktop.modules.estudiantes.application.EstudiantesListQuery;
import com.marcosmoreiradev.uensdesktop.modules.estudiantes.model.FormMode;
import com.marcosmoreiradev.uensdesktop.modules.estudiantes.viewmodel.EstudianteDetailViewModel;
import com.marcosmoreiradev.uensdesktop.modules.estudiantes.viewmodel.EstudianteFormViewModel;
import com.marcosmoreiradev.uensdesktop.modules.estudiantes.viewmodel.EstudiantesListViewModel;
import com.marcosmoreiradev.uensdesktop.session.Role;
import com.marcosmoreiradev.uensdesktop.ui.command.UiCommand;
import com.marcosmoreiradev.uensdesktop.ui.command.UiCommands;
import com.marcosmoreiradev.uensdesktop.ui.drawer.DrawerCoordinator;
import com.marcosmoreiradev.uensdesktop.ui.fx.FxExecutors;
import com.marcosmoreiradev.uensdesktop.ui.fx.SearchableComboBoxSupport;
import com.marcosmoreiradev.uensdesktop.ui.fx.UiStyling;
import com.marcosmoreiradev.uensdesktop.ui.tooltip.TooltipSupport;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

public final class EstudiantesController implements ContextAwareController {

    private static final int PAGE_SIZE = 20;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private TextField queryField;

    @FXML
    private ComboBox<String> estadoFilter;

    @FXML
    private ComboBox<RepresentanteLegalListItemDto> representanteFilterCombo;

    @FXML
    private ComboBox<SeccionListItemDto> seccionFilterCombo;

    @FXML
    private Button buscarButton;

    @FXML
    private Button limpiarButton;

    @FXML
    private Button crearEstudianteButton;

    @FXML
    private Button anteriorButton;

    @FXML
    private Button siguienteButton;

    @FXML
    private Label totalLabel;

    @FXML
    private Label pageLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private TableView<EstudianteListItemDto> estudiantesTable;

    @FXML
    private TableColumn<EstudianteListItemDto, Long> idColumn;

    @FXML
    private TableColumn<EstudianteListItemDto, String> nombresColumn;

    @FXML
    private TableColumn<EstudianteListItemDto, String> apellidosColumn;

    @FXML
    private TableColumn<EstudianteListItemDto, LocalDate> fechaNacimientoColumn;

    @FXML
    private TableColumn<EstudianteListItemDto, String> estadoColumn;

    @FXML
    private TableColumn<EstudianteListItemDto, String> representanteColumn;

    @FXML
    private TableColumn<EstudianteListItemDto, String> seccionColumn;

    @FXML
    private TableColumn<EstudianteListItemDto, Void> accionesColumn;

    @FXML
    private VBox drawerPane;

    @FXML
    private Label drawerTitleLabel;

    @FXML
    private Label drawerBannerLabel;

    @FXML
    private VBox detailContent;

    @FXML
    private VBox formContent;

    @FXML
    private Label detailNombresLabel;

    @FXML
    private Label detailApellidosLabel;

    @FXML
    private Label detailFechaNacimientoLabel;

    @FXML
    private Label detailEstadoLabel;

    @FXML
    private Label detailRepresentanteLabel;

    @FXML
    private Label detailSeccionLabel;

    @FXML
    private TextField formNombresField;

    @FXML
    private TextField formApellidosField;

    @FXML
    private DatePicker formFechaNacimientoPicker;

    @FXML
    private ComboBox<RepresentanteLegalListItemDto> representanteCombo;

    @FXML
    private ComboBox<SeccionListItemDto> seccionCombo;

    @FXML
    private Label formRepresentanteResumenLabel;

    @FXML
    private Label formSeccionResumenLabel;

    @FXML
    private VBox formNombresBox;

    @FXML
    private VBox formApellidosBox;

    @FXML
    private VBox formFechaNacimientoBox;

    @FXML
    private VBox formRepresentanteBox;

    @FXML
    private VBox formSeccionBox;

    @FXML
    private Button detailEditarButton;

    @FXML
    private Button detailEstadoButton;

    @FXML
    private Button detailAsignarSeccionButton;

    @FXML
    private Button guardarButton;

    private final EstudiantesListViewModel listViewModel = new EstudiantesListViewModel();
    private final EstudianteDetailViewModel detailViewModel = new EstudianteDetailViewModel();
    private final EstudianteFormViewModel formViewModel = new EstudianteFormViewModel();
    private final ObservableList<RepresentanteLegalListItemDto> representantes = FXCollections.observableArrayList();
    private final ObservableList<SeccionListItemDto> secciones = FXCollections.observableArrayList();
    private final Map<Long, RepresentanteLegalListItemDto> representantesById = new HashMap<>();
    private final Map<Long, SeccionListItemDto> seccionesById = new HashMap<>();

    private AppContext appContext;
    private EstudiantesService estudiantesService;
    private EstudiantesReferenceDataService referenceDataService;
    private EstudianteResponseDto currentEstudiante;
    private DrawerCoordinator drawerCoordinator;
    private UiCommand loadReferenceDataCommand;
    private UiCommand loadEstudiantesCommand;
    private UiCommand submitFormCommand;
    private boolean isAdmin;
    private boolean initialized;
    private boolean contextApplied;

    private record ReferenceDataLoad(
            ApiResult<PageResponse<RepresentanteLegalListItemDto>> representantesResult,
            ApiResult<PageResponse<SeccionListItemDto>> seccionesResult) {
    }

    @FXML
    private void initialize() {
        configureFilters();
        configureTable();
        configureDrawer();
        configureForm();
        bindViewModels();
        initialized = true;
        applyContextIfReady();
    }

    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
        this.estudiantesService = appContext.services().estudiantesService();
        this.referenceDataService = appContext.services().estudiantesReferenceDataService();
        this.isAdmin = appContext.sessionState().role().filter(role -> role == Role.ADMIN).isPresent();
        initializeCommands();
        applyContextIfReady();
    }

    @FXML
    private void onBuscar() {
        SearchableComboBoxSupport.commitSelection(representanteFilterCombo);
        SearchableComboBoxSupport.commitSelection(seccionFilterCombo);
        listViewModel.pageProperty().set(0);
        loadEstudiantes();
    }

    @FXML
    private void onLimpiar() {
        listViewModel.queryTextProperty().set("");
        estadoFilter.getSelectionModel().selectFirst();
        representanteFilterCombo.getSelectionModel().clearSelection();
        seccionFilterCombo.getSelectionModel().clearSelection();
        listViewModel.pageProperty().set(0);
        loadEstudiantes();
    }

    @FXML
    private void onPaginaAnterior() {
        if (listViewModel.pageProperty().get() <= 0) {
            return;
        }
        listViewModel.pageProperty().set(listViewModel.pageProperty().get() - 1);
        loadEstudiantes();
    }

    @FXML
    private void onPaginaSiguiente() {
        if (!listViewModel.nextPageAvailableProperty().get()) {
            return;
        }
        listViewModel.pageProperty().set(listViewModel.pageProperty().get() + 1);
        loadEstudiantes();
    }

    @FXML
    private void onCrearEstudiante() {
        currentEstudiante = null;
        detailViewModel.visibleProperty().set(false);
        formViewModel.modeProperty().set(FormMode.CREATE);
        formViewModel.titleProperty().set("Crear estudiante");
        formViewModel.bannerMessageProperty().set("");
        detailViewModel.bannerMessageProperty().set("");
        formNombresField.clear();
        formApellidosField.clear();
        formFechaNacimientoPicker.setValue(null);
        representanteCombo.getSelectionModel().clearSelection();
        seccionCombo.getSelectionModel().clearSelection();
        formViewModel.selectedRepresentanteTextProperty().set("Selecciona un representante");
        formViewModel.selectedSeccionTextProperty().set("Sin sección asignada");
        formViewModel.sectionSelectedProperty().set(false);
        formViewModel.visibleProperty().set(true);
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
        if (currentEstudiante == null) {
            return;
        }
        openEditForm(currentEstudiante);
    }

    @FXML
    private void onGuardarFormulario() {
        SearchableComboBoxSupport.commitSelection(representanteCombo);
        SearchableComboBoxSupport.commitSelection(seccionCombo);
        Optional<String> validationError = validateForm();
        if (validationError.isPresent()) {
            formViewModel.bannerMessageProperty().set(validationError.get());
            return;
        }

        formViewModel.loadingProperty().set(true);
        formViewModel.bannerMessageProperty().set("");
        submitFormCommand.execute();
    }

    @FXML
    private void onCambiarEstadoActual() {
        if (!isAdmin || currentEstudiante == null) {
            return;
        }

        String nextEstado = "ACTIVO".equalsIgnoreCase(currentEstudiante.estado()) ? "INACTIVO" : "ACTIVO";
        if (!appContext.feedback().confirm(
                currentWindow(),
                "Confirmar cambio de estado",
                "Cambiar estado del estudiante",
                "Se cambiará el estado a " + nextEstado + ".",
                "Confirmar",
                "Cancelar")) {
            return;
        }

        detailViewModel.loadingProperty().set(true);
        detailViewModel.bannerMessageProperty().set("");
        UiCommands.io(
                () -> estudiantesService.cambiarEstado(currentEstudiante.id(), nextEstado),
                this::applyEstadoResult).execute();
    }

    private void configureFilters() {
        estadoFilter.setItems(FXCollections.observableArrayList("TODOS", "ACTIVO", "INACTIVO"));
        estadoFilter.getSelectionModel().selectFirst();
        queryField.textProperty().bindBidirectional(listViewModel.queryTextProperty());
        representanteFilterCombo.setPromptText("Todos los representantes");
        seccionFilterCombo.setPromptText("Todas las secciones");
        SearchableComboBoxSupport.installLocalSearch(
                representanteFilterCombo,
                representantes,
                RepresentanteLegalListItemDto::displayName);
        SearchableComboBoxSupport.installLocalSearch(
                seccionFilterCombo,
                secciones,
                SeccionListItemDto::displayName);
    }

    private void configureTable() {
        UiStyling.configureTableToFillWidth(estudiantesTable);
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().id()));
        nombresColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().nombres()));
        apellidosColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().apellidos()));
        fechaNacimientoColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().fechaNacimiento()));
        estadoColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().estado()));
        representanteColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
                lookupRepresentante(cellData.getValue().representanteLegalId())));
        seccionColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
                lookupSeccion(cellData.getValue().seccionId())));
        fechaNacimientoColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : DATE_FORMATTER.format(item));
            }
        });
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
                estadoButton.setVisible(isAdmin);
                estadoButton.setManaged(isAdmin);
                TooltipSupport.refresh(verButton, editarButton, estadoButton);
                verButton.setOnAction(event -> {
                    EstudianteListItemDto currentItem = getCurrentItem();
                    if (currentItem != null) {
                        openDetail(currentItem);
                    }
                });
                editarButton.setOnAction(event -> {
                    EstudianteListItemDto currentItem = getCurrentItem();
                    if (currentItem != null) {
                        loadEstudiante(currentItem.id(), EstudiantesController.this::openEditForm);
                    }
                });
                estadoButton.setOnAction(event -> {
                    EstudianteListItemDto currentItem = getCurrentItem();
                    if (currentItem != null) {
                        toggleEstado(currentItem);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getCurrentItem() == null) {
                    setGraphic(null);
                    return;
                }
                estadoButton.setVisible(isAdmin);
                estadoButton.setManaged(isAdmin);
                estadoButton.setText("ACTIVO".equalsIgnoreCase(getCurrentItem().estado()) ? "Inactivar" : "Activar");
                setGraphic(container);
            }

            private EstudianteListItemDto getCurrentItem() {
                if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    return null;
                }
                return getTableView().getItems().get(getIndex());
            }
        });

        estudiantesTable.setItems(listViewModel.items());
        estudiantesTable.setPlaceholder(new Label("Sin resultados para los filtros actuales."));
        estudiantesTable.setRowFactory(table -> {
            TableRow<EstudianteListItemDto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openDetail(row.getItem());
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
        detailAsignarSeccionButton.setVisible(false);
        guardarButton.setVisible(false);
    }

    private void configureForm() {
        SearchableComboBoxSupport.installLocalSearch(
                representanteCombo,
                representantes,
                RepresentanteLegalListItemDto::displayName);
        SearchableComboBoxSupport.installLocalSearch(
                seccionCombo,
                secciones,
                SeccionListItemDto::displayName);
        representanteCombo.valueProperty().addListener((observable, oldValue, newValue) -> formViewModel
                .selectedRepresentanteTextProperty()
                .set(newValue == null ? "Selecciona un representante" : newValue.displayName()));
        seccionCombo.valueProperty().addListener((observable, oldValue, newValue) -> formViewModel
                .selectedSeccionTextProperty()
                .set(newValue == null ? "Sin sección asignada" : newValue.displayName()));
        seccionCombo.valueProperty().addListener((observable, oldValue, newValue) ->
                formViewModel.sectionSelectedProperty().set(newValue != null));
        formNombresField.textProperty().bindBidirectional(formViewModel.nombresProperty());
        formApellidosField.textProperty().bindBidirectional(formViewModel.apellidosProperty());
        formFechaNacimientoPicker.valueProperty().bindBidirectional(formViewModel.fechaNacimientoProperty());
    }

    private void bindViewModels() {
        statusLabel.textProperty().bind(listViewModel.statusTextProperty());
        totalLabel.textProperty().bind(listViewModel.totalTextProperty());
        pageLabel.textProperty().bind(listViewModel.pageTextProperty());
        anteriorButton.disableProperty().bind(listViewModel.loadingProperty().or(listViewModel.previousPageAvailableProperty().not()));
        siguienteButton.disableProperty().bind(listViewModel.loadingProperty().or(listViewModel.nextPageAvailableProperty().not()));
        buscarButton.disableProperty().bind(listViewModel.loadingProperty());
        limpiarButton.disableProperty().bind(listViewModel.loadingProperty());
        crearEstudianteButton.disableProperty().bind(listViewModel.loadingProperty());
        drawerTitleLabel.textProperty().bind(Bindings.when(detailViewModel.visibleProperty())
                .then(detailViewModel.titleProperty())
                .otherwise(formViewModel.titleProperty()));
        drawerBannerLabel.textProperty().bind(Bindings.when(detailViewModel.visibleProperty())
                .then(detailViewModel.bannerMessageProperty())
                .otherwise(formViewModel.bannerMessageProperty()));
        UiStyling.bindVisibleWhenTextPresent(drawerBannerLabel);
        UiStyling.installAutoHideBanner(detailViewModel.bannerMessageProperty());
        UiStyling.installAutoHideBanner(formViewModel.bannerMessageProperty());
        detailNombresLabel.textProperty().bind(detailViewModel.nombresProperty());
        detailApellidosLabel.textProperty().bind(detailViewModel.apellidosProperty());
        detailFechaNacimientoLabel.textProperty().bind(detailViewModel.fechaNacimientoProperty());
        detailEstadoLabel.textProperty().bind(detailViewModel.estadoProperty());
        detailRepresentanteLabel.textProperty().bind(detailViewModel.representanteProperty());
        detailSeccionLabel.textProperty().bind(detailViewModel.seccionProperty());
        formRepresentanteResumenLabel.textProperty().bind(formViewModel.selectedRepresentanteTextProperty());
        formSeccionResumenLabel.textProperty().bind(formViewModel.selectedSeccionTextProperty());
        guardarButton.disableProperty().bind(formViewModel.canSubmitBinding().not());
        detailEditarButton.managedProperty().bind(detailEditarButton.visibleProperty());
        detailEstadoButton.managedProperty().bind(detailEstadoButton.visibleProperty());
        detailAsignarSeccionButton.managedProperty().bind(detailAsignarSeccionButton.visibleProperty());
        guardarButton.managedProperty().bind(guardarButton.visibleProperty());
    }

    private void refreshRoleControls() {
        detailEstadoButton.setVisible(isAdmin);
    }

    private void applyContextIfReady() {
        if (!initialized || appContext == null || contextApplied) {
            return;
        }
        contextApplied = true;
        refreshRoleControls();
        loadReferenceData();
        loadEstudiantes();
    }

    private void loadReferenceData() {
        loadReferenceDataCommand.execute();
    }

    private void applyReferenceDataLoad(ReferenceDataLoad result) {
        applyReferenceData(result.representantesResult(), result.seccionesResult());
    }

    private void loadEstudiantes() {
        listViewModel.loadingProperty().set(true);
        listViewModel.statusTextProperty().set("Consultando estudiantes...");
        estudiantesTable.setDisable(true);
        loadEstudiantesCommand.execute();
    }

    private void loadReferenceDataAsync() {
        FxExecutors.submitIo(
                () -> new ReferenceDataLoad(
                        referenceDataService.listarRepresentantes(),
                        referenceDataService.listarSecciones()),
                this::applyReferenceDataLoad);
    }

    private void applyReferenceData(
            ApiResult<PageResponse<RepresentanteLegalListItemDto>> representantesResult,
            ApiResult<PageResponse<SeccionListItemDto>> seccionesResult) {
        if (representantesResult.isSuccess()) {
            representantes.setAll(representantesResult.data().orElseThrow().getItems());
            representantesById.clear();
            representantes.forEach(item -> representantesById.put(item.id(), item));
        }
        if (seccionesResult.isSuccess()) {
            secciones.setAll(seccionesResult.data().orElseThrow().getItems());
            seccionesById.clear();
            secciones.forEach(item -> seccionesById.put(item.id(), item));
        }
        estudiantesTable.refresh();
    }

    private void applyListResult(ApiResult<PageResponse<EstudianteListItemDto>> result) {
        listViewModel.loadingProperty().set(false);
        estudiantesTable.setDisable(false);
        if (!result.isSuccess()) {
            listViewModel.statusTextProperty().set(
                    result.error().map(error -> error.message()).orElse("No se pudo cargar estudiantes."));
            listViewModel.items().clear();
            listViewModel.totalTextProperty().set("0 registros");
            listViewModel.pageTextProperty().set("P\u00e1gina 1 de 1");
            listViewModel.previousPageAvailableProperty().set(false);
            listViewModel.nextPageAvailableProperty().set(false);
            return;
        }

        PageResponse<EstudianteListItemDto> page = result.data().orElseThrow();
        listViewModel.items().setAll(page.getItems());
        listViewModel.pageProperty().set(page.getPage());
        listViewModel.totalPagesProperty().set(Math.max(page.getTotalPages(), 1));
        listViewModel.totalTextProperty().set(page.getTotalElements() + " estudiantes registrados");
        listViewModel.pageTextProperty().set("P\u00e1gina " + (page.getPage() + 1) + " de " + Math.max(page.getTotalPages(), 1));
        listViewModel.previousPageAvailableProperty().set(!page.isFirst());
        listViewModel.nextPageAvailableProperty().set(!page.isLast());
        listViewModel.statusTextProperty().set(
                "Mostrando " + page.getNumberOfElements() + " de " + page.getTotalElements() + " estudiantes.");
    }

    private void openDetail(EstudianteListItemDto estudiante) {
        if (estudiante == null) {
            return;
        }
        loadEstudiante(estudiante.id(), this::showDetailDrawer);
    }

    private void loadEstudiante(long estudianteId, Consumer<EstudianteResponseDto> onSuccess) {
        detailViewModel.loadingProperty().set(true);
        detailViewModel.bannerMessageProperty().set("");
        UiCommands.io(() -> estudiantesService.obtenerPorId(estudianteId), result -> {
            detailViewModel.loadingProperty().set(false);
            if (!result.isSuccess()) {
                String message = result.error().map(error -> error.message()).orElse("No se pudo cargar el estudiante.");
                detailViewModel.bannerMessageProperty().set(message);
                detailViewModel.visibleProperty().set(true);
                showDetailPane();
                return;
            }
            onSuccess.accept(result.data().orElseThrow());
        }).execute();
    }

    private void showDetailDrawer(EstudianteResponseDto estudiante) {
        currentEstudiante = estudiante;
        detailViewModel.visibleProperty().set(true);
        detailViewModel.titleProperty().set("Detalle del estudiante");
        detailViewModel.bannerMessageProperty().set("");
        detailViewModel.nombresProperty().set(estudiante.nombres());
        detailViewModel.apellidosProperty().set(estudiante.apellidos());
        detailViewModel.fechaNacimientoProperty().set(
                estudiante.fechaNacimiento() == null ? "-" : estudiante.fechaNacimiento().toString());
        detailViewModel.estadoProperty().set(estudiante.estado());
        detailViewModel.representanteProperty().set(lookupRepresentante(estudiante.representanteLegalId()));
        detailViewModel.seccionProperty().set(lookupSeccion(estudiante.seccionId()));
        detailEstadoButton.setText("ACTIVO".equalsIgnoreCase(estudiante.estado()) ? "Inactivar" : "Activar");
        formViewModel.visibleProperty().set(false);
        showDetailPane();
    }

    private void openEditForm(EstudianteResponseDto estudiante) {
        currentEstudiante = estudiante;
        detailViewModel.visibleProperty().set(false);
        formViewModel.visibleProperty().set(true);
        formViewModel.modeProperty().set(FormMode.EDIT);
        formViewModel.titleProperty().set("Editar estudiante");
        formViewModel.bannerMessageProperty().set("");
        formNombresField.setText(estudiante.nombres());
        formApellidosField.setText(estudiante.apellidos());
        formFechaNacimientoPicker.setValue(estudiante.fechaNacimiento());
        representanteCombo.getSelectionModel().select(representantesById.get(estudiante.representanteLegalId()));
        seccionCombo.getSelectionModel().select(seccionesById.get(estudiante.seccionId()));
        formViewModel.sectionSelectedProperty().set(estudiante.seccionId() != null);
        showFormPane();
    }

    private ApiResult<EstudianteResponseDto> submitForm() {
        if (formViewModel.modeProperty().get() == FormMode.ASSIGN_SECTION && currentEstudiante != null) {
            return estudiantesService.asignarSeccionVigente(currentEstudiante.id(), seccionCombo.getValue().id());
        }
        if (formViewModel.modeProperty().get() == FormMode.EDIT && currentEstudiante != null) {
            return estudiantesService.actualizar(
                    currentEstudiante.id(),
                    new EstudianteUpdateRequestDto(
                            formNombresField.getText().trim(),
                            formApellidosField.getText().trim(),
                            formFechaNacimientoPicker.getValue(),
                            representanteCombo.getValue().id(),
                            seccionCombo.getValue() == null ? null : seccionCombo.getValue().id(),
                            currentEstudiante.estado()));
        }

        return estudiantesService.crear(new EstudianteCreateRequestDto(
                formNombresField.getText().trim(),
                formApellidosField.getText().trim(),
                formFechaNacimientoPicker.getValue(),
                representanteCombo.getValue().id(),
                seccionCombo.getValue() == null ? null : seccionCombo.getValue().id()));
    }

    private void applySaveResult(ApiResult<EstudianteResponseDto> result) {
        formViewModel.loadingProperty().set(false);
        if (!result.isSuccess()) {
            String message = result.error().map(error -> error.message()).orElse("No se pudo guardar el estudiante.");
            formViewModel.bannerMessageProperty().set(message);
            return;
        }

        EstudianteResponseDto saved = result.data().orElseThrow();
        currentEstudiante = saved;
        listViewModel.statusTextProperty().set(
                switch (formViewModel.modeProperty().get()) {
                    case EDIT -> "Estudiante actualizado correctamente.";
                    case ASSIGN_SECTION -> "Sección vigente actualizada correctamente.";
                    default -> "Estudiante creado correctamente.";
                });
        onCerrarDrawer();
        loadEstudiantes();
    }

    private void toggleEstado(EstudianteListItemDto estudiante) {
        if (!isAdmin || estudiante == null) {
            return;
        }
        loadEstudiante(estudiante.id(), loaded -> {
            currentEstudiante = loaded;
            onCambiarEstadoActual();
        });
    }

    private void applyEstadoResult(ApiResult<EstudianteResponseDto> result) {
        detailViewModel.loadingProperty().set(false);
        if (!result.isSuccess()) {
            String message = result.error().map(error -> error.message()).orElse("No se pudo cambiar el estado.");
            detailViewModel.bannerMessageProperty().set(message);
            return;
        }

        EstudianteResponseDto updated = result.data().orElseThrow();
        showDetailDrawer(updated);
        listViewModel.statusTextProperty().set("Estado de estudiante actualizado correctamente.");
        loadEstudiantes();
    }

    private Optional<String> validateForm() {
        if (formViewModel.modeProperty().get() == FormMode.ASSIGN_SECTION) {
            if (seccionCombo.getValue() == null) {
                return Optional.of("Debes seleccionar una sección.");
            }
            return Optional.empty();
        }
        if (formNombresField.getText() == null || formNombresField.getText().isBlank()) {
            return Optional.of("Los nombres son obligatorios.");
        }
        if (formApellidosField.getText() == null || formApellidosField.getText().isBlank()) {
            return Optional.of("Los apellidos son obligatorios.");
        }
        if (formFechaNacimientoPicker.getValue() == null) {
            return Optional.of("La fecha de nacimiento es obligatoria.");
        }
        if (!formFechaNacimientoPicker.getValue().isBefore(LocalDate.now())) {
            return Optional.of("La fecha de nacimiento debe estar en el pasado.");
        }
        if (representanteCombo.getValue() == null) {
            return Optional.of("Debes seleccionar un representante.");
        }
        return Optional.empty();
    }

    private EstudiantesListQuery buildListQuery() {
        return new EstudiantesListQuery(
                listViewModel.pageProperty().get(),
                PAGE_SIZE,
                listViewModel.queryTextProperty().get(),
                normalizeEstadoFilter(),
                selectedSeccionId(),
                selectedRepresentanteId());
    }

    private String lookupRepresentante(Long id) {
        if (id == null) {
            return "Sin representante";
        }
        RepresentanteLegalListItemDto value = representantesById.get(id);
        return value == null ? "Representante #" + id : value.displayName();
    }

    private String lookupSeccion(Long id) {
        if (id == null) {
            return "Sin sección vigente";
        }
        SeccionListItemDto value = seccionesById.get(id);
        return value == null ? "Sección #" + id : value.displayName();
    }

    private String normalizeEstadoFilter() {
        String value = estadoFilter.getSelectionModel().getSelectedItem();
        if (value == null || "TODOS".equals(value)) {
            return null;
        }
        return value;
    }

    private void showDetailPane() {
        drawerCoordinator.showOnly(detailContent);
        detailEditarButton.setVisible(true);
        detailEstadoButton.setVisible(isAdmin);
        detailAsignarSeccionButton.setVisible(true);
        guardarButton.setVisible(false);
    }

    private void showFormPane() {
        drawerCoordinator.showOnly(formContent);
        detailEditarButton.setVisible(false);
        detailEstadoButton.setVisible(false);
        detailAsignarSeccionButton.setVisible(false);
        guardarButton.setVisible(true);
        boolean assignMode = formViewModel.modeProperty().get() == FormMode.ASSIGN_SECTION;
        formNombresBox.setVisible(!assignMode);
        formNombresBox.setManaged(!assignMode);
        formApellidosBox.setVisible(!assignMode);
        formApellidosBox.setManaged(!assignMode);
        formFechaNacimientoBox.setVisible(!assignMode);
        formFechaNacimientoBox.setManaged(!assignMode);
        formRepresentanteBox.setVisible(!assignMode);
        formRepresentanteBox.setManaged(!assignMode);
        formSeccionBox.setVisible(true);
        formSeccionBox.setManaged(true);
    }

    @FXML
    private void onAsignarSeccionVigente() {
        if (currentEstudiante == null) {
            return;
        }
        detailViewModel.visibleProperty().set(false);
        formViewModel.visibleProperty().set(true);
        formViewModel.modeProperty().set(FormMode.ASSIGN_SECTION);
        formViewModel.titleProperty().set("Asignar sección vigente");
        formViewModel.bannerMessageProperty().set("");
        formViewModel.sectionSelectedProperty().set(currentEstudiante.seccionId() != null);
        seccionCombo.getSelectionModel().select(seccionesById.get(currentEstudiante.seccionId()));
        formViewModel.selectedSeccionTextProperty().set(lookupSeccion(currentEstudiante.seccionId()));
        showFormPane();
    }

    private Long selectedRepresentanteId() {
        RepresentanteLegalListItemDto value = representanteFilterCombo.getValue();
        return value == null ? null : value.id();
    }

    private Long selectedSeccionId() {
        SeccionListItemDto value = seccionFilterCombo.getValue();
        return value == null ? null : value.id();
    }

    private void initializeCommands() {
        loadReferenceDataCommand = UiCommands.action(this::loadReferenceDataAsync);
        loadEstudiantesCommand = UiCommands.io(() -> estudiantesService.listar(buildListQuery()), this::applyListResult);
        submitFormCommand = UiCommands.io(this::submitForm, this::applySaveResult);
    }

    private Window currentWindow() {
        return drawerPane.getScene() == null ? null : drawerPane.getScene().getWindow();
    }
}
