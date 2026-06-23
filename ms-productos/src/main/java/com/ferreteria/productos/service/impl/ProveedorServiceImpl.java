package com.ferreteria.productos.service.impl;

import com.ferreteria.productos.dto.ProveedorRequest;
import com.ferreteria.productos.dto.ProveedorResponse;
import com.ferreteria.productos.entity.Proveedor;
import com.ferreteria.productos.exception.BusinessException;
import com.ferreteria.productos.exception.ResourceNotFoundException;
import com.ferreteria.productos.repository.ProveedorRepository;
import com.ferreteria.productos.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    @Override
    public List<ProveedorResponse> listar() {
        return proveedorRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public ProveedorResponse obtenerPorId(Long id) {
        return toResponse(findProveedor(id));
    }

    @Override
    @Transactional
    public ProveedorResponse crear(ProveedorRequest request) {
        if (proveedorRepository.existsByRuc(request.ruc())) {
            throw new BusinessException("Ya existe un proveedor con ese RUC");
        }
        Proveedor proveedor = Proveedor.builder()
            .nombre(request.razonSocial())
            .ruc(request.ruc())
            .telefono(request.telefono())
            .build();
        return toResponse(proveedorRepository.save(proveedor));
    }

    @Override
    @Transactional
    public ProveedorResponse actualizar(Long id, ProveedorRequest request) {
        Proveedor proveedor = findProveedor(id);
        proveedor.setNombre(request.razonSocial());
        proveedor.setRuc(request.ruc());
        proveedor.setTelefono(request.telefono());
        return toResponse(proveedorRepository.save(proveedor));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        proveedorRepository.delete(findProveedor(id));
    }

    private Proveedor findProveedor(Long id) {
        return proveedorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id " + id));
    }

    private ProveedorResponse toResponse(Proveedor p) {
        return new ProveedorResponse(p.getId(), p.getNombre(), p.getRuc(), p.getTelefono());
    }
}
