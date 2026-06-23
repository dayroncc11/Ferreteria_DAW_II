package com.ferreteria.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.List;

/**
 * Filtro JWT global del API Gateway.
 * Valida el token ANTES de enrutar al microservicio destino.
 * Si el token es válido, inyecta X-User-Email y X-User-Role como headers.
 */
@Slf4j
@Component
public class JwtAuthGatewayFilter implements GlobalFilter, Ordered {

    // Rutas públicas que NO requieren token JWT
    private static final List<String> PUBLIC_PATHS = List.of(
        "/api/auth/login",
        "/api/auth/register",
        "/gateway/health"
    );

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path   = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        log.debug("Gateway → {} {}", method, path);

        // 1. Preflight CORS — siempre pasar
        if (HttpMethod.OPTIONS.equals(method)) {
            return chain.filter(exchange);
        }

        // 2. Rutas públicas — sin validación
        if (isPublicPath(path)) {
            log.debug("  Ruta pública — sin validación JWT");
            return chain.filter(exchange);
        }

        // 3. Extraer Bearer token
        List<String> authHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null || authHeaders.isEmpty() || !authHeaders.get(0).startsWith("Bearer ")) {
            log.warn("  Sin token en: {} {}", method, path);
            return unauthorized(exchange);
        }

        String token = authHeaders.get(0).substring(7);

        // 4. Validar JWT
        try {
            Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

            String correo = claims.getSubject();
            String rol    = claims.get("rol", String.class);
            log.debug("  Token válido → usuario={} rol={}", correo, rol);

            // 5. Propagar info del usuario como headers a los microservicios
            ServerHttpRequest enriched = request.mutate()
                .header("X-User-Email", correo)
                .header("X-User-Role", rol != null ? rol : "")
                .build();

            return chain.filter(exchange.mutate().request(enriched).build());

        } catch (Exception ex) {
            log.warn("  Token inválido: {}", ex.getMessage());
            return unauthorized(exchange);
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
