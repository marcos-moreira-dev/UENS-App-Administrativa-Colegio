package com.marcosmoreiradev.uensbackend.modules.auth.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthUsuarioResumenDto {

    private Long id;
    private String login;
    private String rol;
    private String estado;
}
