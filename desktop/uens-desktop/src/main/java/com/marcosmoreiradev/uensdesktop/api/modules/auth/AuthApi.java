package com.marcosmoreiradev.uensdesktop.api.modules.auth;

import com.marcosmoreiradev.uensdesktop.api.client.ApiClient;
import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.modules.auth.dto.LoginRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.auth.dto.LoginResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.auth.dto.LogoutRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.auth.dto.MeResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.auth.dto.RefreshTokenRequestDto;

/**
 * Authentication endpoint wrapper used by the desktop login and session bootstrap flows.
 */
public final class AuthApi {

    private static final String LOGIN_PATH = "/api/v1/auth/login";
    private static final String LOGOUT_PATH = "/api/v1/auth/logout";
    private static final String ME_PATH = "/api/v1/auth/me";
    private static final String REFRESH_PATH = "/api/v1/auth/refresh";

    private final ApiClient apiClient;

    /**
     * Creates the authentication API wrapper around the shared transport client.
     *
     * @param apiClient shared low-level client used to perform HTTP requests
     */
    public AuthApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Requests a new authenticated session using user credentials.
     *
     * @param request login credentials entered by the operator
     * @return authentication result with token and user snapshot
     */
    public ApiResult<LoginResponseDto> login(LoginRequestDto request) {
        return apiClient.post(LOGIN_PATH, request, LoginResponseDto.class, false);
    }

    /**
     * Validates the current token and retrieves the effective backend user session.
     *
     * @return current authenticated user as seen by the backend
     */
    public ApiResult<MeResponseDto> me() {
        return apiClient.get(ME_PATH, MeResponseDto.class, true);
    }

    /**
     * Requests a rotated access/refresh token pair using the current refresh token.
     *
     * @param refreshToken refresh token currently associated with the desktop session
     * @return renewed token pair and user snapshot
     */
    public ApiResult<LoginResponseDto> refresh(String refreshToken) {
        return apiClient.post(REFRESH_PATH, new RefreshTokenRequestDto(refreshToken), LoginResponseDto.class, false);
    }

    /**
     * Notifies the backend that the current desktop session is being closed.
     *
     * @return success when the backend accepts the logout request
     */
    public ApiResult<Void> logout(String refreshToken) {
        return apiClient.postWithoutResponse(LOGOUT_PATH, new LogoutRequestDto(refreshToken), false, false);
    }
}
