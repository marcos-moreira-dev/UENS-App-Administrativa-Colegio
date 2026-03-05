package com.marcosmoreiradev.uensdesktop.api.client;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcosmoreiradev.uensdesktop.api.contract.ApiErrorResponse;
import com.marcosmoreiradev.uensdesktop.api.contract.ApiResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.auth.dto.LoginResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.auth.dto.RefreshTokenRequestDto;
import com.marcosmoreiradev.uensdesktop.session.SessionState;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Low-level HTTP client used by the desktop to talk to the backend REST API.
 *
 * <p>The class is responsible for building authenticated requests, deserializing the backend
 * envelope format, mapping transport failures and clearing the session when the backend returns
 * {@code 401 Unauthorized}.
 */
public final class ApiClient {

    private static final Pattern FILENAME_STAR_PATTERN = Pattern.compile("filename\\*=UTF-8''([^;]+)");
    private static final Pattern FILENAME_PATTERN = Pattern.compile("filename=\"?([^\";]+)\"?");
    private static final String REFRESH_PATH = "/api/v1/auth/refresh";
    private static final Duration ACCESS_TOKEN_REFRESH_THRESHOLD = Duration.ofSeconds(60);

    private final ApiConfig config;
    private final SessionState sessionState;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ErrorMapper errorMapper;
    private final Object tokenRefreshLock = new Object();

    /**
     * Creates the singleton API client used across the desktop runtime.
     *
     * @param config effective backend base URL and timeout configuration
     * @param sessionState shared session used to attach bearer tokens and react to unauthorized
     *     responses
     */
    public ApiClient(ApiConfig config, SessionState sessionState) {
        this.config = config;
        this.sessionState = sessionState;
        this.httpClient = HttpClientFactory.create(config);
        this.objectMapper = JsonMapperFactory.create();
        this.errorMapper = new ErrorMapper();
    }

    /**
     * Sends an HTTP {@code GET} request that expects a regular {@link ApiResponse} payload.
     *
     * @param path backend path relative to the configured base URL
     * @param dataClass expected payload type inside the response envelope
     * @param authenticated whether the request should include the current bearer token
     * @param <T> payload type returned by the endpoint
     * @return successful result with the deserialized payload or a mapped failure
     */
    public <T> ApiResult<T> get(String path, Class<T> dataClass, boolean authenticated) {
        HttpRequest.Builder builder = baseRequest(path, authenticated).GET();
        return send(builder.build(), dataClass);
    }

    /**
     * Sends an HTTP {@code GET} request that expects a paginated {@link ApiResponse}.
     *
     * @param path backend path relative to the configured base URL
     * @param itemClass item type contained in the paginated response
     * @param authenticated whether the request should include the current bearer token
     * @param <T> item type returned inside the page
     * @return successful result with the deserialized page or a mapped failure
     */
    public <T> ApiResult<com.marcosmoreiradev.uensdesktop.api.contract.PageResponse<T>> getPage(
            String path,
            Class<T> itemClass,
            boolean authenticated) {
        HttpRequest.Builder builder = baseRequest(path, authenticated).GET();
        return sendPage(builder.build(), itemClass);
    }

    /**
     * Sends an HTTP {@code POST} request with a JSON body.
     *
     * @param path backend path relative to the configured base URL
     * @param requestBody request DTO serialized as JSON
     * @param dataClass expected payload type inside the response envelope
     * @param authenticated whether the request should include the current bearer token
     * @param <T> payload type returned by the endpoint
     * @return successful result with the deserialized payload or a mapped failure
     */
    public <T> ApiResult<T> post(String path, Object requestBody, Class<T> dataClass, boolean authenticated) {
        return sendWithBody("POST", path, requestBody, dataClass, authenticated);
    }

    /**
     * Sends an HTTP {@code POST} request with no body while still expecting a regular payload.
     *
     * @param path backend path relative to the configured base URL
     * @param dataClass expected payload type inside the response envelope
     * @param authenticated whether the request should include the current bearer token
     * @param <T> payload type returned by the endpoint
     * @return successful result with the deserialized payload or a mapped failure
     */
    public <T> ApiResult<T> postWithoutBody(String path, Class<T> dataClass, boolean authenticated) {
        HttpRequest request = baseRequest(path, authenticated)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return send(request, dataClass);
    }

    /**
     * Sends an HTTP {@code POST} request that only needs to report success or failure.
     *
     * @param path backend path relative to the configured base URL
     * @param authenticated whether the request should include the current bearer token
     * @param logoutOnUnauthorized whether a {@code 401} should clear the current session
     * @return success with {@code null} payload or a mapped failure
     */
    public ApiResult<Void> postWithoutResponse(String path, boolean authenticated, boolean logoutOnUnauthorized) {
        HttpRequest request = baseRequest(path, authenticated)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return sendWithoutResponse(request, logoutOnUnauthorized);
    }

