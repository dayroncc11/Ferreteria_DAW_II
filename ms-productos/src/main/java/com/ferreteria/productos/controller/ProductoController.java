package com.ferreteria.productos.controller;

import com.ferreteria.productos.dto.ProductoRequest;
import com.ferreteria.productos.dto.ProductoResponse;
import com.ferreteria.productos.dto.StockRequest;
import com.ferreteria.productos.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST del microservicio de Productos.
 * Disponible en: http://localhost:8091/api/productos
 *
 * Endpoint especial para Feign:
 *   PUT /api/productos/{id}/stock/descontar  → llamado por ms-ventas al registrar venta
 *   PUT /api/productos/{id}/stock/restaurar  → llamado por ms-ventas al registrar devolución
 */
@Slf4j
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar() {
        List<ProductoResponse> productos = productoService.listarProductos();
        System.out.println("============ [MS-PRODUCTOS] =============");
        System.out.println("  Operacion : GET /api/productos");
        System.out.println("  Estado    : CONSULTA EXITOSA");
        System.out.println("  Resultado : " + productos.size() + " producto(s) encontrado(s)");
        System.out.println("==========================================");
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtenerPorId(@PathVariable Long id) {
        ProductoResponse producto = productoService.obtenerPorId(id);
        System.out.println("============ [MS-PRODUCTOS] =============");
        System.out.println("  Operacion : GET /api/productos/" + id);
        System.out.println("  Estado    : CONSULTA EXITOSA");
        System.out.println("  Producto  : " + producto.nombre());
        System.out.println("  Stock     : " + producto.stock() + " unidades");
        System.out.println("==========================================");
        return ResponseEntity.ok(producto);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoResponse>> buscarPorNombre(@RequestParam String nombre) {
        List<ProductoResponse> productos = productoService.buscarPorNombre(nombre);
        System.out.println("============ [MS-PRODUCTOS] =============");
        System.out.println("  Operacion : GET /api/productos/buscar?nombre=" + nombre);
        System.out.println("  Estado    : CONSULTA EXITOSA");
        System.out.println("  Resultado : " + productos.size() + " producto(s) encontrado(s)");
        System.out.println("==========================================");
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> obtenerCategorias() {
        List<String> categorias = productoService.obtenerCategorias();
        System.out.println("============ [MS-PRODUCTOS] =============");
        System.out.println("  Operacion : GET /api/productos/categorias");
        System.out.println("  Estado    : CONSULTA EXITOSA");
        System.out.println("  Resultado : " + categorias.size() + " categoria(s)");
        System.out.println("==========================================");
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoResponse>> stockBajo(@RequestParam(required = false) Integer umbral) {
        List<ProductoResponse> productos = productoService.obtenerProductosStockBajo(umbral);
        System.out.println("============ [MS-PRODUCTOS] =============");
        System.out.println("  Operacion : GET /api/productos/stock-bajo");
        System.out.println("  Estado    : CONSULTA EXITOSA");
        System.out.println("  Resultado : " + productos.size() + " producto(s) con stock bajo");
        System.out.println("==========================================");
        return ResponseEntity.ok(productos);
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.crear(request);
        System.out.println("============ [MS-PRODUCTOS] =============");
        System.out.println("  Operacion : POST /api/productos");
        System.out.println("  Estado    : REGISTRO EXITOSO");
        System.out.println("  Producto  : " + response.nombre());
        System.out.println("  Precio    : S/ " + response.precio());
        System.out.println("  Stock     : " + response.stock() + " unidades");
        System.out.println("==========================================");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.actualizar(id, request);
        System.out.println("============ [MS-PRODUCTOS] =============");
        System.out.println("  Operacion : PUT /api/productos/" + id);
        System.out.println("  Estado    : ACTUALIZACION EXITOSA");
        System.out.println("  Producto  : " + response.nombre());
        System.out.println("==========================================");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        System.out.println("============ [MS-PRODUCTOS] =============");
        System.out.println("  Operacion : DELETE /api/productos/" + id);
        System.out.println("  Estado    : ELIMINACION EXITOSA");
        System.out.println("  ID        : " + id);
        System.out.println("==========================================");
        return ResponseEntity.noContent().build();
    }

    // ── Endpoints Feign ── llamados por ms-ventas ──────────────────────────

    /**
     * Descuenta stock — llamado por ms-ventas al registrar una venta.
     */
    @PutMapping("/{id}/stock/descontar")
    public ResponseEntity<Void> descontarStock(@PathVariable Long id, @RequestBody StockRequest request) {
        productoService.descontarStock(id, request.cantidad());
        System.out.println("============ [MS-PRODUCTOS] =============");
        System.out.println("  Operacion : PUT /api/productos/" + id + "/stock/descontar");
        System.out.println("  Estado    : STOCK DESCONTADO EXITOSAMENTE");
        System.out.println("  Producto  : ID " + id);
        System.out.println("  Cantidad  : -" + request.cantidad() + " unidades");
        System.out.println("==========================================");
        log.info("Feign -> descontarStock: producto={} cantidad={}", id, request.cantidad());
        return ResponseEntity.ok().build();
    }

    /**
     * Restaura stock — llamado por ms-ventas al registrar una devolución.
     */
    @PutMapping("/{id}/stock/restaurar")
    public ResponseEntity<Void> restaurarStock(@PathVariable Long id, @RequestBody StockRequest request) {
        productoService.restaurarStock(id, request.cantidad());
        System.out.println("============ [MS-PRODUCTOS] =============");
        System.out.println("  Operacion : PUT /api/productos/" + id + "/stock/restaurar");
        System.out.println("  Estado    : STOCK RESTAURADO EXITOSAMENTE");
        System.out.println("  Producto  : ID " + id);
        System.out.println("  Cantidad  : +" + request.cantidad() + " unidades");
        System.out.println("==========================================");
        log.info("Feign -> restaurarStock: producto={} cantidad={}", id, request.cantidad());
        return ResponseEntity.ok().build();
    }
}
