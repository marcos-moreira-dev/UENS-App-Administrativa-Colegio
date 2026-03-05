package com.marcosmoreiradev.uensdesktop.modules.secciones.view;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto.SeccionCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto.SeccionListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto.SeccionResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto.SeccionUpdateRequestDto;
import com.marcosmoreiradev.uensdesktop.app.AppContext;
import com.marcosmoreiradev.uensdesktop.app.ContextAwareController;
import com.marcosmoreiradev.uensdesktop.modules.secciones.application.SeccionesListQuery;
import com.marcosmoreiradev.uensdesktop.modules.secciones.application.SeccionesService;
import com.marcosmoreiradev.uensdesktop.modules.secciones.viewmodel.SeccionDetailViewModel;
import com.marcosmoreiradev.uensdesktop.modules.secciones.viewmodel.SeccionFormViewModel;
import com.marcosmoreiradev.uensdesktop.modules.secciones.viewmodel.SeccionesListViewModel;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

public final class SeccionesController implements ContextAwareController {

    private static final int PAGE_SIZE = 20;
    private static final Pattern ANIO_LECTIVO_PATTERN = Pattern.compile("^[0-9]{4}-[0-9]{4}$");

    @FXML private TextField queryField;
    @FXML private ComboBox<String> estadoFilter;
    @FXML private ComboBox<Integer> gradoFilter;
    @FXML private TextField paraleloFilterField;
    @FXML private TextField anioLectivoFilterField;
    @FXML private Button buscarButton;
    @FXML private Button limpiarButton;
    @FXML private Button crearSeccionButton;
    @FXML private Button anteriorButton;
    @FXML private Button siguienteButton;
    @FXML private Label totalLabel;
    @FXML private Label pageLabel;
    @FXML private Label statusLabel;
    @FXML private TableView<SeccionListItemDto> seccionesTable;
    @FXML private TableColumn<SeccionListItemDto, Long> idColumn;
    @FXML private TableColumn<SeccionListItemDto, Integer> gradoColumn;
    @FXML private TableColumn<SeccionListItemDto, String> paraleloColumn;
    @FXML private TableColumn<SeccionListItemDto, Integer> cupoColumn;
    @FXML private TableColumn<SeccionListItemDto, String> anioLectivoColumn;
    @FXML private TableColumn<SeccionListItemDto, String> estadoColumn;
    @FXML private TableColumn<SeccionListItemDto, Void> accionesColumn;
    @FXML private VBox drawerPane;
    @FXML private Label drawerTitleLabel;
    @FXML private Label drawerBannerLabel;
    @FXML private VBox detailContent;
    @FXML private VBox formContent;
    @FXML private Label detailNombreLabel;
    @FXML private Label detailGradoLabel;
    @FXML private Label detailParaleloLabel;
    @FXML private Label detailCupoLabel;
    @FXML private Label detailAnioLectivoLabel;
    @FXML private Label detailEstadoLabel;
    @FXML private ComboBox<Integer> formGradoCombo;
    @FXML private TextField formParaleloField;
    @FXML private Spinner<Integer> formCupoMaximoSpinner;
    @FXML private TextField formAnioLectivoField;
    @FXML private Button detailEditarButton;
    @FXML private Button detailEstadoButton;
    @FXML private Button guardarButton;

    private final SeccionesListViewModel listViewModel = new SeccionesListViewModel();
    private final SeccionDetailViewModel detailViewModel = new SeccionDetailViewModel();
    private final SeccionFormViewModel formViewModel = new SeccionFormViewModel();

