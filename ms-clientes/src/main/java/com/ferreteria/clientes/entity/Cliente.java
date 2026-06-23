package com.ferreteria.clientes.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dni_ruc", nullable = false)
    private String dniRuc;

    @Column(nullable = false)
    private String nombre;

    private String direccion;
    private String telefono;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;
}
