package com.ferreteria.ventas.dto;

/**
 * DTO que recibe ms-ventas desde ms-productos vía Feign.
 * Debe tener los mismos campos que ProductoResponse en ms-productos.
 */
public record ProductoDto(Long idProducto, String nombre, Double precio, Integer stock, String categoria, Long idProveedor) {}
