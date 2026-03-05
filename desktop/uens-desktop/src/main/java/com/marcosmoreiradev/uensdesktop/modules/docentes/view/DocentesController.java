package com.marcosmoreiradev.uensdesktop.modules.docentes.view;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.docentes.dto.DocenteCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.docentes.dto.DocenteListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.docentes.dto.DocenteResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.docentes.dto.DocenteUpdateRequestDto;
import com.marcosmoreiradev.uensdesktop.app.AppContext;
import com.marcosmoreiradev.uensdesktop.app.ContextAwareController;
import com.marcosmoreiradev.uensdesktop.modules.docentes.application.DocentesService;
import com.marcosmoreiradev.uensdesktop.modules.docentes.viewmodel.DocenteDetailViewModel;
import com.marcosmoreiradev.uensdesktop.modules.docentes.viewmodel.DocenteFormViewModel;
import com.marcosmoreiradev.uensdesktop.modules.docentes.viewmodel.DocentesListViewModel;
import com.marcosmoreiradev.uensdesktop.session.Role;
import com.marcosmoreiradev.uensdesktop.ui.fx.FxExecutors;
import com.marcosmoreiradev.uensdesktop.ui.fx.UiStyling;
import com.marcosmoreiradev.uensdesktop.ui.tooltip.TooltipSupport;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
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

public final class DocentesController implements ContextAwareController {

    private static final int PAGE_SIZE = 20;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^$|^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    @FXML private TextField queryField;
    @FXML private ComboBox<String> estadoFilter;
    @FXML private Button buscarButton;
    @FXML private Button limpiarButton;
    @FXML private Button crearDocenteButton;
    @FXML private Button anteriorButton;
    @FXML private Button siguienteButton;
    @FXML private Label totalLabel;
    @FXML private Label pageLabel;
    @FXML private Label statusLabel;
    @FXML private TableView<DocenteListItemDto> docentesTable;
    @FXML private TableColumn<DocenteListItemDto, Long> idColumn;
    @FXML private TableColumn<DocenteListItemDto, String> nombresColumn;
    @FXML private TableColumn<DocenteListItemDto, String> apellidosColumn;
    @FXML private TableColumn<DocenteListItemDto, String> telefonoColumn;
    @FXML private TableColumn<DocenteListItemDto, String> correoColumn;
    @FXML private TableColumn<DocenteListItemDto, String> estadoColumn;
    @FXML private TableColumn<DocenteListItemDto, Void> accionesColumn;
    @FXML private VBox drawerPane;
    @FXML private Label drawerTitleLabel;
    @FXML private Label drawerBannerLabel;
    @FXML private VBox detailContent;
    @FXML private VBox formContent;
    @FXML private Label detailNombresLabel;
    @FXML private Label detailApellidosLabel;
    @FXML private Label detailTelefonoLabel;
    @FXML private Label detailCorreoLabel;
    @FXML private Label detailEstadoLabel;
    @FXML private TextField formNombresField;
    @FXML private TextField formApellidosField;
    @FXML private TextField formTelefonoField;
    @FXML private TextField formCorreoField;
    @FXML private Button detailEditarButton;
    @FXML private Button detailEstadoButton;
    @FXML private Button guardarButton;

    private final DocentesListViewModel listViewModel = new DocentesListViewModel();
    private final DocenteDetailViewModel detailViewModel = new DocenteDetailViewModel();
    private final DocenteFormViewModel formViewModel = new DocenteFormViewModel();

    private AppContext appContext;
    private DocentesService docentesService;
    private DocenteResponseDto currentDocente;
    private boolean isAdmin;
    private boolean initialized;
    private boolean contextApplied;

