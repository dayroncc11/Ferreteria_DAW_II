package com.ferreteria.ventas.dto;

import jakarta.validation.constraints.*;

public record DetalleVentaItemRequest(
    @NotNull Long idProducto,
    @NotNull @Positive Integer cantidad,
    @NotNull @Positive Double precio,
    @NotNull @Positive Double subtotal
) {}
