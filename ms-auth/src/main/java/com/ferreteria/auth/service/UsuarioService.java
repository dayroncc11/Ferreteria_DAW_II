package com.ferreteria.auth.service;

import com.ferreteria.auth.dto.UsuarioRequest;
import com.ferreteria.auth.dto.UsuarioResponse;
import java.util.List;

public interface UsuarioService {
    List<UsuarioResponse> listar();
    UsuarioResponse obtenerPorId(Long id);
    UsuarioResponse crear(UsuarioRequest request);
    UsuarioResponse actualizar(Long id, UsuarioRequest request);
    void eliminar(Long id);
}
