package com.ferreteria.ventas.repository;

import com.ferreteria.ventas.entity.Devolucion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DevolucionRepository extends JpaRepository<Devolucion, Long> {
    boolean existsByVentaId(Long ventaId);
}
