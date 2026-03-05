package com.marcosmoreiradev.uensdesktop.modules.auditoria.view;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.auditoria.dto.AuditoriaEventoListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.auditoria.dto.CrearAuditoriaReporteRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudCreatedResponseDto;
import com.marcosmoreiradev.uensdesktop.app.AppContext;
import com.marcosmoreiradev.uensdesktop.app.ContextAwareController;
import com.marcosmoreiradev.uensdesktop.common.error.ErrorMessages;
import com.marcosmoreiradev.uensdesktop.nav.ViewId;
import com.marcosmoreiradev.uensdesktop.modules.auditoria.application.AuditoriaEventosQuery;
import com.marcosmoreiradev.uensdesktop.modules.auditoria.presenter.AuditoriaPresenter;
import com.marcosmoreiradev.uensdesktop.modules.auditoria.application.AuditoriaService;
import com.marcosmoreiradev.uensdesktop.modules.auditoria.viewmodel.AuditoriaDetailViewModel;
import com.marcosmoreiradev.uensdesktop.modules.auditoria.viewmodel.AuditoriaListViewModel;
import com.marcosmoreiradev.uensdesktop.modules.auditoria.viewmodel.AuditoriaReporteFormViewModel;
import com.marcosmoreiradev.uensdesktop.ui.assets.FxUiAssets;
import com.marcosmoreiradev.uensdesktop.ui.assets.UiAssetId;
import com.marcosmoreiradev.uensdesktop.ui.command.UiCommand;
import com.marcosmoreiradev.uensdesktop.ui.command.UiCommands;
import com.marcosmoreiradev.uensdesktop.ui.drawer.DrawerCoordinator;
import com.marcosmoreiradev.uensdesktop.ui.fx.FxExecutors;
import com.marcosmoreiradev.uensdesktop.ui.fx.UiStyling;
import com.marcosmoreiradev.uensdesktop.ui.theme.TypographyManager;
import com.marcosmoreiradev.uensdesktop.ui.tooltip.TooltipSupport;
import java.time.LocalDate;
import java.util.Optional;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

public final class AuditoriaController implements ContextAwareController {

    private static final int PAGE_SIZE = 20;
    private static final ObservableList<String> MODULOS = FXCollections.observableArrayList(
            "AUTH", "DASHBOARD", "ASIGNATURA", "SECCION", "DOCENTE", "REPRESENTANTE",
            "ESTUDIANTE", "CLASE", "CALIFICACION", "REPORTE", "AUDITORIA");
    private static final ObservableList<String> RESULTADOS = FXCollections.observableArrayList("EXITO", "ERROR", "INFO", "ADVERTENCIA");
    private static final ObservableList<String> FORMATOS = FXCollections.observableArrayList("XLSX", "PDF", "DOCX");
    @FXML private TextField queryField;
    @FXML private ComboBox<String> moduloFilterCombo;
    @FXML private TextField accionFilterField;
    @FXML private ComboBox<String> resultadoFilterCombo;
    @FXML private TextField actorLoginFilterField;
    @FXML private DatePicker fechaDesdeFilterPicker;
    @FXML private DatePicker fechaHastaFilterPicker;
    @FXML private Button buscarButton;
    @FXML private Button limpiarButton;
    @FXML private Button solicitarReporteButton;
    @FXML private Button auditInfoButton;
    @FXML private Button anteriorButton;
    @FXML private Button siguienteButton;
    @FXML private Label statusLabel;
    @FXML private Label totalLabel;
    @FXML private Label pageLabel;

