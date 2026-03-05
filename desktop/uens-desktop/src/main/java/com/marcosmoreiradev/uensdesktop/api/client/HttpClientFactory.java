package com.marcosmoreiradev.uensdesktop.api.client;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Factory for the shared JDK {@link HttpClient} used by the desktop.
 */
public final class HttpClientFactory {

    private HttpClientFactory() {
    }

    /**
     * Builds an {@link HttpClient} configured with the timeout required by the desktop runtime.
     *
     * @param config effective backend transport configuration
     * @return configured HTTP client instance
     */
    public static HttpClient create(ApiConfig config) {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(config.timeoutSeconds()))
                .build();
    }
}
