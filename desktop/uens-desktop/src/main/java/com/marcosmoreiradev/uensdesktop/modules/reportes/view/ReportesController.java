package com.marcosmoreiradev.uensdesktop.modules.reportes.view;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudCreatedResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudDetailResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudResultResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto.SeccionListItemDto;
import com.marcosmoreiradev.uensdesktop.app.AppContext;
import com.marcosmoreiradev.uensdesktop.app.ContextAwareController;
import com.marcosmoreiradev.uensdesktop.common.error.ErrorMessages;
import com.marcosmoreiradev.uensdesktop.common.util.DesktopFileSupport;
import com.marcosmoreiradev.uensdesktop.nav.ViewId;
import com.marcosmoreiradev.uensdesktop.modules.reportes.application.ReporteDownloadResult;
import com.marcosmoreiradev.uensdesktop.modules.reportes.application.ReportePollingService;
import com.marcosmoreiradev.uensdesktop.modules.reportes.application.ReportesListQuery;
import com.marcosmoreiradev.uensdesktop.modules.reportes.application.ReportesReferenceDataService;
import com.marcosmoreiradev.uensdesktop.modules.reportes.application.ReportesService;
import com.marcosmoreiradev.uensdesktop.modules.reportes.presenter.ReportesPresenter;
import com.marcosmoreiradev.uensdesktop.modules.reportes.viewmodel.CrearReporteSolicitudFormViewModel;
import com.marcosmoreiradev.uensdesktop.modules.reportes.viewmodel.ReporteSolicitudDetailViewModel;
import com.marcosmoreiradev.uensdesktop.modules.reportes.viewmodel.ReporteSolicitudListViewModel;
import com.marcosmoreiradev.uensdesktop.modules.reportes.viewmodel.ReportesViewModel;
import com.marcosmoreiradev.uensdesktop.session.Role;
import com.marcosmoreiradev.uensdesktop.ui.assets.FxUiAssets;
import com.marcosmoreiradev.uensdesktop.ui.assets.UiAssetId;
import com.marcosmoreiradev.uensdesktop.ui.command.UiCommand;
import com.marcosmoreiradev.uensdesktop.ui.command.UiCommands;
import com.marcosmoreiradev.uensdesktop.ui.drawer.DrawerCoordinator;
import com.marcosmoreiradev.uensdesktop.ui.fx.FxExecutors;
import com.marcosmoreiradev.uensdesktop.ui.fx.SearchableComboBoxSupport;
import com.marcosmoreiradev.uensdesktop.ui.fx.UiStyling;
import com.marcosmoreiradev.uensdesktop.ui.theme.TypographyManager;
import com.marcosmoreiradev.uensdesktop.ui.tooltip.TooltipSupport;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.StringConverter;

public final class ReportesController implements ContextAwareController {

    private static final int PAGE_SIZE = 15;
    private static final ObservableList<String> TIPOS_REPORTE = FXCollections.observableArrayList(
            "LISTADO_ESTUDIANTES_POR_SECCION",
            "CALIFICACIONES_POR_SECCION_Y_PARCIAL");
    private static final ObservableList<String> FORMATOS_SALIDA = FXCollections.observableArrayList("XLSX", "PDF", "DOCX");
    private static final ObservableList<String> ESTADOS = FXCollections.observableArrayList("PENDIENTE", "EN_PROCESO", "COMPLETADA", "ERROR");
    private static final ObservableList<Integer> PARCIALES = FXCollections.observableArrayList(1, 2);
    @FXML private ComboBox<String> tipoReporteCombo;
    @FXML private ComboBox<String> formatoSalidaCombo;
    @FXML private ComboBox<SeccionListItemDto> seccionCombo;
    @FXML private ComboBox<Integer> numeroParcialCombo;
    @FXML private DatePicker fechaDesdePicker;
    @FXML private DatePicker fechaHastaPicker;
    @FXML private Button crearReporteButton;
    @FXML private Button requestInfoButton;
    @FXML private Label formBannerLabel;
    @FXML private VBox numeroParcialFieldBox;
    @FXML private TextField queryField;
    @FXML private ComboBox<String> tipoReporteFilterCombo;
    @FXML private ComboBox<String> estadoFilterCombo;
    @FXML private Button buscarButton;
    @FXML private Button limpiarButton;
    @FXML private Button anteriorButton;
    @FXML private Button siguienteButton;
    @FXML private Label statusLabel;
    @FXML private Label totalLabel;
    @FXML private Label pageLabel;
    @FXML private Label pollingLabel;
    @FXML private TableView<ReporteSolicitudListItemDto> reportesTable;
    @FXML private TableColumn<ReporteSolicitudListItemDto, Long> solicitudIdColumn;
    @FXML private TableColumn<ReporteSolicitudListItemDto, String> tipoReporteColumn;
    @FXML private TableColumn<ReporteSolicitudListItemDto, String> estadoColumn;
    @FXML private TableColumn<ReporteSolicitudListItemDto, String> fechaSolicitudColumn;
    @FXML private TableColumn<ReporteSolicitudListItemDto, String> fechaActualizacionColumn;
    @FXML private TableColumn<ReporteSolicitudListItemDto, Integer> intentosColumn;
    @FXML private TableColumn<ReporteSolicitudListItemDto, Void> accionesColumn;
    @FXML private VBox drawerPane;
    @FXML private Label drawerTitleLabel;
    @FXML private Label drawerBannerLabel;
    @FXML private Label downloadPathLabel;
    @FXML private Label detailSolicitudIdLabel;
    @FXML private Label detailTipoReporteLabel;
    @FXML private Label detailEstadoLabel;
    @FXML private Label detailIntentosLabel;
    @FXML private Label detailFechaSolicitudLabel;
    @FXML private Label detailFechaActualizacionLabel;
    @FXML private TextArea detailParametrosArea;
    @FXML private TextArea detailResultadoArea;
    @FXML private TextArea detailErrorArea;
    @FXML private Button refrescarDetalleButton;
    @FXML private Button descargarDetalleButton;
    @FXML private Button reintentarDetalleButton;

