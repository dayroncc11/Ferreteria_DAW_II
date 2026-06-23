# ══════════════════════════════════════════════════════════════
#  Dockerfile — eureka-server
#  Construcción en 2 etapas:
#   1. BUILD: Maven compila y empaqueta el .jar
#   2. RUNTIME: Solo JRE + el .jar (imagen más liviana)
# ══════════════════════════════════════════════════════════════

# ── Etapa 1: Compilación ──────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copiar pom.xml primero (caching de dependencias)
COPY pom.xml .

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -q 2>/dev/null || true

# Copiar código fuente y compilar
COPY src ./src
RUN mvn package -DskipTests -q

# ── Etapa 2: Runtime ─────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar solo el .jar generado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8761

# -Djava.security.egd acelera el arranque en Linux
ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]
