package com.ferreteria.ventas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Microservicio de Ventas y Devoluciones — Ferretería Molina.
 * Puerto: 8092
 *
 * @EnableFeignClients activa los clientes declarativos que llaman a:
 *   - ms-productos (ProductoClient) → verificar/descontar stock
 *   - ms-clientes  (ClienteClient)  → verificar existencia del cliente
 *
 * Mismo patrón que el T1: @EnableFeignClients en la clase principal.
 */
@SpringBootApplication
@EnableFeignClients
public class MsVentasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsVentasApplication.class, args);
    }
}
