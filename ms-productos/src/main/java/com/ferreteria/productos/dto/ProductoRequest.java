package com.ferreteria.productos.dto;

import jakarta.validation.constraints.*;

public record ProductoRequest(
    @NotBlank String nombre,
    @NotNull @Positive Double precio,
    @NotNull @PositiveOrZero Integer stock,
    @NotBlank String categoria,
    @NotNull Long idProveedor
) {}
