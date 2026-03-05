package com.marcosmoreiradev.uensdesktop.ui.theme;

import java.io.InputStream;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.text.Font;

public final class TypographyManager {

    private static final double FONT_REGISTRATION_SIZE = 14.0;
    private static final String FONT_PROFILE_PROPERTY = "uens.font.profile";

    private static boolean initialized;
    private static TypographyProfile activeProfile = TypographyProfile.INTER;
    private static String activeUiFontFamily = TypographyProfile.INTER.uiCssFamily();
    private static String activeMonoFontFamily = TypographyProfile.INTER.monoCssFamily();

    private TypographyManager() {
    }

    public static void apply(Scene scene) {
        ensureInitialized();
        if (scene == null || scene.getRoot() == null) {
            return;
        }
        scene.getRoot().setStyle(mergeStyle(scene.getRoot().getStyle(),
                "-fx-font-family: \"" + activeUiFontFamily + "\"; -fx-font-style: normal;"));
    }

    public static void applyMono(Node... nodes) {
        ensureInitialized();
        if (nodes == null) {
            return;
        }
        String monoStyle = "-fx-font-family: \"" + activeMonoFontFamily + "\"; -fx-font-style: normal;";
        for (Node node : nodes) {
            if (node != null) {
                node.setStyle(mergeStyle(node.getStyle(), monoStyle));
            }
        }
    }

    public static TypographyProfile activeProfile() {
        ensureInitialized();
        return activeProfile;
    }

    public static String activeUiFontFamily() {
        ensureInitialized();
        return activeUiFontFamily;
    }

    public static String activeMonoFontFamily() {
        ensureInitialized();
        return activeMonoFontFamily;
    }

    private static synchronized void ensureInitialized() {
        if (initialized) {
            return;
        }
        activeProfile = TypographyProfile.fromSystemProperty(System.getProperty(FONT_PROFILE_PROPERTY));
        activeUiFontFamily = loadFonts(activeProfile.uiResourcePaths(), activeProfile.uiCssFamily());
        activeMonoFontFamily = loadFonts(activeProfile.monoResourcePaths(), activeProfile.monoCssFamily());
        initialized = true;
    }

    private static String loadFonts(Iterable<String> resourcePaths, String fallbackFamily) {
        String resolvedFamily = null;
        for (String resourcePath : resourcePaths) {
            try (InputStream inputStream = TypographyManager.class.getResourceAsStream(resourcePath)) {
                if (inputStream != null) {
                    Font loadedFont = Font.loadFont(inputStream, FONT_REGISTRATION_SIZE);
                    if (loadedFont != null && resolvedFamily == null
                            && loadedFont.getFamily() != null && !loadedFont.getFamily().isBlank()) {
                        resolvedFamily = loadedFont.getFamily();
                    }
                }
            } catch (Exception ignored) {
                // If a custom font fails, JavaFX will fall back to system fonts.
            }
        }
        return resolvedFamily == null || resolvedFamily.isBlank() ? fallbackFamily : resolvedFamily;
    }

    private static String mergeStyle(String currentStyle, String newRule) {
        if (currentStyle == null || currentStyle.isBlank()) {
            return newRule;
        }
        if (currentStyle.contains(newRule)) {
            return currentStyle;
        }
        return currentStyle.stripTrailing() + " " + newRule;
    }
}
