package com.marcosmoreiradev.uensdesktop.modules.auth.application;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.modules.auth.AuthApi;
import com.marcosmoreiradev.uensdesktop.api.modules.auth.dto.LoginRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.auth.dto.LoginResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.auth.dto.MeResponseDto;

public final class AuthService {

    private final AuthApi authApi;

    public AuthService(AuthApi authApi) {
        this.authApi = authApi;
    }

    public ApiResult<LoginResponseDto> login(String login, String password) {
        return authApi.login(new LoginRequestDto(login, password));
    }

    public ApiResult<MeResponseDto> me() {
        return authApi.me();
    }

    public ApiResult<LoginResponseDto> refresh(String refreshToken) {
        return authApi.refresh(refreshToken);
    }

    public ApiResult<Void> logout(String refreshToken) {
        return authApi.logout(refreshToken);
    }

    public AuthApi authApi() {
        return authApi;
    }
}
