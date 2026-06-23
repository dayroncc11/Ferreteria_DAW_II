package com.ferreteria.ventas.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalle_venta")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    // productoId como Long — no como relación JPA a ms-productos
    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}
