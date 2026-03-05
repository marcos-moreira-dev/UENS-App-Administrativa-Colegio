package com.marcosmoreiradev.uensdesktop.ui.theme;

import java.util.List;

public enum TypographyProfile {
    INTER(
            "Inter",
            List.of(
                    "/assets/fonts/Inter/static/Inter_18pt-Regular.ttf",
                    "/assets/fonts/Inter/static/Inter_18pt-Medium.ttf",
                    "/assets/fonts/Inter/static/Inter_18pt-SemiBold.ttf"),
            "JetBrains Mono",
            List.of(
                    "/assets/fonts/JetBrains_Mono/static/JetBrainsMono-Regular.ttf",
                    "/assets/fonts/JetBrains_Mono/static/JetBrainsMono-Medium.ttf",
                    "/assets/fonts/JetBrains_Mono/static/JetBrainsMono-SemiBold.ttf")),
    SOURCE_SANS_3(
            "Source Sans 3",
            List.of(
                    "/assets/fonts/Source_Sans_3/static/SourceSans3-Regular.ttf",
                    "/assets/fonts/Source_Sans_3/static/SourceSans3-Medium.ttf",
                    "/assets/fonts/Source_Sans_3/static/SourceSans3-SemiBold.ttf"),
            "JetBrains Mono",
            List.of(
                    "/assets/fonts/JetBrains_Mono/static/JetBrainsMono-Regular.ttf",
                    "/assets/fonts/JetBrains_Mono/static/JetBrainsMono-Medium.ttf",
                    "/assets/fonts/JetBrains_Mono/static/JetBrainsMono-SemiBold.ttf")),
    IBM_PLEX_SANS(
            "IBM Plex Sans",
            List.of(
                    "/assets/fonts/IBM_Plex_Sans/static/IBMPlexSans-Regular.ttf",
                    "/assets/fonts/IBM_Plex_Sans/static/IBMPlexSans-Medium.ttf",
                    "/assets/fonts/IBM_Plex_Sans/static/IBMPlexSans-SemiBold.ttf"),
            "JetBrains Mono",
            List.of(
                    "/assets/fonts/JetBrains_Mono/static/JetBrainsMono-Regular.ttf",
                    "/assets/fonts/JetBrains_Mono/static/JetBrainsMono-Medium.ttf",
                    "/assets/fonts/JetBrains_Mono/static/JetBrainsMono-SemiBold.ttf"));

    private final String uiCssFamily;
    private final List<String> uiResourcePaths;
    private final String monoCssFamily;
    private final List<String> monoResourcePaths;

    TypographyProfile(String uiCssFamily, List<String> uiResourcePaths, String monoCssFamily, List<String> monoResourcePaths) {
        this.uiCssFamily = uiCssFamily;
        this.uiResourcePaths = uiResourcePaths;
        this.monoCssFamily = monoCssFamily;
        this.monoResourcePaths = monoResourcePaths;
    }

    public String uiCssFamily() {
        return uiCssFamily;
    }

    public List<String> uiResourcePaths() {
        return uiResourcePaths;
    }

    public String monoCssFamily() {
        return monoCssFamily;
    }

    public List<String> monoResourcePaths() {
        return monoResourcePaths;
    }

    public static TypographyProfile fromSystemProperty(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return INTER;
        }
        try {
            return TypographyProfile.valueOf(rawValue.trim().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return INTER;
        }
    }
}
