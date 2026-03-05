package com.marcosmoreiradev.uensdesktop.common.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public final class I18n {

    private static final String BUNDLE_BASE_NAME = "i18n.messages";
    private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("es");

    private I18n() {
    }

    public static ResourceBundle bundle() {
        return ResourceBundle.getBundle(BUNDLE_BASE_NAME, resolveLocale());
    }

    private static Locale resolveLocale() {
        String configured = System.getProperty("uens.locale");
        if (configured == null || configured.isBlank()) {
            configured = System.getenv("UENS_LOCALE");
        }
        if (configured == null || configured.isBlank()) {
            return DEFAULT_LOCALE;
        }
        Locale requested = Locale.forLanguageTag(configured.trim());
        String language = requested.getLanguage();
        if ("en".equals(language)) {
            return Locale.ENGLISH;
        }
        if ("es".equals(language)) {
            return DEFAULT_LOCALE;
        }
        return DEFAULT_LOCALE;
    }
}
