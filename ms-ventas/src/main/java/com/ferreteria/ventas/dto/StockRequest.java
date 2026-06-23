package com.ferreteria.ventas.dto;

/** DTO enviado a ms-productos vía Feign para descontar/restaurar stock. */
public record StockRequest(Integer cantidad) {}
