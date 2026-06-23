package com.ferreteria.ventas.service.impl;

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
import com.ferreteria.ventas.service.DevolucionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DevolucionServiceImpl implements DevolucionService {

    private final DevolucionRepository devolucionRepository;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoClient productoClient;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public List<DevolucionResponse> listarDevoluciones() {
        return devolucionRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public DevolucionResponse registrarDevolucion(DevolucionRequest request) {
        Venta venta = ventaRepository.findById(request.idVenta())
            .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id " + request.idVenta()));

        if (devolucionRepository.existsByVentaId(request.idVenta())) {
            throw new BusinessException("La venta ya tiene una devolución registrada.");
        }

        // Restaurar stock de cada producto vía Feign → ms-productos
        List<DetalleVenta> detalles = detalleVentaRepository.findByVentaId(request.idVenta());
        for (DetalleVenta detalle : detalles) {
            log.info("Feign → restaurarStock: producto={} cantidad={}",
                detalle.getProductoId(), detalle.getCantidad());
            productoClient.restaurarStock(detalle.getProductoId(), new StockRequest(detalle.getCantidad()));
        }

        Devolucion devolucion = Devolucion.builder()
            .venta(venta)
            .fecha(LocalDateTime.now())
            .motivo(request.motivo())
            .build();

        log.info("Devolución registrada para venta id={}", venta.getId());
        return toResponse(devolucionRepository.save(devolucion));
    }

    @Override
    public boolean ventaTieneDevolucion(Long idVenta) {
        return devolucionRepository.existsByVentaId(idVenta);
    }

    private DevolucionResponse toResponse(Devolucion d) {
        return new DevolucionResponse(d.getId(), d.getVenta().getId(),
            d.getFecha().toLocalDate().format(DATE_FMT), d.getMotivo());
    }
}