    @FXML
    private void initialize() {
        estadoFilter.setItems(FXCollections.observableArrayList("TODOS", "ACTIVO", "INACTIVO"));
        estadoFilter.getSelectionModel().selectFirst();
        queryField.textProperty().bindBidirectional(listViewModel.queryTextProperty());
        configureTable();
        configureDrawer();
        bindViewModels();
        formNombresField.textProperty().bindBidirectional(formViewModel.nombresProperty());
        formApellidosField.textProperty().bindBidirectional(formViewModel.apellidosProperty());
        formTelefonoField.textProperty().bindBidirectional(formViewModel.telefonoProperty());
        formCorreoField.textProperty().bindBidirectional(formViewModel.correoProperty());
        initialized = true;
        applyContextIfReady();
    }

    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
        this.docentesService = appContext.services().docentesService();
        this.isAdmin = appContext.sessionState().role().filter(role -> role == Role.ADMIN).isPresent();
        applyContextIfReady();
    }

    @FXML
    private void onBuscar() {
        listViewModel.pageProperty().set(0);
        loadDocentes();
    }

    @FXML
    private void onLimpiar() {
        listViewModel.queryTextProperty().set("");
        estadoFilter.getSelectionModel().selectFirst();
        listViewModel.pageProperty().set(0);
        loadDocentes();
    }

    @FXML
    private void onPaginaAnterior() {
        if (listViewModel.pageProperty().get() <= 0) {
            return;
        }
        listViewModel.pageProperty().set(listViewModel.pageProperty().get() - 1);
        loadDocentes();
    }

    @FXML
    private void onPaginaSiguiente() {
        if (!listViewModel.nextPageAvailableProperty().get()) {
            return;
        }
        listViewModel.pageProperty().set(listViewModel.pageProperty().get() + 1);
        loadDocentes();
    }

    @FXML
    private void onCrearDocente() {
        currentDocente = null;
        detailViewModel.visibleProperty().set(false);
        formViewModel.visibleProperty().set(true);
        formViewModel.titleProperty().set("Crear docente");
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
        if (currentDocente != null) {
            openEditForm(currentDocente);
        }
    }

    @FXML
    private void onCambiarEstadoActual() {
        if (!isAdmin || currentDocente == null) {
            return;
        }
        String nextEstado = "ACTIVO".equalsIgnoreCase(currentDocente.estado()) ? "INACTIVO" : "ACTIVO";
        boolean confirmed = appContext.feedback().confirm(
                currentWindow(),
                "Confirmar cambio de estado",
                "Cambiar estado del docente",
                "Se cambiar\u00e1 el estado a " + nextEstado + ".",
                "Confirmar",
                "Cancelar");
        if (!confirmed) {
            return;
        }
        detailViewModel.loadingProperty().set(true);
        detailViewModel.bannerMessageProperty().set("");
        FxExecutors.submitIo(() -> docentesService.cambiarEstado(currentDocente.id(), nextEstado), this::applyEstadoResult);
    }

    @FXML
    private void onGuardarFormulario() {
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
        refreshRoleControls();
        loadDocentes();
    }

    private void refreshRoleControls() {
        detailEstadoButton.setVisible(isAdmin);
    }

    private void configureTable() {
        UiStyling.configureTableToFillWidth(docentesTable);
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().id()));
        nombresColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().nombres()));
        apellidosColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().apellidos()));
        telefonoColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(safeText(cellData.getValue().telefono())));
        correoColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(safeText(cellData.getValue().correoElectronico())));
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
                verButton.setOnAction(event -> openFromRow(DocentesController.this::showDetailDrawer));
                editarButton.setOnAction(event -> openFromRow(DocentesController.this::openEditForm));
                estadoButton.setOnAction(event -> openFromRow(docente -> {
                    currentDocente = docente;
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
                estadoButton.setVisible(isAdmin);
                estadoButton.setManaged(isAdmin);
                estadoButton.setText("ACTIVO".equalsIgnoreCase(getCurrentItem().estado()) ? "Inactivar" : "Activar");
                setGraphic(container);
            }

            private void openFromRow(Consumer<DocenteResponseDto> consumer) {
                DocenteListItemDto currentItem = getCurrentItem();
                if (currentItem != null) {
                    loadDocente(currentItem.id(), consumer);
                }
            }

            private DocenteListItemDto getCurrentItem() {
                if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    return null;
                }
                return getTableView().getItems().get(getIndex());
            }
        });
        docentesTable.setItems(listViewModel.items());
        docentesTable.setPlaceholder(new Label("Sin resultados para los filtros actuales."));
        docentesTable.setRowFactory(table -> {
            TableRow<DocenteListItemDto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    loadDocente(row.getItem().id(), this::showDetailDrawer);
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
        detailEstadoButton.setVisible(false);
        guardarButton.setVisible(false);
    }

    private void bindViewModels() {
        statusLabel.textProperty().bind(listViewModel.statusTextProperty());
        totalLabel.textProperty().bind(listViewModel.totalTextProperty());
        pageLabel.textProperty().bind(listViewModel.pageTextProperty());
        buscarButton.disableProperty().bind(listViewModel.loadingProperty());
        limpiarButton.disableProperty().bind(listViewModel.loadingProperty());
        crearDocenteButton.disableProperty().bind(listViewModel.loadingProperty());
        anteriorButton.disableProperty().bind(listViewModel.loadingProperty().or(listViewModel.previousPageAvailableProperty().not()));
        siguienteButton.disableProperty().bind(listViewModel.loadingProperty().or(listViewModel.nextPageAvailableProperty().not()));
        drawerTitleLabel.textProperty().bind(Bindings.when(detailViewModel.visibleProperty()).then(detailViewModel.titleProperty()).otherwise(formViewModel.titleProperty()));
        drawerBannerLabel.textProperty().bind(Bindings.when(detailViewModel.visibleProperty()).then(detailViewModel.bannerMessageProperty()).otherwise(formViewModel.bannerMessageProperty()));
        UiStyling.bindVisibleWhenTextPresent(drawerBannerLabel);
        UiStyling.installAutoHideBanner(detailViewModel.bannerMessageProperty());
        UiStyling.installAutoHideBanner(formViewModel.bannerMessageProperty());
        detailNombresLabel.textProperty().bind(detailViewModel.nombresProperty());
        detailApellidosLabel.textProperty().bind(detailViewModel.apellidosProperty());
        detailTelefonoLabel.textProperty().bind(detailViewModel.telefonoProperty());
        detailCorreoLabel.textProperty().bind(detailViewModel.correoProperty());
        detailEstadoLabel.textProperty().bind(detailViewModel.estadoProperty());
        guardarButton.disableProperty().bind(formViewModel.canSubmitBinding().not());
        detailEditarButton.managedProperty().bind(detailEditarButton.visibleProperty());
        detailEstadoButton.managedProperty().bind(detailEstadoButton.visibleProperty());
        guardarButton.managedProperty().bind(guardarButton.visibleProperty());
    }

    private void loadDocentes() {
        listViewModel.loadingProperty().set(true);
        listViewModel.statusTextProperty().set("Consultando docentes...");
        docentesTable.setDisable(true);
        FxExecutors.submitIo(
                () -> docentesService.listar(
                        listViewModel.pageProperty().get(),
                        PAGE_SIZE,
                        listViewModel.queryTextProperty().get(),
                        normalizeEstadoFilter()),
                this::applyListResult);
    }

    private void applyListResult(ApiResult<PageResponse<DocenteListItemDto>> result) {
        listViewModel.loadingProperty().set(false);
        docentesTable.setDisable(false);
        if (!result.isSuccess()) {
            listViewModel.statusTextProperty().set(result.error().map(error -> error.message()).orElse("No se pudo cargar docentes."));
            listViewModel.items().clear();
            listViewModel.totalTextProperty().set("0 registros");
            listViewModel.pageTextProperty().set("P\u00e1gina 1 de 1");
            listViewModel.previousPageAvailableProperty().set(false);
            listViewModel.nextPageAvailableProperty().set(false);
            return;
        }
        PageResponse<DocenteListItemDto> page = result.data().orElseThrow();
        listViewModel.items().setAll(page.getItems());
        listViewModel.pageProperty().set(page.getPage());
        listViewModel.totalTextProperty().set(page.getTotalElements() + " docentes registrados");
        listViewModel.pageTextProperty().set("P\u00e1gina " + (page.getPage() + 1) + " de " + Math.max(page.getTotalPages(), 1));
        listViewModel.previousPageAvailableProperty().set(!page.isFirst());
        listViewModel.nextPageAvailableProperty().set(!page.isLast());
        listViewModel.statusTextProperty().set("Mostrando " + page.getNumberOfElements() + " de " + page.getTotalElements() + " docentes.");
    }

    private void loadDocente(long docenteId, Consumer<DocenteResponseDto> onSuccess) {
        detailViewModel.loadingProperty().set(true);
        detailViewModel.bannerMessageProperty().set("");
        FxExecutors.submitIo(() -> docentesService.obtenerPorId(docenteId), result -> {
            detailViewModel.loadingProperty().set(false);
            if (!result.isSuccess()) {
                detailViewModel.bannerMessageProperty().set(result.error().map(error -> error.message()).orElse("No se pudo cargar el docente."));
                detailViewModel.visibleProperty().set(true);
                showDetailPane();
                return;
            }
            onSuccess.accept(result.data().orElseThrow());
        });
    }

    private void showDetailDrawer(DocenteResponseDto docente) {
        currentDocente = docente;
        detailViewModel.visibleProperty().set(true);
        detailViewModel.titleProperty().set("Detalle del docente");
        detailViewModel.bannerMessageProperty().set("");
        detailViewModel.nombresProperty().set(docente.nombres());
        detailViewModel.apellidosProperty().set(docente.apellidos());
        detailViewModel.telefonoProperty().set(safeText(docente.telefono()));
        detailViewModel.correoProperty().set(safeText(docente.correoElectronico()));
        detailViewModel.estadoProperty().set(docente.estado());
        detailEstadoButton.setText("ACTIVO".equalsIgnoreCase(docente.estado()) ? "Inactivar" : "Activar");
        formViewModel.visibleProperty().set(false);
        showDetailPane();
    }

    private void openEditForm(DocenteResponseDto docente) {
        currentDocente = docente;
        detailViewModel.visibleProperty().set(false);
        formViewModel.visibleProperty().set(true);
        formViewModel.titleProperty().set("Editar docente");
        formViewModel.bannerMessageProperty().set("");
        formNombresField.setText(docente.nombres());
        formApellidosField.setText(docente.apellidos());
        formTelefonoField.setText(safeText(docente.telefono()));
        formCorreoField.setText(safeText(docente.correoElectronico()));
        showFormPane();
    }

    private ApiResult<DocenteResponseDto> submitForm() {
        if (currentDocente != null) {
            return docentesService.actualizar(currentDocente.id(), new DocenteUpdateRequestDto(
                    formNombresField.getText().trim(),
                    formApellidosField.getText().trim(),
                    normalizeOptional(formTelefonoField.getText()),
                    normalizeOptional(formCorreoField.getText()),
                    currentDocente.estado()));
        }
        return docentesService.crear(new DocenteCreateRequestDto(
                formNombresField.getText().trim(),
                formApellidosField.getText().trim(),
                normalizeOptional(formTelefonoField.getText()),
                normalizeOptional(formCorreoField.getText())));
    }

    private void applySaveResult(ApiResult<DocenteResponseDto> result) {
        formViewModel.loadingProperty().set(false);
        if (!result.isSuccess()) {
            formViewModel.bannerMessageProperty().set(result.error().map(error -> error.message()).orElse("No se pudo guardar el docente."));
            return;
        }
        listViewModel.statusTextProperty().set(currentDocente == null ? "Docente creado correctamente." : "Docente actualizado correctamente.");
        currentDocente = result.data().orElseThrow();
        onCerrarDrawer();
        loadDocentes();
    }

    private void applyEstadoResult(ApiResult<DocenteResponseDto> result) {
        detailViewModel.loadingProperty().set(false);
        if (!result.isSuccess()) {
            detailViewModel.bannerMessageProperty().set(result.error().map(error -> error.message()).orElse("No se pudo cambiar el estado."));
            return;
        }
        showDetailDrawer(result.data().orElseThrow());
        listViewModel.statusTextProperty().set("Estado de docente actualizado correctamente.");
        loadDocentes();
    }

    private Optional<String> validateForm() {
        if (formNombresField.getText() == null || formNombresField.getText().isBlank()) {
            return Optional.of("Los nombres son obligatorios.");
        }
        if (formApellidosField.getText() == null || formApellidosField.getText().isBlank()) {
            return Optional.of("Los apellidos son obligatorios.");
        }
        String correo = normalizeOptional(formCorreoField.getText());
        if (correo != null && !EMAIL_PATTERN.matcher(correo).matches()) {
            return Optional.of("El correo no tiene formato valido.");
        }
        return Optional.empty();
    }

    private void clearForm() {
        formNombresField.clear();
        formApellidosField.clear();
        formTelefonoField.clear();
        formCorreoField.clear();
    }

    private void showDetailPane() {
        drawerPane.setVisible(true);
        drawerPane.setManaged(true);
        detailContent.setVisible(true);
        detailContent.setManaged(true);
        formContent.setVisible(false);
        formContent.setManaged(false);
        detailEditarButton.setVisible(true);
        detailEstadoButton.setVisible(isAdmin);
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
        detailEstadoButton.setVisible(false);
        guardarButton.setVisible(true);
    }

    private String normalizeEstadoFilter() {
        String value = estadoFilter.getSelectionModel().getSelectedItem();
        if (value == null || "TODOS".equals(value)) {
            return null;
        }
        return value;
    }

    private Window currentWindow() {
        return drawerPane.getScene() == null ? null : drawerPane.getScene().getWindow();
    }

    private static String safeText(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private static String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
