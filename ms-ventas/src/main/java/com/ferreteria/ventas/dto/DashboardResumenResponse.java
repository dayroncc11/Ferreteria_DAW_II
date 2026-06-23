package com.ferreteria.ventas.dto;

import java.util.List;

/**
 * DTO de respuesta para el resumen del Dashboard.
 * Agrega datos de ventas, productos, clientes y stock bajo.
 */
public record DashboardResumenResponse(
        long totalProductos,
        long totalClientes,
        long ventasHoy,
        double ingresoHoy,
        List<ProductoDto> productosStockBajo,
        List<VentaResponse> ultimasVentas
) {}
