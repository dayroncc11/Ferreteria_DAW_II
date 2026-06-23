package com.ferreteria.clientes.repository;

import com.ferreteria.clientes.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByDniRuc(String dniRuc);
    List<Cliente> findByActivoTrue();
    boolean existsByDniRuc(String dniRuc);
}
