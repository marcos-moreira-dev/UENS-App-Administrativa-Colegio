package com.marcosmoreiradev.uensdesktop.api.client;

import com.marcosmoreiradev.uensdesktop.api.contract.ApiErrorResponse;
import com.marcosmoreiradev.uensdesktop.common.error.ErrorCategory;
import com.marcosmoreiradev.uensdesktop.common.error.ErrorInfo;
import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpTimeoutException;

/**
 * Converts transport and HTTP failures into the desktop's structured error model.
 */
public final class ErrorMapper {

    /**
     * Maps an HTTP status code and optional backend error body to a categorized {@link ErrorInfo}.
     *
     * @param statusCode HTTP status returned by the backend
     * @param errorResponse parsed backend error envelope when available
     * @return normalized error information for UI and services
     */
    public ErrorInfo mapHttpError(int statusCode, ApiErrorResponse errorResponse) {
        ErrorCategory category = switch (statusCode) {
            case 400, 422 -> ErrorCategory.VALIDATION;
            case 401 -> ErrorCategory.AUTH;
            case 403 -> ErrorCategory.PERMISSION;
            case 409 -> ErrorCategory.CONFLICT;
            default -> statusCode >= 500 ? ErrorCategory.SERVER : ErrorCategory.UNKNOWN;
        };

        String message = errorResponse != null && errorResponse.getMessage() != null
                ? errorResponse.getMessage()
                : "Error HTTP " + statusCode;
        String errorCode = errorResponse != null ? errorResponse.getErrorCode() : null;
        String requestId = errorResponse != null ? errorResponse.getRequestId() : null;
        return new ErrorInfo(category, message, errorCode, requestId);
    }

    /**
     * Maps unexpected client-side exceptions such as timeouts or connectivity failures.
     *
     * @param ex unexpected exception raised while executing the HTTP request
     * @return normalized error information for UI and services
     */
    public ErrorInfo mapUnexpected(Exception ex) {
        if (ex instanceof HttpTimeoutException) {
            return new ErrorInfo(ErrorCategory.NETWORK, "Tiempo de espera agotado con el backend.", null, null);
        }
        if (ex instanceof ConnectException || ex instanceof IOException) {
            return new ErrorInfo(ErrorCategory.NETWORK, "No se pudo conectar con el backend.", null, null);
        }
        return new ErrorInfo(ErrorCategory.UNKNOWN, ex.getMessage(), null, null);
    }
}
