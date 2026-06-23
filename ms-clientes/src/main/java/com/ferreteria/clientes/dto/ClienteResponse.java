package com.ferreteria.clientes.dto;

public record ClienteResponse(
    Long idCliente,
    String nombre,
    String dni,
    String direccion,
    String telefono,
    Boolean activo
) {}
