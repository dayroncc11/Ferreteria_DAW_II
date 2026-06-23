package com.ferreteria.productos.service;

import com.ferreteria.productos.dto.ProveedorRequest;
import com.ferreteria.productos.dto.ProveedorResponse;
import java.util.List;

public interface ProveedorService {
    List<ProveedorResponse> listar();
    ProveedorResponse obtenerPorId(Long id);
    ProveedorResponse crear(ProveedorRequest request);
    ProveedorResponse actualizar(Long id, ProveedorRequest request);
    void eliminar(Long id);
}
