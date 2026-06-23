package com.ferreteria.clientes.service.impl;

import com.ferreteria.clientes.dto.ClienteRequest;
import com.ferreteria.clientes.dto.ClienteResponse;
import com.ferreteria.clientes.entity.Cliente;
import com.ferreteria.clientes.exception.ResourceNotFoundException;
import com.ferreteria.clientes.repository.ClienteRepository;
import com.ferreteria.clientes.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    @Override
    public List<ClienteResponse> listarClientes() {
        return clienteRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<ClienteResponse> listarActivos() {
        return clienteRepository.findByActivoTrue().stream().map(this::toResponse).toList();
    }

    @Override
    public ClienteResponse obtenerPorId(Long id) {
        return toResponse(findCliente(id));
    }

    @Override
    public ClienteResponse buscarPorDni(String dni) {
        Cliente cliente = clienteRepository.findByDniRuc(dni)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        return toResponse(cliente);
    }

    @Override
    @Transactional
    public ClienteResponse registrarCliente(ClienteRequest request) {
        log.info("Registrando cliente: {}", request.nombre());
        Cliente cliente = Cliente.builder()
            .nombre(request.nombre())
            .dniRuc(request.dni())
            .direccion(request.direccion())
            .telefono(request.telefono())
            .activo(request.activo() == null ? true : request.activo())
            .build();
        return toResponse(clienteRepository.save(cliente));
    }

    @Override
    @Transactional
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = findCliente(id);
        cliente.setNombre(request.nombre());
        cliente.setDniRuc(request.dni());
        cliente.setDireccion(request.direccion());
        cliente.setTelefono(request.telefono());
        if (request.activo() != null) cliente.setActivo(request.activo());
        return toResponse(clienteRepository.save(cliente));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        clienteRepository.delete(findCliente(id));
    }

    @Override
    @Transactional
    public ClienteResponse activar(Long id) {
        Cliente cliente = findCliente(id);
        cliente.setActivo(true);
        return toResponse(clienteRepository.save(cliente));
    }

    @Override
    @Transactional
    public ClienteResponse desactivar(Long id) {
        Cliente cliente = findCliente(id);
        cliente.setActivo(false);
        return toResponse(clienteRepository.save(cliente));
    }

    private Cliente findCliente(Long id) {
        return clienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id " + id));
    }

    private ClienteResponse toResponse(Cliente c) {
        return new ClienteResponse(c.getId(), c.getNombre(), c.getDniRuc(),
            c.getDireccion(), c.getTelefono(), c.getActivo());
    }
}
