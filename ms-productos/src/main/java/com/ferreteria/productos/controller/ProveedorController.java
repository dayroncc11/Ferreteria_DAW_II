package com.ferreteria.productos.controller;

import com.ferreteria.productos.dto.ProveedorRequest;
import com.ferreteria.productos.dto.ProveedorResponse;
import com.ferreteria.productos.service.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<List<ProveedorResponse>> listar() {
        List<ProveedorResponse> proveedores = proveedorService.listar();
        System.out.println("============ [MS-PRODUCTOS] =============");
        System.out.println("  Operacion : GET /api/proveedores");
        System.out.println("  Estado    : CONSULTA EXITOSA");
        System.out.println("  Resultado : " + proveedores.size() + " proveedor(es) encontrado(s)");
        System.out.println("==========================================");
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorResponse> obtenerPorId(@PathVariable Long id) {
        ProveedorResponse proveedor = proveedorService.obtenerPorId(id);
        System.out.println("============ [MS-PRODUCTOS] =============");
        System.out.println("  Operacion : GET /api/proveedores/" + id);
        System.out.println("  Estado    : CONSULTA EXITOSA");
        System.out.println("  Proveedor : " + proveedor.razonSocial());
        System.out.println("==========================================");
        return ResponseEntity.ok(proveedor);
    }

    @PostMapping
    public ResponseEntity<ProveedorResponse> crear(@Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse response = proveedorService.crear(request);
        System.out.println("============ [MS-PRODUCTOS] =============");
        System.out.println("  Operacion : POST /api/proveedores");
        System.out.println("  Estado    : REGISTRO EXITOSO");
        System.out.println("  Proveedor : " + response.razonSocial());
        System.out.println("  RUC       : " + response.ruc());
        System.out.println("==========================================");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse response = proveedorService.actualizar(id, request);
        System.out.println("============ [MS-PRODUCTOS] =============");
        System.out.println("  Operacion : PUT /api/proveedores/" + id);
        System.out.println("  Estado    : ACTUALIZACION EXITOSA");
        System.out.println("  Proveedor : " + response.razonSocial());
        System.out.println("==========================================");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        proveedorService.eliminar(id);
        System.out.println("============ [MS-PRODUCTOS] =============");
        System.out.println("  Operacion : DELETE /api/proveedores/" + id);
        System.out.println("  Estado    : ELIMINACION EXITOSA");
        System.out.println("  ID        : " + id);
        System.out.println("==========================================");
        return ResponseEntity.noContent().build();
    }
}
