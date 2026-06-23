package com.ferreteria.ventas.controller;

import com.ferreteria.ventas.dto.DevolucionRequest;
import com.ferreteria.ventas.dto.DevolucionResponse;
import com.ferreteria.ventas.service.DevolucionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/devoluciones")
@RequiredArgsConstructor
public class DevolucionController {

    private final DevolucionService devolucionService;

    @GetMapping
    public ResponseEntity<List<DevolucionResponse>> listar() {
        List<DevolucionResponse> devoluciones = devolucionService.listarDevoluciones();
        System.out.println("============= [MS-VENTAS] ===============");
        System.out.println("  Operacion : GET /api/devoluciones");
        System.out.println("  Estado    : CONSULTA EXITOSA");
        System.out.println("  Resultado : " + devoluciones.size() + " devolucion(es) encontrada(s)");
        System.out.println("==========================================");
        return ResponseEntity.ok(devoluciones);
    }

    @PostMapping
    public ResponseEntity<DevolucionResponse> registrar(@Valid @RequestBody DevolucionRequest request) {
        DevolucionResponse response = devolucionService.registrarDevolucion(request);
        System.out.println("============= [MS-VENTAS] ===============");
        System.out.println("  Operacion : POST /api/devoluciones");
        System.out.println("  Estado    : DEVOLUCION REGISTRADA EXITOSAMENTE");
        System.out.println("  Devol. ID : " + response.id());
        System.out.println("  Venta ID  : " + request.idVenta());
        System.out.println("  Motivo    : " + request.motivo());
        System.out.println("==========================================");
        log.info("POST /api/devoluciones — venta={}", request.idVenta());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/venta/{idVenta}/tiene-devolucion")
    public ResponseEntity<Boolean> tienDevolucion(@PathVariable Long idVenta) {
        Boolean tiene = devolucionService.ventaTieneDevolucion(idVenta);
        System.out.println("============= [MS-VENTAS] ===============");
        System.out.println("  Operacion : GET /api/devoluciones/venta/" + idVenta + "/tiene-devolucion");
        System.out.println("  Estado    : CONSULTA EXITOSA");
        System.out.println("  Resultado : " + (tiene ? "SI tiene devolucion" : "NO tiene devolucion"));
        System.out.println("==========================================");
        return ResponseEntity.ok(tiene);
    }
}
