package com.marcosmoreiradev.uensdesktop.api.modules.auth.dto;

/**
 * Payload used to revoke the refresh token during logout.
 *
 * @param refreshToken current refresh token to revoke on the backend
 */
public record LogoutRequestDto(String refreshToken) {
}
