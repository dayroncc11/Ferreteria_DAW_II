package com.ferreteria.ventas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Venta en ms-ventas.
 *
 * IMPORTANTE: clienteId y usuarioId se almacenan como Long (IDs simples),
 * NO como relaciones JPA a otras entidades. Esto es correcto en microservicios:
 * cada servicio es dueño de sus propios datos y NO tiene acceso directo
 * a las tablas de otros servicios.
 */
@Entity
@Table(name = "venta")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;
    private Double total;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Builder.Default
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    private List<Devolucion> devoluciones = new ArrayList<>();
}
