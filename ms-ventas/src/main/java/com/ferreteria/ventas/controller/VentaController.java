package com.ferreteria.ventas.controller;

import com.ferreteria.ventas.dto.*;
import com.ferreteria.ventas.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST del microservicio de Ventas.
 * Disponible en: http://localhost:8092/api/ventas
 */
@Slf4j
@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    public ResponseEntity<List<VentaResponse>> listarVentas() {
        List<VentaResponse> ventas = ventaService.listarVentas();
        System.out.println("============= [MS-VENTAS] ===============");
        System.out.println("  Operacion : GET /api/ventas");
        System.out.println("  Estado    : CONSULTA EXITOSA");
        System.out.println("  Resultado : " + ventas.size() + " venta(s) encontrada(s)");
        System.out.println("==========================================");
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponse> obtenerPorId(@PathVariable Long id) {
        VentaResponse venta = ventaService.obtenerVentaPorId(id);
        System.out.println("============= [MS-VENTAS] ===============");
        System.out.println("  Operacion : GET /api/ventas/" + id);
        System.out.println("  Estado    : CONSULTA EXITOSA");
        System.out.println("  Total     : S/ " + venta.total());
        System.out.println("==========================================");
        return ResponseEntity.ok(venta);
    }

    @GetMapping("/{id}/detalles")
    public ResponseEntity<List<DetalleVentaResponse>> obtenerDetalles(@PathVariable Long id) {
        List<DetalleVentaResponse> detalles = ventaService.obtenerDetallesPorVenta(id);
        System.out.println("============= [MS-VENTAS] ===============");
        System.out.println("  Operacion : GET /api/ventas/" + id + "/detalles");
        System.out.println("  Estado    : CONSULTA EXITOSA");
        System.out.println("  Resultado : " + detalles.size() + " detalle(s) encontrado(s)");
        System.out.println("==========================================");
        return ResponseEntity.ok(detalles);
    }

    @GetMapping("/hoy")
    public ResponseEntity<List<VentaResponse>> ventasHoy() {
        List<VentaResponse> ventas = ventaService.obtenerVentasHoy();
        System.out.println("============= [MS-VENTAS] ===============");
        System.out.println("  Operacion : GET /api/ventas/hoy");
        System.out.println("  Estado    : CONSULTA EXITOSA");
        System.out.println("  Resultado : " + ventas.size() + " venta(s) del dia");
        System.out.println("==========================================");
        return ResponseEntity.ok(ventas);
    }

    @PostMapping
    public ResponseEntity<VentaResponse> registrar(@Valid @RequestBody VentaRequest request) {
        VentaResponse response = ventaService.registrarVenta(request);
        System.out.println("============= [MS-VENTAS] ===============");
        System.out.println("  Operacion : POST /api/ventas");
        System.out.println("  Estado    : VENTA REGISTRADA EXITOSAMENTE");
        System.out.println("  Venta ID  : " + response.idVenta());
        System.out.println("  Cliente   : ID " + request.idCliente());
        System.out.println("  Total     : S/ " + response.total());
        System.out.println("==========================================");
        log.info("POST /api/ventas — cliente={}", request.idCliente());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para el dashboard del frontend Angular.
     * Agrega métricas de ventas, productos (vía Feign) y clientes (vía Feign).
     */
    @GetMapping("/dashboard/resumen")
    public ResponseEntity<DashboardResumenResponse> dashboard() {
        DashboardResumenResponse resumen = ventaService.obtenerResumenDashboard();
        System.out.println("============= [MS-VENTAS] ===============");
        System.out.println("  Operacion : GET /api/ventas/dashboard/resumen");
        System.out.println("  Estado    : CONSULTA EXITOSA");
        System.out.println("  Dashboard : Datos cargados correctamente");
        System.out.println("==========================================");
        return ResponseEntity.ok(resumen);
    }
}
