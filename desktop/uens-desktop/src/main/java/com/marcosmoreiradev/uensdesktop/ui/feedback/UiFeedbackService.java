package com.marcosmoreiradev.uensdesktop.ui.feedback;

import com.marcosmoreiradev.uensdesktop.ui.assets.FxUiAssets;
import com.marcosmoreiradev.uensdesktop.ui.assets.UiAssetId;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;

public final class UiFeedbackService {

    private final UiNotificationsState notificationsState;

    public UiFeedbackService(UiNotificationsState notificationsState) {
        this.notificationsState = notificationsState;
    }

    public void toastInfo(String message) {
        notificationsState.pushInfo(message);
    }

    public void toastSuccess(String message) {
        notificationsState.pushSuccess(message);
    }

    public void toastWarning(String message) {
        notificationsState.pushWarning(message);
    }

    public void toastError(String message) {
        notificationsState.pushError(message);
    }

    public boolean confirm(Window owner, String title, String header, String content, String confirmLabel, String cancelLabel) {
        ButtonType confirmButton = new ButtonType(confirmLabel, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType(cancelLabel, ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = buildAlert(owner, Alert.AlertType.CONFIRMATION, title, header, content, "session-exit-message-box", true);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);
        return alert.showAndWait().filter(confirmButton::equals).isPresent();
    }

    public Optional<ButtonType> showInformation(Window owner, String title, String header, String content) {
        Alert alert = buildAlert(owner, Alert.AlertType.INFORMATION, title, header, content, "info-message-box", true);
        alert.getButtonTypes().setAll(ButtonType.OK);
        return alert.showAndWait();
    }

    public Optional<ButtonType> showError(Window owner, String title, String header, String content) {
        Alert alert = buildAlert(owner, Alert.AlertType.ERROR, title, header, content, "info-message-box", false);
        alert.getButtonTypes().setAll(ButtonType.OK);
        return alert.showAndWait();
    }

    private Alert buildAlert(
            Window owner,
            Alert.AlertType alertType,
            String title,
            String header,
            String content,
            String styleClass,
            boolean showInfoIcon) {
        Alert alert = new Alert(alertType);
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.getDialogPane().getStyleClass().add(styleClass);
        if (showInfoIcon) {
            FxUiAssets.image(UiAssetId.INFORMATION_ICON).ifPresent(image -> {
                ImageView iconView = new ImageView(image);
                iconView.setFitWidth(52);
                iconView.setFitHeight(52);
                iconView.setPreserveRatio(true);
                alert.setGraphic(iconView);
            });
        }
        alert.setOnShown(event -> FxUiAssets.image(UiAssetId.INFORMATION_ICON).ifPresent(image -> {
            if (alert.getDialogPane().getScene().getWindow() instanceof Stage stage) {
                stage.getIcons().add(image);
            }
        }));
        return alert;
    }
}
