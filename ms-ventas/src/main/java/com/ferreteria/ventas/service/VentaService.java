package com.ferreteria.ventas.service;

import com.ferreteria.ventas.dto.*;
import java.util.List;

public interface VentaService {
    List<VentaResponse> listarVentas();
    VentaResponse obtenerVentaPorId(Long id);
    List<DetalleVentaResponse> obtenerDetallesPorVenta(Long idVenta);
    VentaResponse registrarVenta(VentaRequest request);
    List<VentaResponse> obtenerVentasHoy();
    DashboardResumenResponse obtenerResumenDashboard();
}
