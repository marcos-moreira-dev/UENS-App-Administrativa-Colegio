package com.marcosmoreiradev.uensdesktop.api.modules.auth.dto;

/**
 * Payload sent by the desktop when it needs to rotate the current token pair.
 *
 * @param refreshToken current refresh token stored in session state
 */
public record RefreshTokenRequestDto(String refreshToken) {
}
