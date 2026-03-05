package com.marcosmoreiradev.uensbackend.common.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {

    private boolean ok;
    private String errorCode;
    private String message;
    private Object details;
    private String path;
    private Instant timestamp;
    private String requestId;

    public static ApiErrorResponse of(String errorCode, String message, Object details, String path, String requestId) {
        return new ApiErrorResponse(false, errorCode, message, details, path, Instant.now(), requestId);
    }
}
