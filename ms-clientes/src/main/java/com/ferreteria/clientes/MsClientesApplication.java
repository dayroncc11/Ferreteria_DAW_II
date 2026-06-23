package com.ferreteria.clientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio de Clientes — Ferretería Molina.
 * Se registra automáticamente en Eureka al arrancar (puerto 8093).
 */
@SpringBootApplication
public class MsClientesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsClientesApplication.class, args);
    }
}
