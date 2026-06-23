package com.ferreteria.ventas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DevolucionRequest(
    @NotNull Long idVenta,
    @NotBlank String motivo
) {}