    /**
     * Sends an HTTP {@code POST} request with a JSON body when the caller only needs
     * a success/failure outcome.
     *
     * @param path backend path relative to the configured base URL
     * @param requestBody request DTO serialized as JSON
     * @param authenticated whether the request should include the current bearer token
     * @param logoutOnUnauthorized whether a {@code 401} should clear the current session
     * @return success with {@code null} payload or a mapped failure
     */
    public ApiResult<Void> postWithoutResponse(
            String path,
            Object requestBody,
            boolean authenticated,
            boolean logoutOnUnauthorized
    ) {
        try {
            String json = objectMapper.writeValueAsString(requestBody);
            HttpRequest request = baseRequest(path, authenticated)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();
            return sendWithoutResponse(request, logoutOnUnauthorized);
        } catch (IOException ex) {
            return ApiResult.failure(errorMapper.mapUnexpected(ex));
        }
    }

    /**
     * Sends an HTTP {@code PUT} request with a JSON body.
     *
     * @param path backend path relative to the configured base URL
     * @param requestBody request DTO serialized as JSON
     * @param dataClass expected payload type inside the response envelope
     * @param authenticated whether the request should include the current bearer token
     * @param <T> payload type returned by the endpoint
     * @return successful result with the deserialized payload or a mapped failure
     */
    public <T> ApiResult<T> put(String path, Object requestBody, Class<T> dataClass, boolean authenticated) {
        return sendWithBody("PUT", path, requestBody, dataClass, authenticated);
    }

    /**
     * Sends an HTTP {@code PATCH} request with a JSON body.
     *
     * @param path backend path relative to the configured base URL
     * @param requestBody request DTO serialized as JSON
     * @param dataClass expected payload type inside the response envelope
     * @param authenticated whether the request should include the current bearer token
     * @param <T> payload type returned by the endpoint
     * @return successful result with the deserialized payload or a mapped failure
     */
    public <T> ApiResult<T> patch(String path, Object requestBody, Class<T> dataClass, boolean authenticated) {
        return sendWithBody("PATCH", path, requestBody, dataClass, authenticated);
    }

