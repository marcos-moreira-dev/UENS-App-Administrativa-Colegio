package com.marcosmoreiradev.uensdesktop.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class AppPropertiesLoader {

    private AppPropertiesLoader() {
    }

    public static AppProperties load() {
        Properties properties = new Properties();

        try (InputStream input = AppPropertiesLoader.class.getResourceAsStream("/app.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo leer app.properties", ex);
        }

        String envBaseUrl = System.getenv("UENS_API_BASE_URL");
        String baseUrl = envBaseUrl != null && !envBaseUrl.isBlank()
                ? envBaseUrl
                : properties.getProperty("uens.api.baseUrl", "http://localhost:8080");
        int timeoutSeconds = Integer.parseInt(properties.getProperty("uens.api.timeoutSeconds", "15"));

        return new AppProperties(baseUrl, timeoutSeconds);
    }
}
