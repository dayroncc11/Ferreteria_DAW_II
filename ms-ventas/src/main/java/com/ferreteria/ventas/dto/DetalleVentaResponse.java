package com.ferreteria.ventas.dto;

public record DetalleVentaResponse(Long id, Long idVenta, Long idProducto, Integer cantidad, Double precioUnitario, Double subtotal) {}
