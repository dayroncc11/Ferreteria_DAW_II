package com.ferreteria.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway — Ferretería Molina.
 *
 * Puerto: 8080 (igual que el monolito anterior, Angular no necesita cambios).
 *
 * Responsabilidades:
 *  1. Enrutar peticiones del Angular a los microservicios correctos.
 *  2. Validar JWT en todas las rutas protegidas (filtro global).
 *  3. Centralizar la configuración CORS.
 *  4. Balancear carga usando Eureka (lb://ms-auth, lb://ms-ventas...).
 */
@SpringBootApplication
public class MsGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsGatewayApplication.class, args);
    }
}
