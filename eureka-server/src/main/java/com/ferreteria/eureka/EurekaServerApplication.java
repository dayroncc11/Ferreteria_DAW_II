package com.ferreteria.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Servidor de registro y descubrimiento de servicios — Ferretería Molina.
 *
 * @EnableEurekaServer activa el servidor Eureka.
 *                     Todos los microservicios que tengan la dependencia
 *                     eureka-client se registran aquí al arrancar.
 *
 * Dashboard disponible en: http://localhost:8761
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
