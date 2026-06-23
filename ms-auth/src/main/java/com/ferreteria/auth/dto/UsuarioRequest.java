package com.ferreteria.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioRequest(
    @NotBlank String nombre,
    @Email @NotBlank String correo,
    @NotBlank String clave,
    @NotBlank String rol,
    String foto
) {}
