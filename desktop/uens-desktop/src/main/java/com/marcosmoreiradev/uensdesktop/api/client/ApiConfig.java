package com.marcosmoreiradev.uensdesktop.api.client;

/**
 * Immutable transport configuration shared by the desktop API client.
 *
 * @param baseUrl root URL of the backend API
 * @param timeoutSeconds request timeout applied to HTTP calls
 */
public record ApiConfig(String baseUrl, int timeoutSeconds) {
}
