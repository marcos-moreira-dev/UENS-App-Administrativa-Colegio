package com.marcosmoreiradev.uensdesktop.api.client;

import com.marcosmoreiradev.uensdesktop.common.error.ErrorInfo;
import java.util.Optional;

/**
 * Uniform result wrapper used by the desktop to represent successful or failed API calls.
 *
 * @param <T> payload type returned by the operation when it succeeds
 */
public final class ApiResult<T> {

    private final T data;
    private final ErrorInfo error;

    private ApiResult(T data, ErrorInfo error) {
        this.data = data;
        this.error = error;
    }

    /**
     * Creates a successful result with the provided payload.
     *
     * @param data payload returned by the operation
     * @param <T> payload type
     * @return successful result instance
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(data, null);
    }

    /**
     * Creates a failed result with structured error information.
     *
     * @param error categorized error information
     * @param <T> expected payload type for the failed operation
     * @return failed result instance
     */
    public static <T> ApiResult<T> failure(ErrorInfo error) {
        return new ApiResult<>(null, error);
    }

    /**
     * Indicates whether the operation completed without mapped errors.
     *
     * @return {@code true} when the result contains data instead of an error
     */
    public boolean isSuccess() {
        return error == null;
    }

    /**
     * Returns the payload when the operation succeeded.
     *
     * @return optional payload
     */
    public Optional<T> data() {
        return Optional.ofNullable(data);
    }

    /**
     * Returns the structured error when the operation failed.
     *
     * @return optional error information
     */
    public Optional<ErrorInfo> error() {
        return Optional.ofNullable(error);
    }
}