    /**
     * Downloads binary content such as generated reports.
     *
     * @param path backend path relative to the configured base URL
     * @param authenticated whether the request should include the current bearer token
     * @return successful result with the raw bytes and inferred metadata, or a mapped failure
     */
    public ApiResult<BinaryPayload> getBinary(String path, boolean authenticated) {
        HttpRequest request = baseRequest(path, authenticated)
                .setHeader("Accept", "*/*")
                .GET()
                .build();
        try {
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                String fileName = extractFileName(response);
                String contentType = response.headers()
                        .firstValue("Content-Type")
                        .orElse("application/octet-stream");
                return ApiResult.success(new BinaryPayload(response.body(), fileName, contentType));
            }

            if (response.statusCode() == 401) {
                sessionState.logout();
            }

            String body = new String(response.body(), StandardCharsets.UTF_8);
            ApiErrorResponse errorResponse = tryReadError(body);
            return ApiResult.failure(errorMapper.mapHttpError(response.statusCode(), errorResponse));
        } catch (Exception ex) {
            return ApiResult.failure(errorMapper.mapUnexpected(ex));
        }
    }

    private <T> ApiResult<T> sendWithBody(
            String method,
            String path,
            Object requestBody,
            Class<T> dataClass,
            boolean authenticated) {
        try {
            String json = objectMapper.writeValueAsString(requestBody);
            HttpRequest request = baseRequest(path, authenticated)
                    .header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();
            return send(request, dataClass);
        } catch (IOException ex) {
            return ApiResult.failure(errorMapper.mapUnexpected(ex));
        }
    }

    private <T> ApiResult<T> send(HttpRequest request, Class<T> dataClass) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JavaType type = objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, dataClass);
                ApiResponse<T> apiResponse = objectMapper.readValue(response.body(), type);
                return ApiResult.success(apiResponse.getData());
            }

            if (response.statusCode() == 401) {
                sessionState.logout();
            }

            ApiErrorResponse errorResponse = tryReadError(response.body());
            return ApiResult.failure(errorMapper.mapHttpError(response.statusCode(), errorResponse));
        } catch (Exception ex) {
            return ApiResult.failure(errorMapper.mapUnexpected(ex));
        }
    }

    private ApiResult<Void> sendWithoutResponse(HttpRequest request, boolean logoutOnUnauthorized) {
        try {
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return ApiResult.success(null);
            }

            if (response.statusCode() == 401 && logoutOnUnauthorized) {
                sessionState.logout();
            }

            ApiErrorResponse errorResponse = tryReadError(response.body());
            return ApiResult.failure(errorMapper.mapHttpError(response.statusCode(), errorResponse));
        } catch (Exception ex) {
            return ApiResult.failure(errorMapper.mapUnexpected(ex));
        }
    }

    private <T> ApiResult<com.marcosmoreiradev.uensdesktop.api.contract.PageResponse<T>> sendPage(
            HttpRequest request,
            Class<T> itemClass) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JavaType pageType = objectMapper.getTypeFactory()
                        .constructParametricType(com.marcosmoreiradev.uensdesktop.api.contract.PageResponse.class, itemClass);
                JavaType apiType = objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, pageType);
                ApiResponse<com.marcosmoreiradev.uensdesktop.api.contract.PageResponse<T>> apiResponse =
                        objectMapper.readValue(response.body(), apiType);
                return ApiResult.success(apiResponse.getData());
            }

            if (response.statusCode() == 401) {
                sessionState.logout();
            }

            ApiErrorResponse errorResponse = tryReadError(response.body());
            return ApiResult.failure(errorMapper.mapHttpError(response.statusCode(), errorResponse));
        } catch (Exception ex) {
            return ApiResult.failure(errorMapper.mapUnexpected(ex));
        }
    }

    private ApiErrorResponse tryReadError(String body) {
        try {
            return objectMapper.readValue(body, ApiErrorResponse.class);
        } catch (Exception ex) {
            return null;
        }
    }

    private HttpRequest.Builder baseRequest(String path, boolean authenticated) {
        if (authenticated) {
            ensureFreshAccessToken();
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(config.baseUrl() + path))
                .timeout(Duration.ofSeconds(config.timeoutSeconds()))
                .header("Accept", "application/json");

        if (authenticated) {
            sessionState.token().ifPresent(token -> builder.header("Authorization", "Bearer " + token));
        }

        return builder;
    }

    private void ensureFreshAccessToken() {
        if (sessionState.token().isEmpty()) {
            return;
        }
        if (!sessionState.isAccessTokenExpiringWithin(ACCESS_TOKEN_REFRESH_THRESHOLD)) {
            return;
        }

        synchronized (tokenRefreshLock) {
            if (!sessionState.isAccessTokenExpiringWithin(ACCESS_TOKEN_REFRESH_THRESHOLD)) {
                return;
            }
            attemptTokenRefresh();
        }
    }

    private void attemptTokenRefresh() {
        String refreshToken = sessionState.refreshToken().orElse(null);
        if (refreshToken == null || refreshToken.isBlank() || !sessionState.hasUsableRefreshToken()) {
            if (sessionState.accessTokenExpiresAt().map(exp -> !exp.isAfter(Instant.now())).orElse(false)) {
                sessionState.logout();
            }
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(new RefreshTokenRequestDto(refreshToken));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.baseUrl() + REFRESH_PATH))
                    .timeout(Duration.ofSeconds(config.timeoutSeconds()))
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JavaType type = objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, LoginResponseDto.class);
                ApiResponse<LoginResponseDto> apiResponse = objectMapper.readValue(response.body(), type);
                LoginResponseDto refreshed = apiResponse.getData();
                if (refreshed != null) {
                    sessionState.login(
                            refreshed.accessToken(),
                            Instant.now().plusSeconds(refreshed.expiresInSeconds()),
                            refreshed.refreshToken(),
                            Instant.now().plusSeconds(refreshed.refreshExpiresInSeconds()),
                            refreshed.usuario()
                    );
                }
                return;
            }

            if (response.statusCode() == 401 || response.statusCode() == 403) {
                sessionState.logout();
            }
        } catch (Exception ex) {
            if (sessionState.accessTokenExpiresAt().map(exp -> !exp.isAfter(Instant.now())).orElse(false)) {
                sessionState.logout();
            }
        }
    }

    /**
     * Extracts the server-provided file name from {@code Content-Disposition} when present.
     *
     * @param response binary HTTP response returned by the backend
     * @return resolved file name or {@code null} when the header does not expose one
     */
    private String extractFileName(HttpResponse<byte[]> response) {
        Optional<String> contentDisposition = response.headers().firstValue("Content-Disposition");
        if (contentDisposition.isEmpty()) {
            return null;
        }
        String header = contentDisposition.get();

        Matcher utf8Matcher = FILENAME_STAR_PATTERN.matcher(header);
        if (utf8Matcher.find()) {
            return URLDecoder.decode(utf8Matcher.group(1), StandardCharsets.UTF_8);
        }

        Matcher plainMatcher = FILENAME_PATTERN.matcher(header);
        if (plainMatcher.find()) {
            return plainMatcher.group(1);
        }

        return null;
    }
}