    private final ReportesViewModel reportesViewModel = new ReportesViewModel();
    private final ReporteSolicitudListViewModel listViewModel = new ReporteSolicitudListViewModel();
    private final CrearReporteSolicitudFormViewModel formViewModel = new CrearReporteSolicitudFormViewModel();
    private final ReporteSolicitudDetailViewModel detailViewModel = new ReporteSolicitudDetailViewModel();
    private final ReportesPresenter presenter = new ReportesPresenter();
    private final ObservableList<SeccionListItemDto> secciones = FXCollections.observableArrayList();
    private final Map<Long, SeccionListItemDto> seccionesById = new HashMap<>();

    private AppContext appContext;
    private ReportesService reportesService;
    private ReportesReferenceDataService referenceDataService;
    private ReportePollingService pollingService;
    private ReporteSolicitudDetailResponseDto currentDetail;
    private DrawerCoordinator drawerCoordinator;
    private UiCommand loadReferenceDataCommand;
    private UiCommand loadSolicitudesCommand;
    private UiCommand createReporteCommand;
    private boolean initialized;
    private boolean contextApplied;

    @FXML
    private void initialize() {
        configureForm();
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
        this.reportesService = appContext.services().reportesService();
        this.referenceDataService = appContext.services().reportesReferenceDataService();
        this.pollingService = appContext.services().createReportePollingService();
        initializeCommands();
        this.appContext.navigator().currentViewProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != ViewId.REPORTES && pollingService != null) {
                pollingService.stopAll();
            }
        });
        applyContextIfReady();
    }

    @FXML
    private void onCrearReporte() {
        SearchableComboBoxSupport.commitSelection(seccionCombo);
        Optional<String> validationError = validateForm();
        if (validationError.isPresent()) {
            formViewModel.bannerMessageProperty().set(validationError.get());
            return;
        }
        formViewModel.loadingProperty().set(true);
        formViewModel.bannerMessageProperty().set("");
        reportesViewModel.statusTextProperty().set("Creando solicitud de reporte...");
        createReporteCommand.execute();
    }

    @FXML
    private void onToggleRequestInfo() {
        appContext.feedback().showInformation(
                currentWindow(),
                "Informaci\u00f3n de solicitud",
                "C\u00f3mo funciona la solicitud de reportes",
                "La solicitud se env\u00eda al backend y queda en cola para procesamiento as\u00edncrono.\n\n"
                        + "El sistema sigue actualizando su estado hasta que el archivo queda listo para descarga,\n"
                        + "o permite reintento si ocurre un error durante la generaci\u00f3n.\n\n"
                        + "El reporte Auditor\u00eda admin resume la trazabilidad operativa: m\u00f3dulo, acci\u00f3n, actor, rol,\n"
                        + "resultado, entidad afectada y RequestId. Sirve para explicar qu\u00e9 pas\u00f3, qui\u00e9n lo ejecut\u00f3 y en qu\u00e9 punto fall\u00f3 o se complet\u00f3 el proceso.");
    }

    @FXML
    private void onBuscar() {
        listViewModel.pageProperty().set(0);
        loadSolicitudes();
    }

    @FXML
    private void onLimpiar() {
        queryField.clear();
        tipoReporteFilterCombo.getSelectionModel().clearSelection();
        estadoFilterCombo.getSelectionModel().clearSelection();
        listViewModel.pageProperty().set(0);
        loadSolicitudes();
    }

    @FXML
    private void onPaginaAnterior() {
        if (listViewModel.pageProperty().get() <= 0) {
            return;
        }
        listViewModel.pageProperty().set(listViewModel.pageProperty().get() - 1);
        loadSolicitudes();
    }

    @FXML
    private void onPaginaSiguiente() {
        if (!listViewModel.nextPageAvailableProperty().get()) {
            return;
        }
        listViewModel.pageProperty().set(listViewModel.pageProperty().get() + 1);
        loadSolicitudes();
    }

    @FXML
    private void onCerrarDrawer() {
        drawerCoordinator.hideAll();
        detailViewModel.visibleProperty().set(false);
        detailViewModel.bannerMessageProperty().set("");
        detailViewModel.downloadPathProperty().set("");
    }

    @FXML
    private void onRefrescarDetalle() {
        if (currentDetail != null) {
            loadDetalle(currentDetail.solicitudId(), true);
        }
    }

    @FXML
    private void onDescargarDetalle() {
        if (currentDetail == null || !presenter.isDownloadAvailable(currentDetail.estado())) {
            return;
        }
        downloadSolicitud(currentDetail.solicitudId(), currentDetail.tipoReporte());
    }

    @FXML
    private void onReintentarDetalle() {
        if (currentDetail == null || !isAdmin()) {
            return;
        }
        reintentarSolicitud(currentDetail.solicitudId());
    }

    private void applyContextIfReady() {
        if (!initialized || appContext == null || contextApplied) {
            return;
        }
        contextApplied = true;
        appContext.uiNotifications().consume().ifPresent(message -> reportesViewModel.pollingTextProperty().set(message.message()));
        loadReferenceDataAndSolicitudes();
    }

    private void configureForm() {
        tipoReporteCombo.setItems(TIPOS_REPORTE);
        formatoSalidaCombo.setItems(FORMATOS_SALIDA);
        numeroParcialCombo.setItems(PARCIALES);
        configureDisplayCombo(tipoReporteCombo, presenter::formatTipoReporte);
        SearchableComboBoxSupport.installLocalSearch(seccionCombo, secciones, SeccionListItemDto::displayName);
        configureInfoButton();
        tipoReporteCombo.valueProperty().addListener((observable, oldValue, newValue) -> refreshFormForTipoReporte(newValue));
        refreshFormForTipoReporte(tipoReporteCombo.getValue());
    }

    private void configureFilters() {
        tipoReporteFilterCombo.setItems(TIPOS_REPORTE);
        estadoFilterCombo.setItems(ESTADOS);
        configureDisplayCombo(tipoReporteFilterCombo, presenter::formatTipoReporte);
        configureDisplayCombo(estadoFilterCombo, presenter::formatEstado);
    }

    private void configureDisplayCombo(ComboBox<String> comboBox, Function<String, String> displayFormatter) {
        StringConverter<String> converter = new StringConverter<>() {
            @Override
            public String toString(String value) {
                return value == null ? "" : displayFormatter.apply(value);
            }

            @Override
            public String fromString(String string) {
                if (string == null || string.isBlank()) {
                    return null;
                }
                String trimmed = string.trim();
                return comboBox.getItems().stream()
                        .filter(item -> trimmed.equalsIgnoreCase(item)
                                || trimmed.equalsIgnoreCase(displayFormatter.apply(item)))
                        .findFirst()
                        .orElse(trimmed);
            }
        };
        comboBox.setConverter(converter);
        comboBox.setVisibleRowCount(8);
        comboBox.setButtonCell(new ListCellWithConverter<>(converter));
        comboBox.setCellFactory(listView -> new ListCellWithConverter<>(converter));
    }

    private void configureInfoButton() {
        FxUiAssets.image(UiAssetId.INFORMATION_ICON).ifPresent(image -> {
            ImageView iconView = new ImageView(image);
            iconView.setFitWidth(18);
            iconView.setFitHeight(18);
            iconView.setPreserveRatio(true);
            requestInfoButton.setGraphic(iconView);
        });
        requestInfoButton.setText(null);
    }

    private void configureTable() {
        UiStyling.configureTableToFillWidth(reportesTable);
        solicitudIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().solicitudId()));
        tipoReporteColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(presenter.formatTipoReporte(cellData.getValue().tipoReporte())));
        estadoColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().estado()));
        estadoColumn.setCellFactory(column -> new TableCell<>() {
            private final Label label = new Label();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                label.setText(presenter.formatEstado(item));
                label.getStyleClass().setAll("badge-like", presenter.statusStyleClass(item));
                setGraphic(label);
            }
        });
        fechaSolicitudColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(presenter.formatDateTime(cellData.getValue().fechaSolicitud())));
        fechaActualizacionColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(presenter.formatDateTime(cellData.getValue().fechaActualizacion())));
        intentosColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().intentos()));
        accionesColumn.setCellFactory(column -> new TableCell<>() {
            private final Button verButton = new Button("Ver");
            private final Button refrescarButton = new Button("Consultar estado");
            private final Button descargarButton = new Button("Descargar");
            private final Button reintentarButton = new Button("Reintentar");
            private final HBox container = new HBox(6, verButton, refrescarButton, descargarButton, reintentarButton);

            {
                UiStyling.addStyleClasses(verButton, "table-action-button", "action-view-button");
                UiStyling.addStyleClasses(refrescarButton, "table-action-button", "action-refresh-button");
                UiStyling.addStyleClasses(descargarButton, "table-action-button", "action-download-button");
                UiStyling.addStyleClasses(reintentarButton, "table-action-button", "action-retry-button");
                TooltipSupport.refresh(verButton, refrescarButton, descargarButton, reintentarButton);
                verButton.setOnAction(event -> openFromRow(item -> loadDetalle(item.solicitudId(), true)));
                refrescarButton.setOnAction(event -> openFromRow(item -> refreshEstado(item.solicitudId())));
                descargarButton.setOnAction(event -> openFromRow(item -> downloadSolicitud(item.solicitudId(), item.tipoReporte())));
                reintentarButton.setOnAction(event -> openFromRow(item -> reintentarSolicitud(item.solicitudId())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                ReporteSolicitudListItemDto currentItem = getCurrentItem();
                if (empty || currentItem == null) {
                    setGraphic(null);
                    return;
                }
                descargarButton.setDisable(!presenter.isDownloadAvailable(currentItem.estado()));
                reintentarButton.setVisible(isAdmin() && presenter.isErrorState(currentItem.estado()));
                reintentarButton.setManaged(reintentarButton.isVisible());
                setGraphic(container);
            }

            private void openFromRow(Consumer<ReporteSolicitudListItemDto> consumer) {
                ReporteSolicitudListItemDto currentItem = getCurrentItem();
                if (currentItem != null) {
                    consumer.accept(currentItem);
                }
            }

            private ReporteSolicitudListItemDto getCurrentItem() {
                if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    return null;
                }
                return getTableView().getItems().get(getIndex());
            }
        });
        reportesTable.setItems(listViewModel.items());
        reportesTable.setPlaceholder(new Label("Sin solicitudes de reporte para los filtros actuales."));
        reportesTable.setRowFactory(table -> {
            TableRow<ReporteSolicitudListItemDto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    loadDetalle(row.getItem().solicitudId(), true);
                }
            });
            return row;
        });
    }

    private void configureDrawer() {
        drawerCoordinator = new DrawerCoordinator(drawerPane);
        drawerCoordinator.hideAll();
        detailParametrosArea.setEditable(false);
        detailResultadoArea.setEditable(false);
        detailErrorArea.setEditable(false);
        UiStyling.addStyleClasses(detailParametrosArea, "mono-area");
        UiStyling.addStyleClasses(detailResultadoArea, "mono-area");
        UiStyling.addStyleClasses(detailErrorArea, "mono-area");
        UiStyling.addStyleClasses(downloadPathLabel, "text-mono", "text-small");
        TypographyManager.applyMono(detailParametrosArea, detailResultadoArea, detailErrorArea, downloadPathLabel);
        drawerBannerLabel.setTextOverrun(OverrunStyle.CLIP);
        drawerBannerLabel.setMinHeight(Region.USE_PREF_SIZE);
        drawerBannerLabel.prefWidthProperty().bind(drawerPane.widthProperty().subtract(36));
        downloadPathLabel.setTextOverrun(OverrunStyle.CLIP);
        downloadPathLabel.setMinHeight(Region.USE_PREF_SIZE);
        downloadPathLabel.prefWidthProperty().bind(drawerPane.widthProperty().subtract(36));
    }

    private void bindViewModels() {
        statusLabel.textProperty().bind(reportesViewModel.statusTextProperty());
        pollingLabel.textProperty().bind(reportesViewModel.pollingTextProperty());
        totalLabel.textProperty().bind(listViewModel.totalTextProperty());
        pageLabel.textProperty().bind(listViewModel.pageTextProperty());
        formBannerLabel.textProperty().bind(formViewModel.bannerMessageProperty());
        UiStyling.bindVisibleWhenTextPresent(formBannerLabel);
        UiStyling.installAutoHideBanner(formViewModel.bannerMessageProperty());
        drawerTitleLabel.textProperty().bind(detailViewModel.titleProperty());
        drawerBannerLabel.textProperty().bind(detailViewModel.bannerMessageProperty());
        UiStyling.bindVisibleWhenTextPresent(drawerBannerLabel);
        UiStyling.installAutoHideBanner(detailViewModel.bannerMessageProperty());
        downloadPathLabel.textProperty().bind(detailViewModel.downloadPathProperty());
        UiStyling.bindVisibleWhenTextPresent(downloadPathLabel);
        detailSolicitudIdLabel.textProperty().bind(detailViewModel.solicitudIdProperty());
        detailTipoReporteLabel.textProperty().bind(detailViewModel.tipoReporteProperty());
        detailEstadoLabel.textProperty().bind(detailViewModel.estadoProperty());
        detailIntentosLabel.textProperty().bind(detailViewModel.intentosProperty());
        detailFechaSolicitudLabel.textProperty().bind(detailViewModel.fechaSolicitudProperty());
        detailFechaActualizacionLabel.textProperty().bind(detailViewModel.fechaActualizacionProperty());
        detailParametrosArea.textProperty().bind(detailViewModel.parametrosJsonProperty());
        detailResultadoArea.textProperty().bind(detailViewModel.resultadoJsonProperty());
        detailErrorArea.textProperty().bind(detailViewModel.errorDetalleProperty());
        crearReporteButton.disableProperty().bind(formViewModel.canSubmitBinding().not());
        buscarButton.disableProperty().bind(listViewModel.loadingProperty());
        limpiarButton.disableProperty().bind(listViewModel.loadingProperty());
        anteriorButton.disableProperty().bind(listViewModel.loadingProperty().or(listViewModel.previousPageAvailableProperty().not()));
        siguienteButton.disableProperty().bind(listViewModel.loadingProperty().or(listViewModel.nextPageAvailableProperty().not()));
        numeroParcialFieldBox.visibleProperty().bind(formViewModel.parcialVisibleProperty());
        numeroParcialFieldBox.managedProperty().bind(formViewModel.parcialVisibleProperty());
    }

    private void loadReferenceDataAndSolicitudes() {
        listViewModel.loadingProperty().set(true);
            reportesViewModel.statusTextProperty().set("Cargando secciones y solicitudes de reporte...");
        loadReferenceDataCommand.execute();
    }

    private void applyReferenceData(ApiResult<PageResponse<SeccionListItemDto>> seccionesResult) {
        if (seccionesResult.isSuccess()) {
            secciones.setAll(seccionesResult.data().orElseThrow().getItems());
            seccionesById.clear();
            secciones.forEach(item -> seccionesById.put(item.id(), item));
            seccionCombo.setItems(secciones);
            if (secciones.isEmpty()) {
                formViewModel.bannerMessageProperty().set("No hay secciones disponibles para generar reportes.");
            }
        } else {
            formViewModel.bannerMessageProperty().set(seccionesResult.error()
                    .map(error -> ErrorMessages.userFacingMessage(error, "No se pudieron cargar las secciones."))
                    .orElse("No se pudieron cargar las secciones."));
        }
        loadSolicitudes();
    }

    private void loadSolicitudes() {
        listViewModel.loadingProperty().set(true);
        reportesViewModel.loadingProperty().set(true);
        reportesViewModel.statusTextProperty().set("Consultando historial de reportes...");
        reportesTable.setDisable(true);
        loadSolicitudesCommand.execute();
    }

    private void applyListResult(ApiResult<PageResponse<ReporteSolicitudListItemDto>> result) {
        listViewModel.loadingProperty().set(false);
        reportesViewModel.loadingProperty().set(false);
        reportesTable.setDisable(false);
        if (!result.isSuccess()) {
            reportesViewModel.statusTextProperty().set(result.error().map(error -> error.message()).orElse("No se pudo cargar el historial de reportes."));
            listViewModel.items().clear();
            listViewModel.totalTextProperty().set("0 solicitudes");
            listViewModel.pageTextProperty().set("P\u00e1gina 1 de 1");
            listViewModel.previousPageAvailableProperty().set(false);
            listViewModel.nextPageAvailableProperty().set(false);
            return;
        }
        PageResponse<ReporteSolicitudListItemDto> page = result.data().orElseThrow();
        listViewModel.items().setAll(page.getItems());
        listViewModel.pageProperty().set(page.getPage());
        listViewModel.totalTextProperty().set(page.getTotalElements() + " solicitudes registradas");
        listViewModel.pageTextProperty().set("P\u00e1gina " + (page.getPage() + 1) + " de " + Math.max(page.getTotalPages(), 1));
        listViewModel.previousPageAvailableProperty().set(!page.isFirst());
        listViewModel.nextPageAvailableProperty().set(!page.isLast());
        reportesViewModel.statusTextProperty().set("Mostrando " + page.getNumberOfElements() + " de " + page.getTotalElements() + " solicitudes.");
    }

    private void applyCreateResult(ApiResult<ReporteSolicitudCreatedResponseDto> result) {
        formViewModel.loadingProperty().set(false);
        if (!result.isSuccess()) {
            formViewModel.bannerMessageProperty().set(result.error()
                    .map(error -> ErrorMessages.userFacingMessage(error, "No se pudo crear la solicitud."))
                    .orElse("No se pudo crear la solicitud."));
            return;
        }
        ReporteSolicitudCreatedResponseDto created = result.data().orElseThrow();
        formViewModel.bannerMessageProperty().set("Solicitud #" + created.solicitudId() + " creada y encolada.");
        appContext.uiNotifications().pushSuccess("Solicitud de reporte #" + created.solicitudId() + " creada.");
        reportesViewModel.pollingTextProperty().set("Seguimiento activo para solicitud #" + created.solicitudId() + ".");
        clearForm();
        loadSolicitudes();
        loadDetalle(created.solicitudId(), true);
        startPolling(created.solicitudId());
    }

    private void loadDetalle(long solicitudId, boolean openDrawer) {
        detailViewModel.loadingProperty().set(true);
        detailViewModel.bannerMessageProperty().set("");
        if (openDrawer && (currentDetail == null || !currentDetail.solicitudId().equals(solicitudId))) {
            detailViewModel.downloadPathProperty().set("");
        }
        if (openDrawer) {
            drawerCoordinator.show();
        }
        UiCommands.io(
                () -> reportesService.obtenerDetalle(solicitudId),
                result -> applyDetailResult(result, openDrawer)).execute();
    }

    private void applyDetailResult(ApiResult<ReporteSolicitudDetailResponseDto> result, boolean openDrawer) {
        detailViewModel.loadingProperty().set(false);
        if (!result.isSuccess()) {
            detailViewModel.bannerMessageProperty().set(result.error()
                    .map(error -> ErrorMessages.userFacingMessage(error, "No se pudo cargar el detalle del reporte."))
                    .orElse("No se pudo cargar el detalle del reporte."));
            if (openDrawer) {
                detailViewModel.visibleProperty().set(true);
                drawerCoordinator.show();
            }
            return;
        }
        showDetail(result.data().orElseThrow(), openDrawer);
    }

    private void showDetail(ReporteSolicitudDetailResponseDto detail, boolean openDrawer) {
        currentDetail = detail;
        ReportesPresenter.ReporteSolicitudDetailPresentation presentation = presenter.presentDetail(detail);
        detailViewModel.visibleProperty().set(true);
        detailViewModel.titleProperty().set("Detalle de la solicitud");
        detailViewModel.bannerMessageProperty().set("");
        detailViewModel.solicitudIdProperty().set(presentation.solicitudId());
        detailViewModel.tipoReporteProperty().set(presentation.tipoReporte());
        detailViewModel.estadoProperty().set(presentation.estado());
        detailViewModel.intentosProperty().set(presentation.intentos());
        detailViewModel.fechaSolicitudProperty().set(presentation.fechaSolicitud());
        detailViewModel.fechaActualizacionProperty().set(presentation.fechaActualizacion());
        detailViewModel.parametrosJsonProperty().set(presentation.parametrosJson());
        detailViewModel.resultadoJsonProperty().set(presentation.resultadoJson());
        detailViewModel.errorDetalleProperty().set(presentation.errorDetalle());
        descargarDetalleButton.setDisable(!presentation.downloadAvailable());
        reintentarDetalleButton.setVisible(isAdmin() && presentation.errorState());
        reintentarDetalleButton.setManaged(reintentarDetalleButton.isVisible());
        if (openDrawer) {
            drawerCoordinator.show();
        }
    }

    private void refreshEstado(long solicitudId) {
        reportesViewModel.pollingTextProperty().set("Refrescando estado de solicitud #" + solicitudId + "...");
        UiCommands.io(
                () -> reportesService.obtenerEstado(solicitudId),
                result -> applyRefreshEstadoResult(solicitudId, result)).execute();
    }

    private void applyRefreshEstadoResult(long solicitudId, ApiResult<ReporteSolicitudResultResponseDto> result) {
        if (!result.isSuccess()) {
            reportesViewModel.pollingTextProperty().set(result.error()
                    .map(error -> ErrorMessages.userFacingMessage(error, "No se pudo refrescar el estado del reporte."))
                    .orElse("No se pudo refrescar el estado del reporte."));
            return;
        }
        ReporteSolicitudResultResponseDto dto = result.data().orElseThrow();
        reportesViewModel.pollingTextProperty().set(presenter.buildPollingStatus(solicitudId, dto.estado()));
        loadSolicitudes();
        if (currentDetail != null && currentDetail.solicitudId().equals(solicitudId)) {
            loadDetalle(solicitudId, false);
        }
        if (!isTerminalState(dto.estado())) {
            startPolling(solicitudId);
        }
    }

    private void startPolling(long solicitudId) {
        reportesViewModel.pollingTextProperty().set("Seguimiento activo para solicitud #" + solicitudId + ".");
        pollingService.startPolling(solicitudId, FxExecutors.uiConsumer(result -> applyPollingResult(solicitudId, result)));
    }

    private void applyPollingResult(long solicitudId, ApiResult<ReporteSolicitudResultResponseDto> result) {
        if (!result.isSuccess()) {
            reportesViewModel.pollingTextProperty().set(result.error()
                    .map(error -> ErrorMessages.userFacingMessage(error, "No se pudo actualizar el estado del reporte."))
                    .orElse("No se pudo actualizar el estado del reporte."));
            return;
        }
        ReporteSolicitudResultResponseDto dto = result.data().orElseThrow();
        reportesViewModel.pollingTextProperty().set(presenter.buildPollingStatus(solicitudId, dto.estado()));
        loadSolicitudes();
        if (currentDetail != null && currentDetail.solicitudId().equals(solicitudId)) {
            detailViewModel.estadoProperty().set(presenter.formatEstado(dto.estado()));
            detailViewModel.resultadoJsonProperty().set(presenter.formatJsonBlock(dto.resultadoJson()));
            detailViewModel.errorDetalleProperty().set(presenter.formatJsonBlock(dto.errorDetalle()));
            descargarDetalleButton.setDisable(!presenter.isDownloadAvailable(dto.estado()));
            reintentarDetalleButton.setVisible(isAdmin() && presenter.isErrorState(dto.estado()));
            reintentarDetalleButton.setManaged(reintentarDetalleButton.isVisible());
            loadDetalle(solicitudId, false);
        }
        if (isTerminalState(dto.estado())) {
            reportesViewModel.pollingTextProperty().set(presenter.buildPollingFinishedStatus(solicitudId, dto.estado()));
        }
    }

    private void reintentarSolicitud(long solicitudId) {
        reportesViewModel.pollingTextProperty().set("Reencolando solicitud #" + solicitudId + "...");
        UiCommands.io(() -> reportesService.reintentar(solicitudId), this::applyRetryResult).execute();
    }

    private void applyRetryResult(ApiResult<ReporteSolicitudCreatedResponseDto> result) {
        if (!result.isSuccess()) {
            String message = result.error()
                    .map(error -> ErrorMessages.userFacingMessage(error, "No se pudo reintentar la solicitud."))
                    .orElse("No se pudo reintentar la solicitud.");
            reportesViewModel.pollingTextProperty().set(message);
            detailViewModel.bannerMessageProperty().set(message);
            return;
        }
        ReporteSolicitudCreatedResponseDto dto = result.data().orElseThrow();
        reportesViewModel.pollingTextProperty().set("Solicitud #" + dto.solicitudId() + " reencolada correctamente.");
        loadSolicitudes();
        loadDetalle(dto.solicitudId(), false);
        startPolling(dto.solicitudId());
    }

    private void downloadSolicitud(long solicitudId, String tipoReporte) {
        reportesViewModel.pollingTextProperty().set("Descargando archivo de solicitud #" + solicitudId + "...");
        UiCommands.io(
                () -> reportesService.descargarArchivo(solicitudId, tipoReporte),
                this::applyDownloadResult).execute();
    }

    private void applyDownloadResult(ApiResult<ReporteDownloadResult> result) {
        if (!result.isSuccess()) {
            String message = result.error()
                    .map(error -> ErrorMessages.userFacingMessage(error, "No se pudo descargar el archivo."))
                    .orElse("No se pudo descargar el archivo.");
            reportesViewModel.pollingTextProperty().set(message);
            detailViewModel.bannerMessageProperty().set(message);
            return;
        }
        ReporteDownloadResult download = result.data().orElseThrow();
        boolean revealed = DesktopFileSupport.revealInFileExplorer(download.path());
        String message = revealed
                ? "Archivo " + download.fileName() + " descargado. Se abri\u00f3 la carpeta de destino."
                : "Archivo " + download.fileName() + " descargado correctamente.";
        reportesViewModel.pollingTextProperty().set(message);
        detailViewModel.bannerMessageProperty().set("");
        detailViewModel.downloadPathProperty().set(presenter.buildDownloadPathLabel(download.path()));
        appContext.feedback().toastSuccess(message);
    }

    private Optional<String> validateForm() {
        if (tipoReporteCombo.getValue() == null) {
            return Optional.of("Selecciona un tipo de reporte.");
        }
        if (formatoSalidaCombo.getValue() == null) {
            return Optional.of("Selecciona un formato de salida.");
        }
        if (seccionCombo.getValue() == null) {
            return Optional.of("Selecciona una secci\u00f3n.");
        }
        if ("CALIFICACIONES_POR_SECCION_Y_PARCIAL".equals(tipoReporteCombo.getValue()) && numeroParcialCombo.getValue() == null) {
            return Optional.of("Selecciona el parcial para el reporte de calificaciones.");
        }
        LocalDate fechaDesde = fechaDesdePicker.getValue();
        LocalDate fechaHasta = fechaHastaPicker.getValue();
        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            return Optional.of("La fecha desde no puede ser mayor que la fecha hasta.");
        }
        return Optional.empty();
    }

    private ReporteSolicitudCreateRequestDto buildCreateRequest() {
        return new ReporteSolicitudCreateRequestDto(
                tipoReporteCombo.getValue(),
                formatoSalidaCombo.getValue(),
                seccionCombo.getValue().id(),
                "CALIFICACIONES_POR_SECCION_Y_PARCIAL".equals(tipoReporteCombo.getValue()) ? numeroParcialCombo.getValue() : null,
                fechaDesdePicker.getValue(),
                fechaHastaPicker.getValue());
    }

    private void clearForm() {
        tipoReporteCombo.getSelectionModel().clearSelection();
        formatoSalidaCombo.getSelectionModel().clearSelection();
        seccionCombo.getSelectionModel().clearSelection();
        numeroParcialCombo.getSelectionModel().clearSelection();
        fechaDesdePicker.setValue(null);
        fechaHastaPicker.setValue(null);
        refreshFormForTipoReporte(null);
    }

    private ReportesListQuery buildListQuery() {
        return new ReportesListQuery(
                listViewModel.pageProperty().get(),
                PAGE_SIZE,
                emptyToNull(queryField.getText()),
                tipoReporteFilterCombo.getValue(),
                estadoFilterCombo.getValue());
    }

    private void refreshFormForTipoReporte(String tipoReporte) {
        boolean requiereParcial = "CALIFICACIONES_POR_SECCION_Y_PARCIAL".equals(tipoReporte);
        formViewModel.parcialVisibleProperty().set(requiereParcial);
        if (!requiereParcial) {
            numeroParcialCombo.getSelectionModel().clearSelection();
        }
    }

    private boolean isAdmin() {
        return appContext.sessionState().role().map(role -> role == Role.ADMIN).orElse(false);
    }

    private boolean isTerminalState(String estado) {
        return "COMPLETADA".equalsIgnoreCase(estado) || "ERROR".equalsIgnoreCase(estado);
    }

    private void initializeCommands() {
        loadReferenceDataCommand = UiCommands.io(referenceDataService::listarSeccionesDisponibles, this::applyReferenceData);
        loadSolicitudesCommand = UiCommands.io(() -> reportesService.listar(buildListQuery()), this::applyListResult);
        createReporteCommand = UiCommands.io(() -> reportesService.crear(buildCreateRequest()), this::applyCreateResult);
    }

    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Window currentWindow() {
        return drawerPane.getScene() == null ? null : drawerPane.getScene().getWindow();
    }

    private static final class ListCellWithConverter<T> extends ListCell<T> {

        private final StringConverter<T> converter;

        private ListCellWithConverter(StringConverter<T> converter) {
            this.converter = converter;
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty ? null : converter.toString(item));
            setGraphic(null);
        }
    }
}
