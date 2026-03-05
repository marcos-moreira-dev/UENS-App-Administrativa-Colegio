package com.marcosmoreiradev.uensdesktop.modules.asignaturas.view;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.dto.AsignaturaCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.dto.AsignaturaListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.dto.AsignaturaResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.dto.AsignaturaUpdateRequestDto;
import com.marcosmoreiradev.uensdesktop.app.AppContext;
import com.marcosmoreiradev.uensdesktop.app.ContextAwareController;
import com.marcosmoreiradev.uensdesktop.modules.asignaturas.application.AsignaturasListQuery;
import com.marcosmoreiradev.uensdesktop.modules.asignaturas.application.AsignaturasService;
import com.marcosmoreiradev.uensdesktop.modules.asignaturas.viewmodel.AsignaturaDetailViewModel;
import com.marcosmoreiradev.uensdesktop.modules.asignaturas.viewmodel.AsignaturaFormViewModel;
import com.marcosmoreiradev.uensdesktop.modules.asignaturas.viewmodel.AsignaturasListViewModel;
import com.marcosmoreiradev.uensdesktop.session.Role;
import com.marcosmoreiradev.uensdesktop.ui.fx.FxExecutors;
import com.marcosmoreiradev.uensdesktop.ui.fx.UiStyling;
import com.marcosmoreiradev.uensdesktop.ui.tooltip.TooltipSupport;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

public final class AsignaturasController implements ContextAwareController {

    private static final int PAGE_SIZE = 20;

    @FXML private TextField queryField;
    @FXML private ComboBox<String> estadoFilter;
    @FXML private ComboBox<Integer> gradoFilter;
    @FXML private TextField areaFilterField;
    @FXML private Button buscarButton;
    @FXML private Button limpiarButton;
    @FXML private Button crearAsignaturaButton;
    @FXML private Button anteriorButton;
    @FXML private Button siguienteButton;
    @FXML private Label totalLabel;
    @FXML private Label pageLabel;
    @FXML private Label statusLabel;
    @FXML private TableView<AsignaturaListItemDto> asignaturasTable;
    @FXML private TableColumn<AsignaturaListItemDto, Long> idColumn;
    @FXML private TableColumn<AsignaturaListItemDto, String> nombreColumn;
    @FXML private TableColumn<AsignaturaListItemDto, String> areaColumn;
    @FXML private TableColumn<AsignaturaListItemDto, Integer> gradoColumn;
    @FXML private TableColumn<AsignaturaListItemDto, String> estadoColumn;
    @FXML private TableColumn<AsignaturaListItemDto, Void> accionesColumn;
    @FXML private VBox drawerPane;
    @FXML private Label drawerTitleLabel;
    @FXML private Label drawerBannerLabel;
    @FXML private VBox detailContent;
    @FXML private VBox formContent;
    @FXML private Label detailNombreLabel;
    @FXML private Label detailAreaLabel;
    @FXML private Label detailDescripcionLabel;
    @FXML private Label detailGradoLabel;
    @FXML private Label detailEstadoLabel;
    @FXML private TextField formNombreField;
    @FXML private TextField formAreaField;
    @FXML private ComboBox<Integer> formGradoCombo;
    @FXML private TextArea formDescripcionArea;
    @FXML private Button detailEditarButton;
    @FXML private Button detailEstadoButton;
    @FXML private Button guardarButton;

    private final AsignaturasListViewModel listViewModel = new AsignaturasListViewModel();
    private final AsignaturaDetailViewModel detailViewModel = new AsignaturaDetailViewModel();
    private final AsignaturaFormViewModel formViewModel = new AsignaturaFormViewModel();

