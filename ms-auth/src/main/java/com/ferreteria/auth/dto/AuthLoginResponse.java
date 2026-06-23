package com.ferreteria.auth.dto;

public record AuthLoginResponse(
    String token,
    Long idUsuario,
    String nombre,
    String correo,
    String rol,
    String foto
) {}
