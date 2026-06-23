package com.ferreteria.ventas.client;

import com.ferreteria.ventas.dto.ProductoDto;
import com.ferreteria.ventas.dto.StockRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Cliente Feign para ms-productos.
 *
 * Patrón idéntico al T1: EstudianteClient.java en servicio-matricula.
 *
 * @FeignClient(name = "ms-productos") — el nombre debe coincidir EXACTAMENTE
 * con spring.application.name en el application.yml de ms-productos.
 * Eureka usa ese nombre para descubrir la IP y puerto del servicio.
 *
 * No se especifica URL — Eureka + Ribbon/LoadBalancer la resuelve automáticamente.
 */
@FeignClient(name = "ms-productos")
public interface ProductoClient {

    /**
     * Obtiene un producto por su ID.
     * Usado para validar existencia antes de registrar una venta.
     */
    @GetMapping("/api/productos/{id}")
    ProductoDto getProductoById(@PathVariable("id") Long id);

    /**
     * Descuenta stock del producto al registrar una venta.
     * Equivalente al comportamiento de EstudianteClient del T1.
     */
    @PutMapping("/api/productos/{id}/stock/descontar")
    void descontarStock(@PathVariable("id") Long id, @RequestBody StockRequest request);

    /**
     * Restaura stock del producto al registrar una devolución.
     */
    @PutMapping("/api/productos/{id}/stock/restaurar")
    void restaurarStock(@PathVariable("id") Long id, @RequestBody StockRequest request);
    /**
     * Obtiene todos los productos — usado para contar el total en el dashboard.
     */
    @GetMapping("/api/productos")
    List<ProductoDto> listarProductos();

    /**
     * Obtiene productos con stock bajo — umbral configurable.
     */
    @GetMapping("/api/productos/stock-bajo")
    List<ProductoDto> obtenerStockBajo(@RequestParam("umbral") Integer umbral);
}
