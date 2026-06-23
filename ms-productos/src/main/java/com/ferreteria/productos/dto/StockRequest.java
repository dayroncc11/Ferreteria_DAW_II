package com.ferreteria.productos.dto;

/**
 * DTO usado por ms-ventas vía Feign para descontar stock de un producto.
 * PUT /api/productos/{id}/stock
 */
public record StockRequest(Integer cantidad) {}
