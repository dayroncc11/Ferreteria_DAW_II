package com.ferreteria.clientes.service;

import com.ferreteria.clientes.dto.ClienteRequest;
import com.ferreteria.clientes.dto.ClienteResponse;
import java.util.List;

public interface ClienteService {
    List<ClienteResponse> listarClientes();
    List<ClienteResponse> listarActivos();
    ClienteResponse obtenerPorId(Long id);
    ClienteResponse buscarPorDni(String dni);
    ClienteResponse registrarCliente(ClienteRequest request);
    ClienteResponse actualizar(Long id, ClienteRequest request);
    void eliminar(Long id);
    ClienteResponse activar(Long id);
    ClienteResponse desactivar(Long id);
}