    @FXML private TableView<AuditoriaEventoListItemDto> auditoriaTable;
    @FXML private TableColumn<AuditoriaEventoListItemDto, String> fechaColumn;
    @FXML private TableColumn<AuditoriaEventoListItemDto, String> moduloColumn;
    @FXML private TableColumn<AuditoriaEventoListItemDto, String> accionColumn;
    @FXML private TableColumn<AuditoriaEventoListItemDto, String> resultadoColumn;
    @FXML private TableColumn<AuditoriaEventoListItemDto, String> entidadColumn;
    @FXML private TableColumn<AuditoriaEventoListItemDto, String> entidadIdColumn;
    @FXML private TableColumn<AuditoriaEventoListItemDto, String> actorLoginColumn;
    @FXML private TableColumn<AuditoriaEventoListItemDto, String> actorRolColumn;
    @FXML private TableColumn<AuditoriaEventoListItemDto, String> requestIdColumn;
    @FXML private TableColumn<AuditoriaEventoListItemDto, Void> accionesColumn;

    @FXML private VBox drawerPane;
    @FXML private Label drawerTitleLabel;
    @FXML private Label drawerBannerLabel;
    @FXML private VBox detailPane;
    @FXML private VBox reportePane;
    @FXML private Label detailModuloLabel;
    @FXML private Label detailAccionLabel;
    @FXML private Label detailResultadoLabel;
    @FXML private Label detailEntidadLabel;
    @FXML private Label detailEntidadIdLabel;
    @FXML private Label detailActorLabel;
    @FXML private TextField detailRequestIdField;
    @FXML private Button copyRequestIdButton;
    @FXML private Label detailIpLabel;
    @FXML private Label detailFechaLabel;
    @FXML private ComboBox<String> reporteFormatoCombo;
    @FXML private DatePicker reporteFechaDesdePicker;
    @FXML private DatePicker reporteFechaHastaPicker;
    @FXML private ComboBox<String> reporteModuloCombo;
    @FXML private TextField reporteAccionField;
    @FXML private ComboBox<String> reporteResultadoCombo;
    @FXML private TextField reporteActorLoginField;
    @FXML private CheckBox incluirDetalleCheck;
    @FXML private Button guardarReporteButton;

    private final AuditoriaListViewModel listViewModel = new AuditoriaListViewModel();
    private final AuditoriaDetailViewModel detailViewModel = new AuditoriaDetailViewModel();
    private final AuditoriaReporteFormViewModel reporteFormViewModel = new AuditoriaReporteFormViewModel();
    private final AuditoriaPresenter presenter = new AuditoriaPresenter();

    private AppContext appContext;
    private AuditoriaService auditoriaService;
    private AuditoriaEventoListItemDto currentEvento;
    private DrawerCoordinator drawerCoordinator;
    private UiCommand loadEventosCommand;
    private UiCommand solicitarReporteCommand;
    private boolean initialized;
    private boolean contextApplied;

