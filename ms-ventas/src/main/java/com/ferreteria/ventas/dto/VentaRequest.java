package com.ferreteria.ventas.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record VentaRequest(
    @NotNull Long idCliente,
    @NotNull Long idUsuario,
    @NotEmpty List<DetalleVentaItemRequest> items
) {}
