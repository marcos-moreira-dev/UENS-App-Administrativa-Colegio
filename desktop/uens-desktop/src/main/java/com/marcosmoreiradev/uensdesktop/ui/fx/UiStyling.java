package com.marcosmoreiradev.uensdesktop.ui.fx;

import com.marcosmoreiradev.uensdesktop.common.constants.UiDefaults;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.TableView;
import javafx.util.Duration;

public final class UiStyling {

    private UiStyling() {
    }

    public static void addStyleClasses(Node node, String... styleClasses) {
        for (String styleClass : styleClasses) {
            if (!node.getStyleClass().contains(styleClass)) {
                node.getStyleClass().add(styleClass);
            }
        }
    }

    public static void bindVisibleWhenTextPresent(Labeled labeled) {
        labeled.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
            String text = labeled.getText();
            return text != null && !text.isBlank();
        }, labeled.textProperty()));
        labeled.managedProperty().bind(labeled.visibleProperty());
    }

    public static void installAutoHideBanner(StringProperty messageProperty) {
        installAutoHideBanner(messageProperty, Duration.millis(UiDefaults.BANNER_AUTO_HIDE_MS));
    }

    public static void installAutoHideBanner(StringProperty messageProperty, Duration duration) {
        PauseTransition autoHide = new PauseTransition(duration);
        messageProperty.addListener((observable, oldValue, newValue) -> {
            autoHide.stop();
            if (newValue == null || newValue.isBlank()) {
                return;
            }
            String snapshot = newValue;
            autoHide.setOnFinished(event -> {
                if (snapshot.equals(messageProperty.get())) {
                    messageProperty.set("");
                }
            });
            autoHide.playFromStart();
        });
    }

    public static <T> void configureTableToFillWidth(TableView<T> tableView) {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }
}
