package com.ferreteria.productos.dto;

public record ProductoResponse(
    Long idProducto,
    String nombre,
    Double precio,
    Integer stock,
    String categoria,
    Long idProveedor
) {}
