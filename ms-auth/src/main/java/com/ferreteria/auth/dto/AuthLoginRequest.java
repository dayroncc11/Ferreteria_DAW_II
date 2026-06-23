package com.ferreteria.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequest(
    @Email @NotBlank String correo,
    @NotBlank String contrasena
) {}
