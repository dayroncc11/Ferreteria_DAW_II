package com.ferreteria.ventas.dto;

public record VentaResponse(Long idVenta, String fecha, Double total, Long idCliente, Long idUsuario) {}
