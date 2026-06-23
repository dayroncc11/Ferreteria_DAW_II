package com.ferreteria.auth.dto;

public record UsuarioResponse(Long idUsuario, String nombre, String correo, String contrasena, String rol, String foto) {}
