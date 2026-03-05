package com.marcosmoreiradev.uensdesktop.api.modules.auth.dto;

import com.marcosmoreiradev.uensdesktop.session.Role;

/**
 * Session bootstrap payload returned by {@code /auth/me}.
 *
 * @param id backend identifier of the authenticated user
 * @param login login name recognized by the backend
 * @param rol effective role used for authorization
 * @param estado backend state of the administrative user
 */
public record MeResponseDto(Long id, String login, Role rol, String estado) {
}
