package com.ferreteria.productos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio de Productos y Proveedores — Ferretería Molina.
 * Se registra automáticamente en Eureka al arrancar (puerto 8091).
 */
@SpringBootApplication
public class MsProductosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsProductosApplication.class, args);
    }
}
