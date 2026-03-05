package com.marcosmoreiradev.uensdesktop.common.error;

public record ErrorInfo(
        ErrorCategory category,
        String message,
        String errorCode,
        String requestId) {
}
