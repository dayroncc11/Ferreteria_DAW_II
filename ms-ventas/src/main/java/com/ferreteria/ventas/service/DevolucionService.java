package com.ferreteria.ventas.service;

import com.ferreteria.ventas.dto.DevolucionRequest;
import com.ferreteria.ventas.dto.DevolucionResponse;
import java.util.List;

public interface DevolucionService {
    List<DevolucionResponse> listarDevoluciones();
    DevolucionResponse registrarDevolucion(DevolucionRequest request);
    boolean ventaTieneDevolucion(Long idVenta);
}
