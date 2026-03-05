package com.marcosmoreiradev.uensbackend.modules.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "El login es obligatorio.")
    @Size(max = 80, message = "El login no debe exceder 80 caracteres.")
    private String login;

    @NotBlank(message = "La contrasena es obligatoria.")
    @Size(max = 120, message = "La contrasena no debe exceder 120 caracteres.")
    private String password;
}

