package com.ferreteria.ventas.dto;

/**
 * DTO que recibe ms-ventas desde ms-clientes vía Feign.
 * Debe tener los mismos campos que ClienteResponse en ms-clientes.
 */
public record ClienteDto(Long idCliente, String nombre, String dni, String direccion, String telefono, Boolean activo) {}
