package com.ferreteria.clientes.controller;

import com.ferreteria.clientes.dto.ClienteRequest;
import com.ferreteria.clientes.dto.ClienteResponse;
import com.ferreteria.clientes.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST del microservicio de Clientes.
 * Disponible en: http://localhost:8093/api/clientes
 */
@Slf4j
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listarClientes() {
        List<ClienteResponse> clientes = clienteService.listarClientes();
        System.out.println("============= [MS-CLIENTES] =============");
        System.out.println("  Operacion : GET /api/clientes");
        System.out.println("  Estado    : CONSULTA EXITOSA");
        System.out.println("  Resultado : " + clientes.size() + " cliente(s) encontrado(s)");
        System.out.println("==========================================");
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ClienteResponse>> listarActivos() {
        List<ClienteResponse> activos = clienteService.listarActivos();
        System.out.println("============= [MS-CLIENTES] =============");
        System.out.println("  Operacion : GET /api/clientes/activos");
        System.out.println("  Estado    : CONSULTA EXITOSA");
        System.out.println("  Resultado : " + activos.size() + " cliente(s) activo(s)");
        System.out.println("==========================================");
        return ResponseEntity.ok(activos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> obtenerPorId(@PathVariable Long id) {
        ClienteResponse cliente = clienteService.obtenerPorId(id);
        System.out.println("============= [MS-CLIENTES] =============");
        System.out.println("  Operacion : GET /api/clientes/" + id);
        System.out.println("  Estado    : CONSULTA EXITOSA");
        System.out.println("  Cliente   : " + cliente.nombre());
        System.out.println("==========================================");
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/buscar")
    public ResponseEntity<ClienteResponse> buscarPorDni(@RequestParam String dni) {
        ClienteResponse cliente = clienteService.buscarPorDni(dni);
        System.out.println("============= [MS-CLIENTES] =============");
        System.out.println("  Operacion : GET /api/clientes/buscar?dni=" + dni);
        System.out.println("  Estado    : CONSULTA EXITOSA");
        System.out.println("  Cliente   : " + cliente.nombre());
        System.out.println("==========================================");
        return ResponseEntity.ok(cliente);
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> registrar(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = clienteService.registrarCliente(request);
        System.out.println("============= [MS-CLIENTES] =============");
        System.out.println("  Operacion : POST /api/clientes");
        System.out.println("  Estado    : REGISTRO EXITOSO");
        System.out.println("  Cliente   : " + response.nombre());
        System.out.println("  DNI/RUC   : " + response.dni());
        System.out.println("==========================================");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = clienteService.actualizar(id, request);
        System.out.println("============= [MS-CLIENTES] =============");
        System.out.println("  Operacion : PUT /api/clientes/" + id);
        System.out.println("  Estado    : ACTUALIZACION EXITOSA");
        System.out.println("  Cliente   : " + response.nombre());
        System.out.println("==========================================");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        System.out.println("============= [MS-CLIENTES] =============");
        System.out.println("  Operacion : DELETE /api/clientes/" + id);
        System.out.println("  Estado    : ELIMINACION EXITOSA");
        System.out.println("  ID        : " + id);
        System.out.println("==========================================");
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ClienteResponse> activar(@PathVariable Long id) {
        ClienteResponse response = clienteService.activar(id);
        System.out.println("============= [MS-CLIENTES] =============");
        System.out.println("  Operacion : PATCH /api/clientes/" + id + "/activar");
        System.out.println("  Estado    : ACTIVACION EXITOSA");
        System.out.println("  Cliente   : " + response.nombre());
        System.out.println("==========================================");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ClienteResponse> desactivar(@PathVariable Long id) {
        ClienteResponse response = clienteService.desactivar(id);
        System.out.println("============= [MS-CLIENTES] =============");
        System.out.println("  Operacion : PATCH /api/clientes/" + id + "/desactivar");
        System.out.println("  Estado    : DESACTIVACION EXITOSA");
        System.out.println("  Cliente   : " + response.nombre());
        System.out.println("==========================================");
        return ResponseEntity.ok(response);
    }
}
