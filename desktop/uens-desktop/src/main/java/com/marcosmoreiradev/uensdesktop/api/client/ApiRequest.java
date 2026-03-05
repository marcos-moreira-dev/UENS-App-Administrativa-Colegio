package com.marcosmoreiradev.uensdesktop.api.client;

import java.util.Map;

/**
 * Simple description of an HTTP request before it is translated to {@link java.net.http.HttpRequest}.
 *
 * @param method HTTP method such as {@code GET} or {@code POST}
 * @param path backend-relative path including query string when needed
 * @param queryParams normalized query parameters associated with the request
 */
public record ApiRequest(String method, String path, Map<String, String> queryParams) {
}
