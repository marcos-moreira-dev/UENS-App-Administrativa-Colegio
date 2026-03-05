package com.marcosmoreiradev.uensdesktop.common.error;

/**
 * Builds user-facing error copy from the structured error information returned by the client.
 *
 * <p>The helper keeps controllers focused on flow orchestration while it decides when the backend
 * message is trustworthy and when extra diagnostics such as {@code requestId} should remain visible.
 */
public final class ErrorMessages {

    private ErrorMessages() {
    }

    /**
     * Produces the message that should be shown in banners, dialogs or toasts.
     *
     * @param errorInfo categorized error information returned by the API client
     * @param fallbackMessage safe message to use when the backend response is missing or blank
     * @return the most useful message for the operator, optionally enriched with a request id
     */
    public static String userFacingMessage(ErrorInfo errorInfo, String fallbackMessage) {
        if (errorInfo == null) {
            return fallbackMessage;
        }

        String message = errorInfo.message() == null || errorInfo.message().isBlank()
                ? fallbackMessage
                : errorInfo.message();

        if (shouldAppendRequestId(errorInfo) && errorInfo.requestId() != null && !errorInfo.requestId().isBlank()) {
            return message + " RequestId: " + errorInfo.requestId();
        }

        return message;
    }

    /**
     * Decides whether the request id should remain visible because the failure may require support
     * or backend traceability.
     *
     * @param errorInfo categorized error returned by the client layer
     * @return {@code true} when the request id adds operational value to the user
     */
    private static boolean shouldAppendRequestId(ErrorInfo errorInfo) {
        return errorInfo.category() == ErrorCategory.SERVER
                || errorInfo.category() == ErrorCategory.UNKNOWN
                || errorInfo.category() == ErrorCategory.CONFLICT;
    }
}
