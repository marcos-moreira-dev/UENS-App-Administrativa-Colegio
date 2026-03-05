package com.marcosmoreiradev.uensdesktop.modules.representantes.view;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.representantes.dto.RepresentanteLegalCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.representantes.dto.RepresentanteLegalListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.representantes.dto.RepresentanteLegalResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.representantes.dto.RepresentanteLegalUpdateRequestDto;
import com.marcosmoreiradev.uensdesktop.app.AppContext;
import com.marcosmoreiradev.uensdesktop.app.ContextAwareController;
import com.marcosmoreiradev.uensdesktop.modules.representantes.application.RepresentantesService;
import com.marcosmoreiradev.uensdesktop.modules.representantes.viewmodel.RepresentanteDetailViewModel;
import com.marcosmoreiradev.uensdesktop.modules.representantes.viewmodel.RepresentanteFormViewModel;
import com.marcosmoreiradev.uensdesktop.modules.representantes.viewmodel.RepresentantesListViewModel;
import com.marcosmoreiradev.uensdesktop.ui.fx.FxExecutors;
import com.marcosmoreiradev.uensdesktop.ui.fx.UiStyling;
import com.marcosmoreiradev.uensdesktop.ui.tooltip.TooltipSupport;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public final class RepresentantesController implements ContextAwareController {

    private static final int PAGE_SIZE = 20;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^$|^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    @FXML
    private TextField queryField;

    @FXML
    private Button buscarButton;

    @FXML
    private Button limpiarButton;

    @FXML
    private Button crearRepresentanteButton;

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
    private TableView<RepresentanteLegalListItemDto> representantesTable;

    @FXML
    private TableColumn<RepresentanteLegalListItemDto, Long> idColumn;

    @FXML
    private TableColumn<RepresentanteLegalListItemDto, String> nombresColumn;

    @FXML
    private TableColumn<RepresentanteLegalListItemDto, String> apellidosColumn;

    @FXML
    private TableColumn<RepresentanteLegalListItemDto, String> telefonoColumn;

    @FXML
    private TableColumn<RepresentanteLegalListItemDto, String> correoColumn;

    @FXML
    private TableColumn<RepresentanteLegalListItemDto, Void> accionesColumn;

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
    private Label detailTelefonoLabel;

    @FXML
    private Label detailCorreoLabel;

    @FXML
    private TextField formNombresField;

    @FXML
    private TextField formApellidosField;

    @FXML
    private TextField formTelefonoField;

    @FXML
    private TextField formCorreoField;

    @FXML
    private Button detailEditarButton;

    @FXML
    private Button guardarButton;

    private final RepresentantesListViewModel listViewModel = new RepresentantesListViewModel();
    private final RepresentanteDetailViewModel detailViewModel = new RepresentanteDetailViewModel();
    private final RepresentanteFormViewModel formViewModel = new RepresentanteFormViewModel();

    private AppContext appContext;
    private RepresentantesService representantesService;
    private RepresentanteLegalResponseDto currentRepresentante;
    private boolean initialized;
    private boolean contextApplied;

    @FXML
    private void initialize() {
        configureTable();
        configureDrawer();
        bindViewModels();
        queryField.textProperty().bindBidirectional(listViewModel.queryTextProperty());
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
        this.representantesService = appContext.services().representantesService();
        applyContextIfReady();
    }

    @FXML
    private void onBuscar() {
        listViewModel.pageProperty().set(0);
        loadRepresentantes();
    }

    @FXML
    private void onLimpiar() {
        listViewModel.queryTextProperty().set("");
        listViewModel.pageProperty().set(0);
        loadRepresentantes();
    }

    @FXML
    private void onPaginaAnterior() {
        if (listViewModel.pageProperty().get() <= 0) {
            return;
        }
        listViewModel.pageProperty().set(listViewModel.pageProperty().get() - 1);
        loadRepresentantes();
    }

    @FXML
    private void onPaginaSiguiente() {
        if (!listViewModel.nextPageAvailableProperty().get()) {
            return;
        }
        listViewModel.pageProperty().set(listViewModel.pageProperty().get() + 1);
        loadRepresentantes();
    }

    @FXML
    private void onCrearRepresentante() {
        currentRepresentante = null;
        detailViewModel.visibleProperty().set(false);
        formViewModel.visibleProperty().set(true);
        formViewModel.titleProperty().set("Crear representante");
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
        if (currentRepresentante == null) {
            return;
        }
        openEditForm(currentRepresentante);
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
        loadRepresentantes();
    }

    private void configureTable() {
        UiStyling.configureTableToFillWidth(representantesTable);
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().id()));
        nombresColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().nombres()));
        apellidosColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().apellidos()));
        telefonoColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(safeText(cellData.getValue().telefono())));
        correoColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(safeText(cellData.getValue().correoElectronico())));
        accionesColumn.setCellFactory(column -> new TableCell<>() {
            private final Button verButton = new Button("Ver");
            private final Button editarButton = new Button("Editar");
            private final HBox container = new HBox(6, verButton, editarButton);

            {
                UiStyling.addStyleClasses(verButton, "table-action-button", "action-view-button");
                UiStyling.addStyleClasses(editarButton, "table-action-button", "action-edit-button");
                TooltipSupport.refresh(verButton, editarButton);
                verButton.setOnAction(event -> {
                    RepresentanteLegalListItemDto currentItem = getCurrentItem();
                    if (currentItem != null) {
                        loadRepresentante(currentItem.id(), RepresentantesController.this::showDetailDrawer);
                    }
                });
                editarButton.setOnAction(event -> {
                    RepresentanteLegalListItemDto currentItem = getCurrentItem();
                    if (currentItem != null) {
                        loadRepresentante(currentItem.id(), RepresentantesController.this::openEditForm);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || getCurrentItem() == null ? null : container);
            }

            private RepresentanteLegalListItemDto getCurrentItem() {
                if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    return null;
                }
                return getTableView().getItems().get(getIndex());
            }
        });

        representantesTable.setItems(listViewModel.items());
        representantesTable.setPlaceholder(new Label("Sin resultados para los filtros actuales."));
        representantesTable.setRowFactory(table -> {
            TableRow<RepresentanteLegalListItemDto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    loadRepresentante(row.getItem().id(), this::showDetailDrawer);
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
        crearRepresentanteButton.disableProperty().bind(listViewModel.loadingProperty());
        anteriorButton.disableProperty().bind(listViewModel.loadingProperty().or(listViewModel.previousPageAvailableProperty().not()));
        siguienteButton.disableProperty().bind(listViewModel.loadingProperty().or(listViewModel.nextPageAvailableProperty().not()));
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
        detailTelefonoLabel.textProperty().bind(detailViewModel.telefonoProperty());
        detailCorreoLabel.textProperty().bind(detailViewModel.correoProperty());
        guardarButton.disableProperty().bind(formViewModel.canSubmitBinding().not());
        detailEditarButton.managedProperty().bind(detailEditarButton.visibleProperty());
        guardarButton.managedProperty().bind(guardarButton.visibleProperty());
    }

    private void loadRepresentantes() {
        listViewModel.loadingProperty().set(true);
        listViewModel.statusTextProperty().set("Consultando representantes...");
        representantesTable.setDisable(true);
        FxExecutors.submitIo(() -> representantesService.listar(
                listViewModel.pageProperty().get(),
                PAGE_SIZE,
                listViewModel.queryTextProperty().get()), this::applyListResult);
    }

    private void applyListResult(ApiResult<PageResponse<RepresentanteLegalListItemDto>> result) {
        listViewModel.loadingProperty().set(false);
        representantesTable.setDisable(false);
        if (!result.isSuccess()) {
            listViewModel.statusTextProperty().set(
                    result.error().map(error -> error.message()).orElse("No se pudo cargar representantes."));
            listViewModel.items().clear();
            listViewModel.totalTextProperty().set("0 registros");
            listViewModel.pageTextProperty().set("P\u00e1gina 1 de 1");
            listViewModel.previousPageAvailableProperty().set(false);
            listViewModel.nextPageAvailableProperty().set(false);
            return;
        }

        PageResponse<RepresentanteLegalListItemDto> page = result.data().orElseThrow();
        listViewModel.items().setAll(page.getItems());
        listViewModel.pageProperty().set(page.getPage());
        listViewModel.totalTextProperty().set(page.getTotalElements() + " representantes registrados");
        listViewModel.pageTextProperty().set("P\u00e1gina " + (page.getPage() + 1) + " de " + Math.max(page.getTotalPages(), 1));
        listViewModel.previousPageAvailableProperty().set(!page.isFirst());
        listViewModel.nextPageAvailableProperty().set(!page.isLast());
        listViewModel.statusTextProperty().set(
                "Mostrando " + page.getNumberOfElements() + " de " + page.getTotalElements() + " representantes.");
    }

    private void loadRepresentante(long representanteId, Consumer<RepresentanteLegalResponseDto> onSuccess) {
        detailViewModel.loadingProperty().set(true);
        detailViewModel.bannerMessageProperty().set("");
        FxExecutors.submitIo(() -> representantesService.obtenerPorId(representanteId), result -> {
            detailViewModel.loadingProperty().set(false);
            if (!result.isSuccess()) {
                detailViewModel.bannerMessageProperty().set(
                        result.error().map(error -> error.message()).orElse("No se pudo cargar el representante."));
                detailViewModel.visibleProperty().set(true);
                showDetailPane();
                return;
            }
            onSuccess.accept(result.data().orElseThrow());
        });
    }

    private void showDetailDrawer(RepresentanteLegalResponseDto representante) {
        currentRepresentante = representante;
        detailViewModel.visibleProperty().set(true);
        detailViewModel.titleProperty().set("Detalle del representante");
        detailViewModel.bannerMessageProperty().set("");
        detailViewModel.nombresProperty().set(representante.nombres());
        detailViewModel.apellidosProperty().set(representante.apellidos());
        detailViewModel.telefonoProperty().set(safeText(representante.telefono()));
        detailViewModel.correoProperty().set(safeText(representante.correoElectronico()));
        formViewModel.visibleProperty().set(false);
        showDetailPane();
    }

    private void openEditForm(RepresentanteLegalResponseDto representante) {
        currentRepresentante = representante;
        detailViewModel.visibleProperty().set(false);
        formViewModel.visibleProperty().set(true);
        formViewModel.titleProperty().set("Editar representante");
        formViewModel.bannerMessageProperty().set("");
        formNombresField.setText(representante.nombres());
        formApellidosField.setText(representante.apellidos());
        formTelefonoField.setText(safeText(representante.telefono()));
        formCorreoField.setText(safeText(representante.correoElectronico()));
        showFormPane();
    }

    private ApiResult<RepresentanteLegalResponseDto> submitForm() {
        if (currentRepresentante != null) {
            return representantesService.actualizar(
                    currentRepresentante.id(),
                    new RepresentanteLegalUpdateRequestDto(
                            formNombresField.getText().trim(),
                            formApellidosField.getText().trim(),
                            normalizeOptional(formTelefonoField.getText()),
                            normalizeOptional(formCorreoField.getText())));
        }

        return representantesService.crear(new RepresentanteLegalCreateRequestDto(
                formNombresField.getText().trim(),
                formApellidosField.getText().trim(),
                normalizeOptional(formTelefonoField.getText()),
                normalizeOptional(formCorreoField.getText())));
    }

    private void applySaveResult(ApiResult<RepresentanteLegalResponseDto> result) {
        formViewModel.loadingProperty().set(false);
        if (!result.isSuccess()) {
            formViewModel.bannerMessageProperty().set(
                    result.error().map(error -> error.message()).orElse("No se pudo guardar el representante."));
            return;
        }

        listViewModel.statusTextProperty().set(
                currentRepresentante == null
                        ? "Representante creado correctamente."
                        : "Representante actualizado correctamente.");
        currentRepresentante = result.data().orElseThrow();
        onCerrarDrawer();
        loadRepresentantes();
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
