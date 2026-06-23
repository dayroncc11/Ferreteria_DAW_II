package com.ferreteria.productos.service;

import com.ferreteria.productos.dto.ProductoRequest;
import com.ferreteria.productos.dto.ProductoResponse;
import java.util.List;

public interface ProductoService {
    List<ProductoResponse> listarProductos();
    ProductoResponse obtenerPorId(Long id);
    ProductoResponse crear(ProductoRequest request);
    ProductoResponse actualizar(Long id, ProductoRequest request);
    void eliminar(Long id);
    List<ProductoResponse> buscarPorNombre(String nombre);
    List<String> obtenerCategorias();
    List<ProductoResponse> obtenerProductosStockBajo(Integer umbral);
    void descontarStock(Long id, Integer cantidad);
    void restaurarStock(Long id, Integer cantidad);
}
