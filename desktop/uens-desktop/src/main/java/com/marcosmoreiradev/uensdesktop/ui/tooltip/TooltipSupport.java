package com.marcosmoreiradev.uensdesktop.ui.tooltip;

import com.marcosmoreiradev.uensdesktop.common.constants.UiDefaults;
import com.marcosmoreiradev.uensdesktop.ui.fx.FxNodeTree;
import java.util.Optional;
import java.util.Set;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public final class TooltipSupport {

    private static final Set<String> PRIMARY_STYLE_CLASSES = Set.of(
            "primary-button",
            "sidebar-button",
            "sidebar-login-button",
            "sidebar-logout-button",
            "screen-title",
            "drawer-title",
            "section-heading",
            "field-label",
            "sidebar-title",
            "sidebar-subtitle",
            "no-auto-tooltip");

    private TooltipSupport() {
    }

    public static void installSecondaryTooltips(Parent root) {
        FxNodeTree.visit(root, node -> {
            if (node instanceof Control control) {
                installIfSecondary(control);
            }
        });
    }

    public static void install(Control control, String text) {
        if (control == null || text == null || text.isBlank()) {
            return;
        }
        Tooltip tooltip = new Tooltip(text.trim());
        tooltip.setShowDelay(Duration.millis(UiDefaults.TOOLTIP_SHOW_DELAY_MS));
        control.setTooltip(tooltip);
    }

    public static void refresh(Control... controls) {
        if (controls == null) {
            return;
        }
        for (Control control : controls) {
            if (control != null) {
                installIfSecondary(control);
            }
        }
    }

    private static void installIfSecondary(Control control) {
        if (control.getTooltip() != null || isPrimaryControl(control)) {
            return;
        }
        inferText(control).ifPresent(text -> install(control, text));
    }

    private static boolean isPrimaryControl(Control control) {
        return control.getStyleClass().stream().anyMatch(PRIMARY_STYLE_CLASSES::contains);
    }

    private static Optional<String> inferText(Control control) {
        if (control instanceof Label) {
            return Optional.empty();
        }
        Object configuredText = control.getProperties().get("tooltip.text");
        if (configuredText instanceof String text && !text.isBlank()) {
            return Optional.of(text.trim());
        }
        if (control instanceof ButtonBase buttonBase) {
            return TooltipCatalog.forButtonText(buttonBase.getText()).or(() ->
                    Optional.ofNullable(buttonBase.getText()).filter(text -> !text.isBlank()));
        }
        if (control instanceof TextInputControl textInputControl) {
            return TooltipCatalog.forPrompt(textInputControl.getPromptText());
        }
        if (control instanceof ComboBoxBase<?> comboBoxBase) {
            return TooltipCatalog.forPrompt(comboBoxBase.getPromptText()).or(() -> Optional.of("Selecciona una opción."));
        }
        if (control instanceof CheckBox checkBox) {
            return Optional.ofNullable(checkBox.getText()).filter(text -> !text.isBlank());
        }
        if (control instanceof TableView<?>) {
            return Optional.of("Tabla interactiva de datos.");
        }
        return Optional.empty();
    }
}
