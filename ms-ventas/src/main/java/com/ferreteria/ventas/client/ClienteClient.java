package com.ferreteria.ventas.client;

import com.ferreteria.ventas.dto.ClienteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Cliente Feign para ms-clientes.
 *
 * Patrón idéntico al T1: EstudianteClient.java en servicio-matricula.
 * Permite verificar que un cliente existe y está activo antes de crear una venta.
 */
@FeignClient(name = "ms-clientes")
public interface ClienteClient {

    /**
     * Obtiene un cliente por su ID.
     * Usado en VentaServiceImpl para verificar existencia y estado activo.
     */
    @GetMapping("/api/clientes/{id}")
    ClienteDto getClienteById(@PathVariable("id") Long id);
    /**
     * Lista todos los clientes — usado para contar el total en el dashboard.
     */
    @GetMapping("/api/clientes")
    List<ClienteDto> listarClientes();
}
