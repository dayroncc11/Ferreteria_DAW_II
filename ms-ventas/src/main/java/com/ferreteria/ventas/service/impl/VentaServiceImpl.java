package com.ferreteria.ventas.service.impl;

import com.ferreteria.ventas.client.ClienteClient;
import com.ferreteria.ventas.client.ProductoClient;
import com.ferreteria.ventas.dto.*;
import com.ferreteria.ventas.entity.DetalleVenta;
import com.ferreteria.ventas.entity.Devolucion;
import com.ferreteria.ventas.entity.Venta;
import com.ferreteria.ventas.exception.BusinessException;
import com.ferreteria.ventas.exception.ResourceNotFoundException;
import com.ferreteria.ventas.repository.DetalleVentaRepository;
import com.ferreteria.ventas.repository.DevolucionRepository;
import com.ferreteria.ventas.repository.VentaRepository;
import com.ferreteria.ventas.service.VentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Implementación de VentaService con OpenFeign.
 *
 * Patrón idéntico al T1 (MatriculaServiceImpl que usaba EstudianteClient):
 *   - clienteClient.getClienteById()  → verifica existencia y estado activo del cliente
 *   - productoClient.getProductoById()→ verifica existencia y stock disponible
 *   - productoClient.descontarStock() → actualiza stock en ms-productos
 *
 * El manejo de errores Feign está centralizado en GlobalExceptionHandler,
 * pero aquí también se hace logging explícito para auditoría.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final DevolucionRepository devolucionRepository;

    // Feign Clients — comunicación con otros microservicios
    private final ClienteClient clienteClient;
    private final ProductoClient productoClient;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public List<VentaResponse> listarVentas() {
        return ventaRepository.findAllByOrderByFechaDesc().stream().map(this::toVentaResponse).toList();
    }

    @Override
    public VentaResponse obtenerVentaPorId(Long id) {
        return toVentaResponse(findVenta(id));
    }

    @Override
    public List<DetalleVentaResponse> obtenerDetallesPorVenta(Long idVenta) {
        findVenta(idVenta);
        return detalleVentaRepository.findByVentaId(idVenta).stream().map(this::toDetalleResponse).toList();
    }

    @Override
    @Transactional
    public VentaResponse registrarVenta(VentaRequest request) {
        log.info("Registrando venta: cliente={} items={}", request.idCliente(), request.items().size());

        // 1. Verificar cliente vía Feign → ms-clientes
        //    Igual que EstudianteClient en el T1 que verificaba matrícula del estudiante
        ClienteDto cliente = clienteClient.getClienteById(request.idCliente());
        if (Boolean.FALSE.equals(cliente.activo())) {
            throw new BusinessException("No se puede realizar la venta: el cliente está inactivo.");
        }
        log.info("Cliente verificado vía Feign: {} [activo={}]", cliente.nombre(), cliente.activo());

        // 2. Calcular total
        double total = request.items().stream().mapToDouble(DetalleVentaItemRequest::subtotal).sum();

        // 3. Guardar venta
        Venta venta = Venta.builder()
            .clienteId(request.idCliente())
            .usuarioId(request.idUsuario())
            .fecha(LocalDateTime.now())
            .total(total)
            .build();
        Venta ventaGuardada = ventaRepository.save(venta);

        // 4. Procesar cada ítem: verificar producto y descontar stock vía Feign → ms-productos
        for (DetalleVentaItemRequest item : request.items()) {

            // 4a. Verificar que el producto existe y tiene stock suficiente
            ProductoDto producto = productoClient.getProductoById(item.idProducto());
            if (producto.stock() < item.cantidad()) {
                throw new BusinessException("Stock insuficiente para: " + producto.nombre()
                    + " (disponible: " + producto.stock() + ", solicitado: " + item.cantidad() + ")");
            }

            // 4b. Descontar stock en ms-productos vía Feign
            log.info("Feign → descontarStock: producto={} cantidad={}", producto.nombre(), item.cantidad());
            productoClient.descontarStock(item.idProducto(), new StockRequest(item.cantidad()));

            // 4c. Guardar detalle
            DetalleVenta detalle = DetalleVenta.builder()
                .venta(ventaGuardada)
                .productoId(item.idProducto())
                .cantidad(item.cantidad())
                .precioUnitario(item.precio())
                .subtotal(item.subtotal())
                .build();
            detalleVentaRepository.save(detalle);
        }

        log.info("Venta registrada exitosamente: id={} total={}", ventaGuardada.getId(), total);

        return toVentaResponse(ventaGuardada);
    }

    @Override
    public List<VentaResponse> obtenerVentasHoy() {
        LocalDate today = LocalDate.now();
        return ventaRepository.findByFechaBetweenOrderByFechaDesc(
            today.atStartOfDay(), today.plusDays(1).atStartOfDay()
        ).stream().map(this::toVentaResponse).toList();
    }

    @Override
    public DashboardResumenResponse obtenerResumenDashboard() {
        // Datos locales de ventas
        LocalDate today = LocalDate.now();
        List<Venta> ventasDeHoy = ventaRepository.findByFechaBetweenOrderByFechaDesc(
            today.atStartOfDay(), today.plusDays(1).atStartOfDay()
        );
        long ventasHoy = ventasDeHoy.size();
        double ingresoHoy = ventasDeHoy.stream().mapToDouble(Venta::getTotal).sum();

        // Últimas 10 ventas
        List<VentaResponse> ultimasVentas = ventaRepository
            .findAllByOrderByFechaDesc().stream()
            .limit(10)
            .map(this::toVentaResponse)
            .toList();

        // Datos de productos vía Feign → ms-productos
        List<ProductoDto> todosProductos = productoClient.listarProductos();
        long totalProductos = todosProductos.size();
        List<ProductoDto> stockBajo = productoClient.obtenerStockBajo(5);

        // Datos de clientes vía Feign → ms-clientes
        long totalClientes = clienteClient.listarClientes().size();

        return new DashboardResumenResponse(
            totalProductos, totalClientes, ventasHoy, ingresoHoy, stockBajo, ultimasVentas
        );
    }

    private Venta findVenta(Long id) {
        return ventaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id " + id));
    }

    private VentaResponse toVentaResponse(Venta v) {
        return new VentaResponse(v.getId(),
            v.getFecha().toLocalDate().format(DATE_FMT), v.getTotal(), v.getClienteId(), v.getUsuarioId());
    }

    private DetalleVentaResponse toDetalleResponse(DetalleVenta d) {
        return new DetalleVentaResponse(d.getId(), d.getVenta().getId(),
            d.getProductoId(), d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal());
    }
}
