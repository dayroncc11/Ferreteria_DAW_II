package com.ferreteria.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio de Autenticación y Usuarios — Ferretería Molina.
 * Puerto: 8090 — Se registra automáticamente en Eureka al arrancar.
 */
@SpringBootApplication
public class MsAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsAuthApplication.class, args);
    }
}
