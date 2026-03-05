package com.marcosmoreiradev.uensdesktop.api.contract;

/**
 * Error envelope returned by the backend when an API call fails.
 */
public class ApiErrorResponse {

    private boolean ok;
    private String errorCode;
    private String message;
    private String path;
    private String timestamp;
    private String requestId;

    /**
     * @return backend flag included in the error envelope
     */
    public boolean isOk() {
        return ok;
    }

    /**
     * @param ok backend flag included in the error envelope
     */
    public void setOk(boolean ok) {
        this.ok = ok;
    }

    /**
     * @return backend-specific error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode backend-specific error code
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return human-readable error message returned by the backend
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message human-readable error message returned by the backend
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return backend path that originated the error
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path backend path that originated the error
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return backend timestamp associated with the error
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp backend timestamp associated with the error
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return backend request id useful for tracing the failure
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * @param requestId backend request id useful for tracing the failure
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
