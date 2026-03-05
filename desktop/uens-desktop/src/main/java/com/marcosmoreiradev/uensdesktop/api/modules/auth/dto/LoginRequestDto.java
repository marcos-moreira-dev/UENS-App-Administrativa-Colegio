package com.marcosmoreiradev.uensdesktop.api.modules.auth.dto;

/**
 * Credentials submitted by the desktop login form.
 *
 * @param login username or login identifier entered by the operator
 * @param password plaintext password sent to the authentication endpoint
 */
public record LoginRequestDto(String login, String password) {
}
