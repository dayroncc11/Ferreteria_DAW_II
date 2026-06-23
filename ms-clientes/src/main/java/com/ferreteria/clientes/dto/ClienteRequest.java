package com.ferreteria.clientes.dto;

import jakarta.validation.constraints.NotBlank;

public record ClienteRequest(
    @NotBlank String nombre,
    @NotBlank String dni,
    String direccion,
    String telefono,
    Boolean activo
) {}