    private AppContext appContext;
    private SeccionesService seccionesService;
    private SeccionResponseDto currentSeccion;
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
        this.seccionesService = appContext.services().seccionesService();
        this.isAdmin = appContext.sessionState().role().filter(role -> role == Role.ADMIN).isPresent();
        applyContextIfReady();
    }

    @FXML
    private void onBuscar() {
        listViewModel.pageProperty().set(0);
        loadSecciones();
    }

    @FXML
    private void onLimpiar() {
        listViewModel.queryTextProperty().set("");
        listViewModel.paraleloFilterProperty().set("");
        listViewModel.anioLectivoFilterProperty().set("");
        estadoFilter.getSelectionModel().selectFirst();
        gradoFilter.getSelectionModel().selectFirst();
        listViewModel.pageProperty().set(0);
        loadSecciones();
    }

    @FXML
    private void onPaginaAnterior() {
        if (listViewModel.pageProperty().get() <= 0) {
            return;
        }
        listViewModel.pageProperty().set(listViewModel.pageProperty().get() - 1);
        loadSecciones();
    }

    @FXML
    private void onPaginaSiguiente() {
        if (!listViewModel.nextPageAvailableProperty().get()) {
            return;
        }
        listViewModel.pageProperty().set(listViewModel.pageProperty().get() + 1);
        loadSecciones();
    }

    @FXML
    private void onCrearSeccion() {
        currentSeccion = null;
        detailViewModel.visibleProperty().set(false);
        formViewModel.visibleProperty().set(true);
        formViewModel.titleProperty().set("Crear secci\u00f3n");
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
        if (currentSeccion != null) {
            openEditForm(currentSeccion);
        }
    }

    @FXML
    private void onCambiarEstadoActual() {
        if (!isAdmin || currentSeccion == null) {
            return;
        }
        String nextEstado = "ACTIVO".equalsIgnoreCase(currentSeccion.estado()) ? "INACTIVO" : "ACTIVO";
        boolean confirmed = appContext.feedback().confirm(
                currentWindow(),
                "Confirmar cambio de estado",
                "Cambiar estado de la secci\u00f3n",
                "Se cambiar\u00e1 el estado a " + nextEstado + ".",
                "Confirmar",
                "Cancelar");
        if (!confirmed) {
            return;
        }
        detailViewModel.loadingProperty().set(true);
        detailViewModel.bannerMessageProperty().set("");
        FxExecutors.submitIo(() -> seccionesService.cambiarEstado(currentSeccion.id(), nextEstado), this::applyEstadoResult);
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
        loadSecciones();
    }

    private void configureFilters() {
        estadoFilter.setItems(FXCollections.observableArrayList("TODOS", "ACTIVO", "INACTIVO"));
        estadoFilter.getSelectionModel().selectFirst();
        gradoFilter.setItems(FXCollections.observableArrayList(null, 1, 2, 3, 4, 5, 6, 7));
        gradoFilter.getSelectionModel().selectFirst();
        gradoFilter.setButtonCell(new NullFriendlyIntegerCell("Todos los grados"));
        gradoFilter.setCellFactory(listView -> new NullFriendlyIntegerCell("Todos los grados"));
        formGradoCombo.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7));
        SpinnerValueFactory.IntegerSpinnerValueFactory cupoFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 35, 35);
        formCupoMaximoSpinner.setValueFactory(cupoFactory);
        formCupoMaximoSpinner.setEditable(true);
        formCupoMaximoSpinner.setMaxWidth(Double.MAX_VALUE);
        queryField.textProperty().bindBidirectional(listViewModel.queryTextProperty());
        paraleloFilterField.textProperty().bindBidirectional(listViewModel.paraleloFilterProperty());
        anioLectivoFilterField.textProperty().bindBidirectional(listViewModel.anioLectivoFilterProperty());
        formGradoCombo.valueProperty().bindBidirectional(formViewModel.gradoProperty());
        formParaleloField.textProperty().bindBidirectional(formViewModel.paraleloProperty());
        cupoFactory.valueProperty().bindBidirectional(formViewModel.cupoMaximoProperty());
        formAnioLectivoField.textProperty().bindBidirectional(formViewModel.anioLectivoProperty());
    }

    private void refreshRoleControls() {
        crearSeccionButton.setVisible(isAdmin);
        crearSeccionButton.setManaged(isAdmin);
        detailEditarButton.setVisible(isAdmin);
        detailEstadoButton.setVisible(isAdmin);
    }

    private void configureTable() {
        UiStyling.configureTableToFillWidth(seccionesTable);
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().id()));
        gradoColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().grado()));
        paraleloColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().paralelo()));
        cupoColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().cupoMaximo()));
        anioLectivoColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().anioLectivo()));
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
                verButton.setOnAction(event -> openFromRow(SeccionesController.this::showDetailDrawer));
                editarButton.setOnAction(event -> openFromRow(SeccionesController.this::openEditForm));
                estadoButton.setOnAction(event -> openFromRow(seccion -> {
                    currentSeccion = seccion;
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

            private void openFromRow(Consumer<SeccionResponseDto> consumer) {
                SeccionListItemDto currentItem = getCurrentItem();
                if (currentItem != null) {
                    loadSeccion(currentItem.id(), consumer);
                }
            }

            private SeccionListItemDto getCurrentItem() {
                if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    return null;
                }
                return getTableView().getItems().get(getIndex());
            }
        });
        seccionesTable.setItems(listViewModel.items());
        seccionesTable.setPlaceholder(new Label("Sin resultados para los filtros actuales."));
        seccionesTable.setRowFactory(table -> {
            TableRow<SeccionListItemDto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    loadSeccion(row.getItem().id(), this::showDetailDrawer);
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
        crearSeccionButton.disableProperty().bind(listViewModel.loadingProperty());
        anteriorButton.disableProperty().bind(listViewModel.loadingProperty().or(listViewModel.previousPageAvailableProperty().not()));
        siguienteButton.disableProperty().bind(listViewModel.loadingProperty().or(listViewModel.nextPageAvailableProperty().not()));
        drawerTitleLabel.textProperty().bind(Bindings.when(detailViewModel.visibleProperty()).then(detailViewModel.titleProperty()).otherwise(formViewModel.titleProperty()));
        drawerBannerLabel.textProperty().bind(Bindings.when(detailViewModel.visibleProperty()).then(detailViewModel.bannerMessageProperty()).otherwise(formViewModel.bannerMessageProperty()));
        UiStyling.bindVisibleWhenTextPresent(drawerBannerLabel);
        UiStyling.installAutoHideBanner(detailViewModel.bannerMessageProperty());
        UiStyling.installAutoHideBanner(formViewModel.bannerMessageProperty());
        detailNombreLabel.textProperty().bind(detailViewModel.nombreProperty());
        detailGradoLabel.textProperty().bind(detailViewModel.gradoProperty());
        detailParaleloLabel.textProperty().bind(detailViewModel.paraleloProperty());
        detailCupoLabel.textProperty().bind(detailViewModel.cupoMaximoProperty());
        detailAnioLectivoLabel.textProperty().bind(detailViewModel.anioLectivoProperty());
        detailEstadoLabel.textProperty().bind(detailViewModel.estadoProperty());
        guardarButton.disableProperty().bind(formViewModel.canSubmitBinding().not());
        detailEditarButton.managedProperty().bind(detailEditarButton.visibleProperty());
        detailEstadoButton.managedProperty().bind(detailEstadoButton.visibleProperty());
        guardarButton.managedProperty().bind(guardarButton.visibleProperty());
    }

    private void loadSecciones() {
        listViewModel.loadingProperty().set(true);
        listViewModel.statusTextProperty().set("Consultando secciones...");
        seccionesTable.setDisable(true);
        FxExecutors.submitIo(() -> seccionesService.listar(buildListQuery()), this::applyListResult);
    }

    private void applyListResult(ApiResult<PageResponse<SeccionListItemDto>> result) {
        listViewModel.loadingProperty().set(false);
        seccionesTable.setDisable(false);
        if (!result.isSuccess()) {
            listViewModel.statusTextProperty().set(result.error().map(error -> error.message()).orElse("No se pudo cargar secciones."));
            listViewModel.items().clear();
            listViewModel.totalTextProperty().set("0 registros");
            listViewModel.pageTextProperty().set("P\u00e1gina 1 de 1");
            listViewModel.previousPageAvailableProperty().set(false);
            listViewModel.nextPageAvailableProperty().set(false);
            return;
        }
        PageResponse<SeccionListItemDto> page = result.data().orElseThrow();
        listViewModel.items().setAll(page.getItems());
        listViewModel.pageProperty().set(page.getPage());
        listViewModel.totalTextProperty().set(page.getTotalElements() + " secciones registradas");
        listViewModel.pageTextProperty().set("P\u00e1gina " + (page.getPage() + 1) + " de " + Math.max(page.getTotalPages(), 1));
        listViewModel.previousPageAvailableProperty().set(!page.isFirst());
        listViewModel.nextPageAvailableProperty().set(!page.isLast());
        listViewModel.statusTextProperty().set("Mostrando " + page.getNumberOfElements() + " de " + page.getTotalElements() + " secciones.");
    }

    private void loadSeccion(long seccionId, Consumer<SeccionResponseDto> onSuccess) {
        detailViewModel.loadingProperty().set(true);
        detailViewModel.bannerMessageProperty().set("");
        FxExecutors.submitIo(() -> seccionesService.obtenerPorId(seccionId), result -> {
            detailViewModel.loadingProperty().set(false);
            if (!result.isSuccess()) {
                detailViewModel.bannerMessageProperty().set(result.error().map(error -> error.message()).orElse("No se pudo cargar la secci\u00f3n."));
                detailViewModel.visibleProperty().set(true);
                showDetailPane();
                return;
            }
            onSuccess.accept(result.data().orElseThrow());
        });
    }

    private void showDetailDrawer(SeccionResponseDto seccion) {
        currentSeccion = seccion;
        detailViewModel.visibleProperty().set(true);
        detailViewModel.titleProperty().set("Detalle de la secci\u00f3n");
        detailViewModel.bannerMessageProperty().set("");
        detailViewModel.nombreProperty().set(seccion.displayName());
        detailViewModel.gradoProperty().set(String.valueOf(seccion.grado()));
        detailViewModel.paraleloProperty().set(seccion.paralelo());
        detailViewModel.cupoMaximoProperty().set(String.valueOf(seccion.cupoMaximo()));
        detailViewModel.anioLectivoProperty().set(seccion.anioLectivo());
        detailViewModel.estadoProperty().set(seccion.estado());
        detailEstadoButton.setText("ACTIVO".equalsIgnoreCase(seccion.estado()) ? "Inactivar" : "Activar");
        formViewModel.visibleProperty().set(false);
        showDetailPane();
    }

    private void openEditForm(SeccionResponseDto seccion) {
        currentSeccion = seccion;
        detailViewModel.visibleProperty().set(false);
        formViewModel.visibleProperty().set(true);
        formViewModel.titleProperty().set("Editar secci\u00f3n");
        formViewModel.bannerMessageProperty().set("");
        formGradoCombo.setValue(seccion.grado());
        formParaleloField.setText(seccion.paralelo());
        formCupoMaximoSpinner.getValueFactory().setValue(seccion.cupoMaximo());
        formAnioLectivoField.setText(seccion.anioLectivo());
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

    private ApiResult<SeccionResponseDto> submitForm() {
        commitCupoSpinnerValue();
        int grado = formViewModel.gradoProperty().get();
        String paralelo = formViewModel.paraleloProperty().get().trim();
        int cupoMaximo = formViewModel.cupoMaximoProperty().get();
        String anioLectivo = formViewModel.anioLectivoProperty().get().trim();
        if (currentSeccion == null) {
            return seccionesService.crear(new SeccionCreateRequestDto(grado, paralelo, cupoMaximo, anioLectivo));
        }
        return seccionesService.actualizar(
                currentSeccion.id(),
                new SeccionUpdateRequestDto(grado, paralelo, cupoMaximo, anioLectivo, currentSeccion.estado()));
    }

    private void applySaveResult(ApiResult<SeccionResponseDto> result) {
        formViewModel.loadingProperty().set(false);
        if (!result.isSuccess()) {
            formViewModel.bannerMessageProperty().set(result.error().map(error -> error.message()).orElse("No se pudo guardar la secci\u00f3n."));
            return;
        }
        SeccionResponseDto saved = result.data().orElseThrow();
        currentSeccion = saved;
        showDetailDrawer(saved);
        loadSecciones();
    }

    private void applyEstadoResult(ApiResult<SeccionResponseDto> result) {
        detailViewModel.loadingProperty().set(false);
        if (!result.isSuccess()) {
            detailViewModel.bannerMessageProperty().set(result.error().map(error -> error.message()).orElse("No se pudo cambiar el estado de la secci\u00f3n."));
            return;
        }
        SeccionResponseDto updated = result.data().orElseThrow();
        currentSeccion = updated;
        showDetailDrawer(updated);
        loadSecciones();
    }

    private Optional<String> validateForm() {
        commitCupoSpinnerValue();
        if (formViewModel.gradoProperty().get() == null) {
            return Optional.of("Selecciona un grado.");
        }
        String paralelo = formViewModel.paraleloProperty().get().trim();
        if (paralelo.isEmpty()) {
            return Optional.of("El paralelo es obligatorio.");
        }
        if (paralelo.length() > 10) {
            return Optional.of("El paralelo no debe exceder 10 caracteres.");
        }
        Integer cupoMaximo = formViewModel.cupoMaximoProperty().get();
        if (cupoMaximo == null) {
            return Optional.of("Selecciona un cupo m\u00e1ximo v\u00e1lido.");
        }
        if (cupoMaximo < 1 || cupoMaximo > 35) {
            return Optional.of("El cupo m\u00e1ximo debe estar entre 1 y 35.");
        }
        String anioLectivo = formViewModel.anioLectivoProperty().get().trim();
        if (!ANIO_LECTIVO_PATTERN.matcher(anioLectivo).matches()) {
            return Optional.of("El a\u00f1o lectivo debe tener formato YYYY-YYYY.");
        }
        return Optional.empty();
    }

    private void clearForm() {
        formGradoCombo.getSelectionModel().clearSelection();
        formParaleloField.clear();
        formCupoMaximoSpinner.getValueFactory().setValue(35);
        formAnioLectivoField.clear();
    }

    private void commitCupoSpinnerValue() {
        if (!formCupoMaximoSpinner.isEditable()) {
            return;
        }
        String editorText = formCupoMaximoSpinner.getEditor().getText();
        if (editorText == null || editorText.isBlank()) {
            formViewModel.cupoMaximoProperty().set(null);
            return;
        }
        try {
            int value = Integer.parseInt(editorText.trim());
            formCupoMaximoSpinner.getValueFactory().setValue(value);
        } catch (NumberFormatException ex) {
            formViewModel.cupoMaximoProperty().set(null);
        }
    }

    private String normalizeEstadoFilter() {
        String value = estadoFilter.getValue();
        if (value == null || "TODOS".equalsIgnoreCase(value)) {
            return null;
        }
        return value;
    }

    private SeccionesListQuery buildListQuery() {
        return new SeccionesListQuery(
                listViewModel.pageProperty().get(),
                PAGE_SIZE,
                listViewModel.queryTextProperty().get(),
                normalizeEstadoFilter(),
                gradoFilter.getValue(),
                listViewModel.paraleloFilterProperty().get(),
                listViewModel.anioLectivoFilterProperty().get());
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
