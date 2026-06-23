package com.ferreteria.productos.service.impl;

import com.ferreteria.productos.dto.ProductoRequest;
import com.ferreteria.productos.dto.ProductoResponse;
import com.ferreteria.productos.entity.Producto;
import com.ferreteria.productos.entity.Proveedor;
import com.ferreteria.productos.exception.BusinessException;
import com.ferreteria.productos.exception.ResourceNotFoundException;
import com.ferreteria.productos.repository.ProductoRepository;
import com.ferreteria.productos.repository.ProveedorRepository;
import com.ferreteria.productos.service.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;

    @Override
    public List<ProductoResponse> listarProductos() {
        return productoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public ProductoResponse obtenerPorId(Long id) {
        return toResponse(findProducto(id));
    }

    @Override
    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        Proveedor proveedor = findProveedor(request.idProveedor());
        Producto producto = Producto.builder()
            .nombre(request.nombre())
            .categoria(request.categoria())
            .precio(request.precio())
            .stock(request.stock())
            .proveedor(proveedor)
            .build();
        return toResponse(productoRepository.save(producto));
    }

    @Override
    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = findProducto(id);
        producto.setNombre(request.nombre());
        producto.setCategoria(request.categoria());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setProveedor(findProveedor(request.idProveedor()));
        return toResponse(productoRepository.save(producto));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        productoRepository.delete(findProducto(id));
    }

    @Override
    public List<ProductoResponse> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre).stream().map(this::toResponse).toList();
    }

    @Override
    public List<String> obtenerCategorias() {
        return productoRepository.findDistinctCategorias();
    }

    @Override
    public List<ProductoResponse> obtenerProductosStockBajo(Integer umbral) {
        int valor = umbral == null ? 5 : umbral;
        return productoRepository.findByStockLessThanEqual(valor).stream().map(this::toResponse).toList();
    }

    /**
     * Endpoint llamado por ms-ventas vía OpenFeign al registrar una venta.
     * Descuenta el stock del producto.
     */
    @Override
    @Transactional
    public void descontarStock(Long id, Integer cantidad) {
        Producto producto = findProducto(id);
        if (producto.getStock() < cantidad) {
            throw new BusinessException("Stock insuficiente para el producto: " + producto.getNombre());
        }
        log.info("Descontando stock: producto={} cantidad={}", producto.getNombre(), cantidad);
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }

    /**
     * Endpoint llamado por ms-ventas vía OpenFeign al registrar una devolución.
     * Restaura el stock del producto.
     */
    @Override
    @Transactional
    public void restaurarStock(Long id, Integer cantidad) {
        Producto producto = findProducto(id);
        log.info("Restaurando stock: producto={} cantidad={}", producto.getNombre(), cantidad);
        producto.setStock(producto.getStock() + cantidad);
        productoRepository.save(producto);
    }

    private Producto findProducto(Long id) {
        return productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + id));
    }

    private Proveedor findProveedor(Long id) {
        return proveedorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id " + id));
    }

    private ProductoResponse toResponse(Producto p) {
        return new ProductoResponse(p.getId(), p.getNombre(), p.getPrecio(), p.getStock(),
            p.getCategoria(), p.getProveedor() != null ? p.getProveedor().getId() : null);
    }
}
