package com.marcosmoreiradev.uensdesktop.api.modules.docentes.dto;

/**
 * Minimal payload used to patch the operational state of a teacher.
 */
public record DocentePatchEstadoRequestDto(String estado) {
}
