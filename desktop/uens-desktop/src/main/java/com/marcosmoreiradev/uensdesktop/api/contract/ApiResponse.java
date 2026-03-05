package com.marcosmoreiradev.uensdesktop.api.contract;

/**
 * Generic success envelope returned by the backend.
 *
 * @param <T> payload type contained in the {@code data} field
 */
public class ApiResponse<T> {

    private boolean ok;
    private String message;
    private T data;
    private String timestamp;

    /**
     * @return backend flag that indicates whether the operation completed successfully
     */
    public boolean isOk() {
        return ok;
    }

    /**
     * @param ok backend success flag
     */
    public void setOk(boolean ok) {
        this.ok = ok;
    }

    /**
     * @return human-readable backend message associated with the response
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message human-readable backend message associated with the response
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return typed payload returned by the backend
     */
    public T getData() {
        return data;
    }

    /**
     * @param data typed payload returned by the backend
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * @return backend timestamp associated with the response
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp backend timestamp associated with the response
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
