package com.ferreteria.productos.dto;

import jakarta.validation.constraints.NotBlank;

public record ProveedorRequest(
    @NotBlank String razonSocial,
    @NotBlank String ruc,
    String telefono
) {}