    private AppContext appContext;
    private AsignaturasService asignaturasService;
    private AsignaturaResponseDto currentAsignatura;
    private boolean isAdmin;
    private boolean initialized;
    private boolean contextApplied;

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
        this.asignaturasService = appContext.services().asignaturasService();
        this.isAdmin = appContext.sessionState().role().filter(role -> role == Role.ADMIN).isPresent();
        applyContextIfReady();
    }

    @FXML
    private void onBuscar() {
        listViewModel.pageProperty().set(0);
        loadAsignaturas();
    }

    @FXML
    private void onLimpiar() {
        listViewModel.queryTextProperty().set("");
        listViewModel.areaFilterProperty().set("");
        estadoFilter.getSelectionModel().selectFirst();
        gradoFilter.getSelectionModel().selectFirst();
        listViewModel.pageProperty().set(0);
        loadAsignaturas();
    }

    @FXML
    private void onPaginaAnterior() {
        if (listViewModel.pageProperty().get() <= 0) {
            return;
        }
        listViewModel.pageProperty().set(listViewModel.pageProperty().get() - 1);
        loadAsignaturas();
    }

    @FXML
    private void onPaginaSiguiente() {
        if (!listViewModel.nextPageAvailableProperty().get()) {
            return;
        }
        listViewModel.pageProperty().set(listViewModel.pageProperty().get() + 1);
        loadAsignaturas();
    }

    @FXML
    private void onCrearAsignatura() {
        currentAsignatura = null;
        detailViewModel.visibleProperty().set(false);
        formViewModel.visibleProperty().set(true);
        formViewModel.titleProperty().set("Crear asignatura");
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
        if (currentAsignatura != null) {
            openEditForm(currentAsignatura);
        }
    }

    @FXML
    private void onCambiarEstadoActual() {
        if (!isAdmin || currentAsignatura == null) {
            return;
        }
        String nextEstado = "ACTIVO".equalsIgnoreCase(currentAsignatura.estado()) ? "INACTIVO" : "ACTIVO";
        boolean confirmed = appContext.feedback().confirm(
                currentWindow(),
                "Confirmar cambio de estado",
                "Cambiar estado de la asignatura",
                "Se cambiar\u00e1 el estado a " + nextEstado + ".",
                "Confirmar",
                "Cancelar");
        if (!confirmed) {
            return;
        }
        detailViewModel.loadingProperty().set(true);
        detailViewModel.bannerMessageProperty().set("");
        FxExecutors.submitIo(
                () -> asignaturasService.cambiarEstado(currentAsignatura.id(), nextEstado),
                this::applyEstadoResult);
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
        loadAsignaturas();
    }

    private void configureFilters() {
        estadoFilter.setItems(FXCollections.observableArrayList("TODOS", "ACTIVO", "INACTIVO"));
        estadoFilter.getSelectionModel().selectFirst();
        gradoFilter.setItems(FXCollections.observableArrayList(null, 1, 2, 3, 4, 5, 6, 7));
        gradoFilter.getSelectionModel().selectFirst();
        gradoFilter.setButtonCell(new NullFriendlyIntegerCell("Todos los grados"));
        gradoFilter.setCellFactory(listView -> new NullFriendlyIntegerCell("Todos los grados"));
        formGradoCombo.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7));
        queryField.textProperty().bindBidirectional(listViewModel.queryTextProperty());
        areaFilterField.textProperty().bindBidirectional(listViewModel.areaFilterProperty());
        formNombreField.textProperty().bindBidirectional(formViewModel.nombreProperty());
        formAreaField.textProperty().bindBidirectional(formViewModel.areaProperty());
        formDescripcionArea.textProperty().bindBidirectional(formViewModel.descripcionProperty());
        formGradoCombo.valueProperty().bindBidirectional(formViewModel.gradoProperty());
    }

    private void refreshRoleControls() {
        crearAsignaturaButton.setVisible(isAdmin);
        crearAsignaturaButton.setManaged(isAdmin);
        detailEditarButton.setVisible(isAdmin);
        detailEstadoButton.setVisible(isAdmin);
    }

    private void configureTable() {
        UiStyling.configureTableToFillWidth(asignaturasTable);
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().id()));
        nombreColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().nombre()));
        areaColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().area()));
        gradoColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().grado()));
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
                verButton.setOnAction(event -> openFromRow(AsignaturasController.this::showDetailDrawer));
                editarButton.setOnAction(event -> openFromRow(AsignaturasController.this::openEditForm));
                estadoButton.setOnAction(event -> openFromRow(asignatura -> {
                    currentAsignatura = asignatura;
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

            private void openFromRow(Consumer<AsignaturaResponseDto> consumer) {
                AsignaturaListItemDto currentItem = getCurrentItem();
                if (currentItem != null) {
                    loadAsignatura(currentItem.id(), consumer);
                }
            }

            private AsignaturaListItemDto getCurrentItem() {
                if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    return null;
                }
                return getTableView().getItems().get(getIndex());
            }
        });
        asignaturasTable.setItems(listViewModel.items());
        asignaturasTable.setPlaceholder(new Label("Sin resultados para los filtros actuales."));
        asignaturasTable.setRowFactory(table -> {
            TableRow<AsignaturaListItemDto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    loadAsignatura(row.getItem().id(), this::showDetailDrawer);
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
        crearAsignaturaButton.disableProperty().bind(listViewModel.loadingProperty());
        anteriorButton.disableProperty().bind(listViewModel.loadingProperty().or(listViewModel.previousPageAvailableProperty().not()));
        siguienteButton.disableProperty().bind(listViewModel.loadingProperty().or(listViewModel.nextPageAvailableProperty().not()));
        drawerTitleLabel.textProperty().bind(Bindings.when(detailViewModel.visibleProperty()).then(detailViewModel.titleProperty()).otherwise(formViewModel.titleProperty()));
        drawerBannerLabel.textProperty().bind(Bindings.when(detailViewModel.visibleProperty()).then(detailViewModel.bannerMessageProperty()).otherwise(formViewModel.bannerMessageProperty()));
        UiStyling.bindVisibleWhenTextPresent(drawerBannerLabel);
        UiStyling.installAutoHideBanner(detailViewModel.bannerMessageProperty());
        UiStyling.installAutoHideBanner(formViewModel.bannerMessageProperty());
        detailNombreLabel.textProperty().bind(detailViewModel.nombreProperty());
        detailAreaLabel.textProperty().bind(detailViewModel.areaProperty());
        detailDescripcionLabel.textProperty().bind(detailViewModel.descripcionProperty());
        detailGradoLabel.textProperty().bind(detailViewModel.gradoProperty());
        detailEstadoLabel.textProperty().bind(detailViewModel.estadoProperty());
        guardarButton.disableProperty().bind(formViewModel.canSubmitBinding().not());
        detailEditarButton.managedProperty().bind(detailEditarButton.visibleProperty());
        detailEstadoButton.managedProperty().bind(detailEstadoButton.visibleProperty());
        guardarButton.managedProperty().bind(guardarButton.visibleProperty());
    }

    private void loadAsignaturas() {
        listViewModel.loadingProperty().set(true);
        listViewModel.statusTextProperty().set("Consultando asignaturas...");
        asignaturasTable.setDisable(true);
        FxExecutors.submitIo(() -> asignaturasService.listar(buildListQuery()), this::applyListResult);
    }

    private void applyListResult(ApiResult<PageResponse<AsignaturaListItemDto>> result) {
        listViewModel.loadingProperty().set(false);
        asignaturasTable.setDisable(false);
        if (!result.isSuccess()) {
            listViewModel.statusTextProperty().set(result.error().map(error -> error.message()).orElse("No se pudo cargar asignaturas."));
            listViewModel.items().clear();
            listViewModel.totalTextProperty().set("0 registros");
            listViewModel.pageTextProperty().set("P\u00e1gina 1 de 1");
            listViewModel.previousPageAvailableProperty().set(false);
            listViewModel.nextPageAvailableProperty().set(false);
            return;
        }
        PageResponse<AsignaturaListItemDto> page = result.data().orElseThrow();
        listViewModel.items().setAll(page.getItems());
        listViewModel.pageProperty().set(page.getPage());
        listViewModel.totalTextProperty().set(page.getTotalElements() + " asignaturas registradas");
        listViewModel.pageTextProperty().set("P\u00e1gina " + (page.getPage() + 1) + " de " + Math.max(page.getTotalPages(), 1));
        listViewModel.previousPageAvailableProperty().set(!page.isFirst());
        listViewModel.nextPageAvailableProperty().set(!page.isLast());
        listViewModel.statusTextProperty().set("Mostrando " + page.getNumberOfElements() + " de " + page.getTotalElements() + " asignaturas.");
    }

    private void loadAsignatura(long asignaturaId, Consumer<AsignaturaResponseDto> onSuccess) {
        detailViewModel.loadingProperty().set(true);
        detailViewModel.bannerMessageProperty().set("");
        FxExecutors.submitIo(() -> asignaturasService.obtenerPorId(asignaturaId), result -> {
            detailViewModel.loadingProperty().set(false);
            if (!result.isSuccess()) {
                detailViewModel.bannerMessageProperty().set(result.error().map(error -> error.message()).orElse("No se pudo cargar la asignatura."));
                detailViewModel.visibleProperty().set(true);
                showDetailPane();
                return;
            }
            onSuccess.accept(result.data().orElseThrow());
        });
    }

    private void showDetailDrawer(AsignaturaResponseDto asignatura) {
        currentAsignatura = asignatura;
        detailViewModel.visibleProperty().set(true);
        detailViewModel.titleProperty().set("Detalle de la asignatura");
        detailViewModel.bannerMessageProperty().set("");
        detailViewModel.nombreProperty().set(asignatura.nombre());
        detailViewModel.areaProperty().set(asignatura.area());
        detailViewModel.descripcionProperty().set(safeText(asignatura.descripcion()));
        detailViewModel.gradoProperty().set(String.valueOf(asignatura.grado()));
        detailViewModel.estadoProperty().set(asignatura.estado());
        detailEstadoButton.setText("ACTIVO".equalsIgnoreCase(asignatura.estado()) ? "Inactivar" : "Activar");
        formViewModel.visibleProperty().set(false);
        showDetailPane();
    }

    private void openEditForm(AsignaturaResponseDto asignatura) {
        currentAsignatura = asignatura;
        detailViewModel.visibleProperty().set(false);
        formViewModel.visibleProperty().set(true);
        formViewModel.titleProperty().set("Editar asignatura");
        formViewModel.bannerMessageProperty().set("");
        formNombreField.setText(asignatura.nombre());
        formAreaField.setText(asignatura.area());
        formDescripcionArea.setText(safeText(asignatura.descripcion()));
        formGradoCombo.setValue(asignatura.grado());
        showFormPane();
    }

    private void showDetailPane() {
        drawerPane.setVisible(true);
        drawerPane.setManaged(true);
        detailContent.setVisible(true);
        detailContent.setManaged(true);
        formContent.setVisible(false);
        formContent.setManaged(false);
        detailEditarButton.setVisible(isAdmin);
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

    private ApiResult<AsignaturaResponseDto> submitForm() {
        String nombre = formViewModel.nombreProperty().get().trim();
        String area = formViewModel.areaProperty().get().trim();
        String descripcion = emptyToNull(formViewModel.descripcionProperty().get());
        int grado = formViewModel.gradoProperty().get();
        if (currentAsignatura == null) {
            return asignaturasService.crear(new AsignaturaCreateRequestDto(nombre, area, descripcion, grado));
        }
        return asignaturasService.actualizar(
                currentAsignatura.id(),
                new AsignaturaUpdateRequestDto(nombre, area, descripcion, grado, currentAsignatura.estado()));
    }

    private void applySaveResult(ApiResult<AsignaturaResponseDto> result) {
        formViewModel.loadingProperty().set(false);
        if (!result.isSuccess()) {
            formViewModel.bannerMessageProperty().set(result.error().map(error -> error.message()).orElse("No se pudo guardar la asignatura."));
            return;
        }
        AsignaturaResponseDto saved = result.data().orElseThrow();
        currentAsignatura = saved;
        showDetailDrawer(saved);
        loadAsignaturas();
    }

    private void applyEstadoResult(ApiResult<AsignaturaResponseDto> result) {
        detailViewModel.loadingProperty().set(false);
        if (!result.isSuccess()) {
            detailViewModel.bannerMessageProperty().set(result.error().map(error -> error.message()).orElse("No se pudo cambiar el estado de la asignatura."));
            return;
        }
        AsignaturaResponseDto updated = result.data().orElseThrow();
        currentAsignatura = updated;
        showDetailDrawer(updated);
        loadAsignaturas();
    }

    private Optional<String> validateForm() {
        String nombre = formViewModel.nombreProperty().get().trim();
        if (nombre.isEmpty()) {
            return Optional.of("El nombre es obligatorio.");
        }
        if (nombre.length() > 120) {
            return Optional.of("El nombre no debe exceder 120 caracteres.");
        }
        String area = formViewModel.areaProperty().get().trim();
        if (area.isEmpty()) {
            return Optional.of("El area es obligatoria.");
        }
        if (area.length() > 60) {
            return Optional.of("El area no debe exceder 60 caracteres.");
        }
        String descripcion = formViewModel.descripcionProperty().get().trim();
        if (descripcion.length() > 500) {
            return Optional.of("La descripcion no debe exceder 500 caracteres.");
        }
        if (formViewModel.gradoProperty().get() == null) {
            return Optional.of("Selecciona un grado.");
        }
        return Optional.empty();
    }

    private void clearForm() {
        formNombreField.clear();
        formAreaField.clear();
        formDescripcionArea.clear();
        formGradoCombo.getSelectionModel().clearSelection();
    }

    private String normalizeEstadoFilter() {
        String value = estadoFilter.getValue();
        if (value == null || "TODOS".equalsIgnoreCase(value)) {
            return null;
        }
        return value;
    }

    private AsignaturasListQuery buildListQuery() {
        return new AsignaturasListQuery(
                listViewModel.pageProperty().get(),
                PAGE_SIZE,
                listViewModel.queryTextProperty().get(),
                normalizeEstadoFilter(),
                gradoFilter.getValue(),
                listViewModel.areaFilterProperty().get());
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

    private static final class NullFriendlyIntegerCell extends ListCell<Integer> {

        private final String nullText;

        private NullFriendlyIntegerCell(String nullText) {
            this.nullText = nullText;
        }

        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
            } else if (item == null) {
                setText(nullText);
            } else {
                setText(String.valueOf(item));
            }
        }
    }

    private Window currentWindow() {
        return drawerPane.getScene() == null ? null : drawerPane.getScene().getWindow();
    }
}