    @FXML
    private void initialize() {
        configureFilters();
        configureTable();
        configureDrawer();
        configureInfoButton();
        bindViewModels();
        initialized = true;
        applyContextIfReady();
    }

    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
        this.auditoriaService = appContext.services().auditoriaService();
        initializeCommands();
        applyContextIfReady();
    }

    @FXML
    private void onBuscar() {
        listViewModel.pageProperty().set(0);
        loadEventos();
    }

    @FXML
    private void onLimpiar() {
        queryField.clear();
        moduloFilterCombo.getSelectionModel().clearSelection();
        accionFilterField.clear();
        resultadoFilterCombo.getSelectionModel().clearSelection();
        actorLoginFilterField.clear();
        fechaDesdeFilterPicker.setValue(null);
        fechaHastaFilterPicker.setValue(null);
        listViewModel.pageProperty().set(0);
        loadEventos();
    }

    @FXML
    private void onPaginaAnterior() {
        if (listViewModel.pageProperty().get() <= 0) {
            return;
        }
        listViewModel.pageProperty().set(listViewModel.pageProperty().get() - 1);
        loadEventos();
    }

    @FXML
    private void onPaginaSiguiente() {
        if (!listViewModel.nextPageAvailableProperty().get()) {
            return;
        }
        listViewModel.pageProperty().set(listViewModel.pageProperty().get() + 1);
        loadEventos();
    }

    @FXML
    private void onSolicitarReporte() {
        reporteFormViewModel.visibleProperty().set(true);
        reporteFormViewModel.bannerMessageProperty().set("");
        drawerTitleLabel.setText("Solicitar reporte de auditoría");
        drawerCoordinator.showOnly(reportePane);
    }

    @FXML
    private void onToggleAuditoriaInfo() {
        appContext.feedback().showInformation(
                currentWindow(),
                "Informaci\u00f3n de auditor\u00eda",
                "C\u00f3mo interpretar la trazabilidad operativa",
                "La auditor\u00eda es la bit\u00e1cora administrativa del sistema. Sirve para revisar qui\u00e9n ejecut\u00f3 una operaci\u00f3n, sobre qu\u00e9 registro trabaj\u00f3 y cu\u00e1l fue el resultado final.\n\n"
                        + "M\u00f3dulo indica el bloque funcional afectado, por ejemplo estudiantes, clases, reportes o autenticaci\u00f3n.\n"
                        + "Acci\u00f3n describe el verbo operativo que ocurri\u00f3, como crear, actualizar, reintentar, completar o generar un reporte.\n"
                        + "Resultado muestra si la operaci\u00f3n termin\u00f3 con \u00e9xito, error, informaci\u00f3n o advertencia.\n"
                        + "Entidad y EntidadId permiten ubicar el registro exacto que fue afectado por la acci\u00f3n.\n"
                        + "Actor y Rol identifican al usuario que realiz\u00f3 el trabajo y el nivel de autorizaci\u00f3n con el que oper\u00f3.\n"
                        + "RequestId sirve para seguir una misma petici\u00f3n t\u00e9cnica de punta a punta cuando administraci\u00f3n o soporte investigan incidentes.\n\n"
                        + "En la pr\u00e1ctica, este historial te ayuda a justificar cambios, revisar errores operativos, validar evidencia administrativa y detectar procesos pendientes o fallidos.\n"
                        + "Si necesitas compartir la trazabilidad con direcci\u00f3n o soporte, el bot\u00f3n Solicitar reporte exporta el recorte que est\u00e1s consultando.");
    }

    @FXML
    private void onCerrarDrawer() {
        drawerCoordinator.hideAll();
        reporteFormViewModel.bannerMessageProperty().set("");
        reporteFormViewModel.visibleProperty().set(false);
    }

    @FXML
    private void onGuardarReporte() {
        Optional<String> validationError = validateReporteForm();
        if (validationError.isPresent()) {
            reporteFormViewModel.bannerMessageProperty().set(validationError.get());
            return;
        }
        reporteFormViewModel.loadingProperty().set(true);
        reporteFormViewModel.bannerMessageProperty().set("");
        solicitarReporteCommand.execute();
    }

    @FXML
    private void onCopyRequestId() {
        String value = detailRequestIdField.getText();
        if (value == null || value.isBlank() || "-".equals(value)) {
            return;
        }
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(value);
        javafx.scene.input.Clipboard.getSystemClipboard().setContent(content);
        listViewModel.statusTextProperty().set("RequestId copiado al portapapeles.");
        appContext.feedback().toastInfo("RequestId copiado al portapapeles.");
    }

    private void applyContextIfReady() {
        if (!initialized || appContext == null || contextApplied) {
            return;
        }
        contextApplied = true;
        loadEventos();
    }

    private void configureFilters() {
        moduloFilterCombo.setItems(MODULOS);
        moduloFilterCombo.setEditable(true);
        resultadoFilterCombo.setItems(RESULTADOS);
        reporteModuloCombo.setItems(MODULOS);
        reporteModuloCombo.setEditable(true);
        reporteResultadoCombo.setItems(RESULTADOS);
        reporteFormatoCombo.setItems(FORMATOS);
        configureFilterTooltips();
    }

    private void configureFilterTooltips() {
        TooltipSupport.install(queryField,
                "Busca eventos por RequestId, entidad afectada, acción registrada o actor que ejecutó la operación.");
        TooltipSupport.install(moduloFilterCombo,
                "Filtra el historial por módulo funcional del sistema donde se generó la traza: estudiantes, clases, reportes, auditoría u otros.");
        TooltipSupport.install(accionFilterField,
                "Acota por el nombre técnico u operativo de la acción registrada, por ejemplo SOLICITUD_CREADA o WORKER_SOLICITUD_ERROR.");
        TooltipSupport.install(resultadoFilterCombo,
                "Limita la consulta al desenlace del evento auditado: éxito, error, advertencia o información.");
        TooltipSupport.install(actorLoginFilterField,
                "Filtra por el usuario del sistema que disparó la acción administrativa o automatizada.");
        TooltipSupport.install(fechaDesdeFilterPicker,
                "Fecha inicial de ocurrencia del evento dentro de la línea de tiempo auditada.");
        TooltipSupport.install(fechaHastaFilterPicker,
                "Fecha final de ocurrencia del evento para cerrar la ventana temporal de revisión.");
        TooltipSupport.install(buscarButton,
                "Aplica el recorte actual sobre el historial de auditoría y refresca la tabla de trazas.");
        TooltipSupport.install(limpiarButton,
                "Quita el recorte operativo actual y vuelve al listado general de eventos auditados.");
        TooltipSupport.install(solicitarReporteButton,
                "Genera un archivo con la traza filtrada para compartirla con dirección, soporte o control interno.");
        TooltipSupport.install(auditInfoButton,
                "Explica qué significa cada dato de auditoría y cómo usar esta pantalla para revisar la operación del sistema.");
    }

    private void configureInfoButton() {
        FxUiAssets.image(UiAssetId.INFORMATION_ICON).ifPresent(image -> {
            ImageView iconView = new ImageView(image);
            iconView.setFitWidth(18);
            iconView.setFitHeight(18);
            iconView.setPreserveRatio(true);
            auditInfoButton.setGraphic(iconView);
        });
        auditInfoButton.setText(null);
    }

    private void configureTable() {
        fechaColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(presenter.formatDateTime(cellData.getValue().fechaEvento())));
        moduloColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(presenter.formatText(cellData.getValue().modulo())));
        accionColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(presenter.formatText(cellData.getValue().accion())));
        resultadoColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(presenter.formatText(cellData.getValue().resultado())));
        resultadoColumn.setCellFactory(column -> new TableCell<>() {
            private final Label label = new Label();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                label.setText(item);
                label.getStyleClass().setAll("badge-like", presenter.resultadoStyleClass(item));
                setGraphic(label);
            }
        });
        entidadColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(presenter.formatText(cellData.getValue().entidad())));
        entidadIdColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(presenter.formatText(cellData.getValue().entidadId())));
        actorLoginColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(presenter.formatText(cellData.getValue().actorLogin())));
        actorRolColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(presenter.formatText(cellData.getValue().actorRol())));
        requestIdColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(presenter.formatText(cellData.getValue().requestId())));
        accionesColumn.setCellFactory(column -> new TableCell<>() {
            private final Button verButton = new Button("Ver");

            {
                UiStyling.addStyleClasses(verButton, "table-action-button", "action-view-button");
                TooltipSupport.refresh(verButton);
                verButton.setOnAction(event -> {
                    AuditoriaEventoListItemDto item = getCurrentItem();
                    if (item != null) {
                        showDetail(item);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || getCurrentItem() == null ? null : verButton);
            }

            private AuditoriaEventoListItemDto getCurrentItem() {
                if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    return null;
                }
                return getTableView().getItems().get(getIndex());
            }
        });
        auditoriaTable.setItems(listViewModel.items());
        auditoriaTable.setPlaceholder(new Label("Sin eventos para los filtros actuales."));
        auditoriaTable.setRowFactory(table -> {
            TableRow<AuditoriaEventoListItemDto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showDetail(row.getItem());
                }
            });
            return row;
        });
    }

    private void configureDrawer() {
        drawerCoordinator = new DrawerCoordinator(drawerPane, detailPane, reportePane);
        drawerCoordinator.hideAll();
        detailRequestIdField.setEditable(false);
        UiStyling.addStyleClasses(detailRequestIdField, "mono-field");
        TypographyManager.applyMono(detailRequestIdField);
    }

    private void bindViewModels() {
        statusLabel.textProperty().bind(listViewModel.statusTextProperty());
        totalLabel.textProperty().bind(listViewModel.totalTextProperty());
        pageLabel.textProperty().bind(listViewModel.pageTextProperty());
        buscarButton.disableProperty().bind(listViewModel.loadingProperty());
        limpiarButton.disableProperty().bind(listViewModel.loadingProperty());
        solicitarReporteButton.disableProperty().bind(listViewModel.loadingProperty());
        anteriorButton.disableProperty().bind(listViewModel.loadingProperty().or(listViewModel.previousPageAvailableProperty().not()));
        siguienteButton.disableProperty().bind(listViewModel.loadingProperty().or(listViewModel.nextPageAvailableProperty().not()));
        guardarReporteButton.disableProperty().bind(reporteFormViewModel.canSubmitBinding().not());
        guardarReporteButton.visibleProperty().bind(reporteFormViewModel.visibleProperty());
        guardarReporteButton.managedProperty().bind(reporteFormViewModel.visibleProperty());
        copyRequestIdButton.visibleProperty().bind(reporteFormViewModel.visibleProperty().not());
        copyRequestIdButton.managedProperty().bind(reporteFormViewModel.visibleProperty().not());
        drawerBannerLabel.textProperty().bind(Bindings.when(reporteFormViewModel.visibleProperty())
                .then(reporteFormViewModel.bannerMessageProperty())
                .otherwise(""));
        UiStyling.bindVisibleWhenTextPresent(drawerBannerLabel);
        UiStyling.installAutoHideBanner(reporteFormViewModel.bannerMessageProperty());
        detailModuloLabel.textProperty().bind(detailViewModel.moduloProperty());
        detailAccionLabel.textProperty().bind(detailViewModel.accionProperty());
        detailResultadoLabel.textProperty().bind(detailViewModel.resultadoProperty());
        detailEntidadLabel.textProperty().bind(detailViewModel.entidadProperty());
        detailEntidadIdLabel.textProperty().bind(detailViewModel.entidadIdProperty());
        detailActorLabel.textProperty().bind(detailViewModel.actorProperty());
        detailRequestIdField.textProperty().bind(detailViewModel.requestIdProperty());
        detailIpLabel.textProperty().bind(detailViewModel.ipOrigenProperty());
        detailFechaLabel.textProperty().bind(detailViewModel.fechaEventoProperty());
    }

    private void loadEventos() {
        listViewModel.loadingProperty().set(true);
        listViewModel.statusTextProperty().set("Consultando eventos de auditoría...");
        auditoriaTable.setDisable(true);
        loadEventosCommand.execute();
    }

    private void applyListResult(ApiResult<PageResponse<AuditoriaEventoListItemDto>> result) {
        listViewModel.loadingProperty().set(false);
        auditoriaTable.setDisable(false);
        if (!result.isSuccess()) {
            listViewModel.items().clear();
            listViewModel.statusTextProperty().set(result.error()
                    .map(error -> ErrorMessages.userFacingMessage(error, "No se pudo cargar auditoría."))
                    .orElse("No se pudo cargar auditoría."));
            listViewModel.totalTextProperty().set("0 eventos");
            listViewModel.pageTextProperty().set("P\u00e1gina 1 de 1");
            listViewModel.previousPageAvailableProperty().set(false);
            listViewModel.nextPageAvailableProperty().set(false);
            return;
        }
        PageResponse<AuditoriaEventoListItemDto> page = result.data().orElseThrow();
        listViewModel.items().setAll(page.getItems());
        listViewModel.pageProperty().set(page.getPage());
        listViewModel.totalTextProperty().set(page.getTotalElements() + " eventos registrados");
        listViewModel.pageTextProperty().set("P\u00e1gina " + (page.getPage() + 1) + " de " + Math.max(page.getTotalPages(), 1));
        listViewModel.previousPageAvailableProperty().set(!page.isFirst());
        listViewModel.nextPageAvailableProperty().set(!page.isLast());
        listViewModel.statusTextProperty().set("Mostrando " + page.getNumberOfElements() + " de " + page.getTotalElements() + " eventos.");
    }

    private void showDetail(AuditoriaEventoListItemDto evento) {
        currentEvento = evento;
        reporteFormViewModel.visibleProperty().set(false);
        AuditoriaPresenter.AuditoriaEventDetailPresentation presentation = presenter.presentDetail(evento);
        detailViewModel.moduloProperty().set(presentation.modulo());
        detailViewModel.accionProperty().set(presentation.accion());
        detailViewModel.resultadoProperty().set(presentation.resultado());
        detailViewModel.entidadProperty().set(presentation.entidad());
        detailViewModel.entidadIdProperty().set(presentation.entidadId());
        detailViewModel.actorProperty().set(presentation.actor());
        detailViewModel.requestIdProperty().set(presentation.requestId());
        detailViewModel.ipOrigenProperty().set(presentation.ipOrigen());
        detailViewModel.fechaEventoProperty().set(presentation.fechaEvento());
        drawerTitleLabel.setText("Detalle del evento");
        drawerCoordinator.showOnly(detailPane);
        reporteFormViewModel.visibleProperty().set(false);
    }

    private Optional<String> validateReporteForm() {
        if (reporteFormatoCombo.getValue() == null) {
            return Optional.of("Selecciona un formato de salida.");
        }
        LocalDate fechaDesde = reporteFechaDesdePicker.getValue();
        LocalDate fechaHasta = reporteFechaHastaPicker.getValue();
        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            return Optional.of("La fecha desde no puede ser mayor que la fecha hasta.");
        }
        return Optional.empty();
    }

    private CrearAuditoriaReporteRequestDto buildReporteRequest() {
        return new CrearAuditoriaReporteRequestDto(
                reporteFormatoCombo.getValue(),
                reporteFechaDesdePicker.getValue(),
                reporteFechaHastaPicker.getValue(),
                emptyToNull(reporteModuloCombo.getEditor().getText()),
                emptyToNull(reporteAccionField.getText()),
                reporteResultadoCombo.getValue(),
                emptyToNull(reporteActorLoginField.getText()),
                incluirDetalleCheck.isSelected());
    }

    private void applyReporteResult(ApiResult<ReporteSolicitudCreatedResponseDto> result) {
        reporteFormViewModel.loadingProperty().set(false);
        if (!result.isSuccess()) {
            reporteFormViewModel.bannerMessageProperty().set(result.error()
                    .map(error -> ErrorMessages.userFacingMessage(error, "No se pudo solicitar el reporte de auditoría."))
                    .orElse("No se pudo solicitar el reporte de auditoría."));
            return;
        }
        ReporteSolicitudCreatedResponseDto created = result.data().orElseThrow();
        appContext.feedback().toastInfo("Solicitud de auditoría #" + created.solicitudId() + " creada. Puedes seguirla desde Reportes.");
        onCerrarDrawer();
        appContext.navigator().navigate(ViewId.REPORTES);
    }

    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void initializeCommands() {
        loadEventosCommand = UiCommands.io(() -> auditoriaService.listar(buildListQuery()), this::applyListResult);
        solicitarReporteCommand = UiCommands.io(() -> auditoriaService.solicitarReporte(buildReporteRequest()), this::applyReporteResult);
    }

    private AuditoriaEventosQuery buildListQuery() {
        return new AuditoriaEventosQuery(
                listViewModel.pageProperty().get(),
                PAGE_SIZE,
                emptyToNull(queryField.getText()),
                emptyToNull(moduloFilterCombo.getEditor().getText()),
                emptyToNull(accionFilterField.getText()),
                resultadoFilterCombo.getValue(),
                emptyToNull(actorLoginFilterField.getText()),
                fechaDesdeFilterPicker.getValue(),
                fechaHastaFilterPicker.getValue());
    }

    private Window currentWindow() {
        return drawerPane.getScene() == null ? null : drawerPane.getScene().getWindow();
    }
}
